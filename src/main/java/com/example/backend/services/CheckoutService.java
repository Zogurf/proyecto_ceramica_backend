package com.example.backend.services;

import com.example.backend.dto.CheckoutItemRequest;
import com.example.backend.dto.CheckoutRequest;
import com.example.backend.dto.CheckoutResponse;
import com.example.backend.dto.OrderItemResponse;
import com.example.backend.dto.OrderResponse;
import com.example.backend.dto.UpdateFulfillmentStatusRequest;
import com.example.backend.models.Order;
import com.example.backend.models.OrderItem;
import com.example.backend.models.Payment;
import com.example.backend.models.Product;
import com.example.backend.models.User;
import com.example.backend.repositories.OrderRepository;
import com.example.backend.repositories.PaymentRepository;
import com.example.backend.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CheckoutService {
    private final CurrentUserService currentUserService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final EmailService emailService;

    @Value("${stripe.secret-key:}")
    private String stripeSecretKey;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    private final RestClient restClient = RestClient.create("https://api.stripe.com");

    @Transactional
    public CheckoutResponse createCheckoutSession(CheckoutRequest request) {
        if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
            throw new RuntimeException("Configura stripe.secret-key antes de crear pagos");
        }

        User user = currentUserService.getCurrentUser();
        Order order = new Order();
        order.setStatus("PENDING");
        order.setFulfillmentStatus("PENDING_SHIPMENT");
        order.setRegisterDate(LocalDateTime.now());
        order.setPersona(user.getPersona());
        order.setCustomerName(request.customerName().trim());
        order.setCustomerEmail(request.customerEmail().trim().toLowerCase());
        order.setShippingAddress(request.shippingAddress().trim());
        order.setShippingReference(request.shippingReference());
        order.setCustomerPhone(request.customerPhone().trim());

        double total = 0;
        for (CheckoutItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (!product.isStatus() || product.getStock() < itemRequest.quantity()) {
                throw new RuntimeException("Stock insuficiente para " + product.getName());
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemRequest.quantity());
            item.setUnitPrice(product.getPrice().doubleValue());
            item.setSizeName(itemRequest.sizeName());
            item.setSizeDimension(itemRequest.sizeDimension());
            order.getItems().add(item);
            total += product.getPrice().doubleValue() * itemRequest.quantity();
        }

        order.setTotal(roundMoney(total));
        Order savedOrder = orderRepository.save(order);

        Map<String, Object> session = createStripeSession(savedOrder);
        savedOrder.setStripeSessionId((String) session.get("id"));
        orderRepository.save(savedOrder);

        return new CheckoutResponse((String) session.get("url"), savedOrder.getStripeSessionId(), savedOrder.getId());
    }

    @Transactional
    public OrderResponse confirmCheckoutSession(String sessionId) {
        Order order = orderRepository.findByStripeSessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if ("PAID".equals(order.getStatus())) {
            return toResponse(order);
        }

        Map<?, ?> session = retrieveStripeSession(sessionId);
        if (!"paid".equals(session.get("payment_status"))) {
            throw new RuntimeException("Stripe aun no confirma el pago");
        }

        order.setStatus("PAID");
        order.getItems().forEach(item -> {
            Product product = item.getProduct();
            product.setStock(Math.max(0, product.getStock() - item.getQuantity()));
            productRepository.save(product);
        });
        orderRepository.save(order);

        if (paymentRepository.findByStripeSessionId(sessionId).isEmpty()) {
            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setMethod("STRIPE");
            payment.setStatus("PAID");
            payment.setAmount(order.getTotal());
            payment.setStripeSessionId(sessionId);
            payment.setPaymentDate(LocalDateTime.now());
            paymentRepository.save(payment);
        }

        emailService.sendPurchaseConfirmation(order);

        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAdminOrders() {
        return orderRepository.findAllByOrderByRegisterDateDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders() {
        User user = currentUserService.getCurrentUser();
        return orderRepository.findByPersonaIdOrderByRegisterDateDesc(user.getPersona().getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getMyOrder(Long orderId) {
        User user = currentUserService.getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (order.getPersona() == null || !order.getPersona().getId().equals(user.getPersona().getId())) {
            throw new RuntimeException("No tienes acceso a este pedido");
        }

        return toResponse(order);
    }

    @Transactional
    public CheckoutResponse retryPayment(Long orderId) {
        User user = currentUserService.getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        if (order.getPersona() == null || !order.getPersona().getId().equals(user.getPersona().getId())) {
            throw new RuntimeException("No tienes acceso a este pedido");
        }
        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("Este pedido ya fue pagado");
        }

        if (order.getStripeSessionId() != null) {
            Map<?, ?> currentSession = retrieveStripeSession(order.getStripeSessionId());
            if ("open".equals(currentSession.get("status")) && currentSession.get("url") != null) {
                return new CheckoutResponse((String) currentSession.get("url"), order.getStripeSessionId(), order.getId());
            }
        }

        order.getItems().forEach(item -> {
            if (!item.getProduct().isStatus() || item.getProduct().getStock() < item.getQuantity()) {
                throw new RuntimeException("Stock insuficiente para " + item.getProduct().getName());
            }
        });
        Map<String, Object> session = createStripeSession(order);
        order.setStripeSessionId((String) session.get("id"));
        orderRepository.save(order);
        return new CheckoutResponse((String) session.get("url"), order.getStripeSessionId(), order.getId());
    }

    @Transactional
    public OrderResponse updateFulfillmentStatus(Long orderId, UpdateFulfillmentStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        String status = request.fulfillmentStatus().trim().toUpperCase();
        if (!List.of("PENDING_SHIPMENT", "SHIPPED").contains(status)) {
            throw new RuntimeException("Estado de envio no valido");
        }

        order.setFulfillmentStatus(status);
        return toResponse(orderRepository.save(order));
    }

    private Map<String, Object> createStripeSession(Order order) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("mode", "payment");
        form.add("success_url", frontendUrl + "/checkout/success?session_id={CHECKOUT_SESSION_ID}");
        form.add("cancel_url", frontendUrl + "/carrito");
        form.add("customer_email", order.getCustomerEmail());
        form.add("metadata[order_id]", String.valueOf(order.getId()));

        for (int index = 0; index < order.getItems().size(); index++) {
            OrderItem item = order.getItems().get(index);
            Product product = item.getProduct();
            form.add("line_items[" + index + "][quantity]", String.valueOf(item.getQuantity()));
            form.add("line_items[" + index + "][price_data][currency]", "pen");
            form.add("line_items[" + index + "][price_data][unit_amount]", toCents(product.getPrice()).toString());
            form.add("line_items[" + index + "][price_data][product_data][name]", product.getName());
        }

        return restClient.post()
                .uri("/v1/checkout/sessions")
                .headers(headers -> headers.setBearerAuth(stripeSecretKey))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(Map.class);
    }

    private Map<?, ?> retrieveStripeSession(String sessionId) {
        return restClient.get()
                .uri("/v1/checkout/sessions/{sessionId}", sessionId)
                .headers(headers -> headers.setBearerAuth(stripeSecretKey))
                .retrieve()
                .body(Map.class);
    }

    private Long toCents(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).longValue();
    }

    private double roundMoney(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getFulfillmentStatus() != null ? order.getFulfillmentStatus() : "PENDING_SHIPMENT",
                order.getRegisterDate(),
                order.getTotal(),
                order.getCustomerName() != null ? order.getCustomerName() : resolveCustomerName(order),
                order.getCustomerEmail() != null ? order.getCustomerEmail() : "",
                order.getShippingAddress() != null ? order.getShippingAddress() : "",
                order.getShippingReference(),
                order.getCustomerPhone() != null ? order.getCustomerPhone() : "",
                order.getItems().stream().map(this::toItemResponse).toList()
        );
    }

    private String resolveCustomerName(Order order) {
        return order.getPersona() != null ? order.getPersona().getName() : "Cliente";
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getImageUrl(),
                item.getQuantity(),
                item.getUnitPrice(),
                roundMoney(item.getUnitPrice() * item.getQuantity()),
                item.getSizeName(),
                item.getSizeDimension()
        );
    }
}
