package com.example.backend.controllers;

import com.example.backend.dto.AdminProductResponse;
import com.example.backend.dto.AdminUserResponse;
import com.example.backend.dto.ProductRequest;
import com.example.backend.models.Order;
import com.example.backend.models.OrderItem;
import com.example.backend.repositories.OrderItemRepository;
import com.example.backend.repositories.OrderRepository;
import com.example.backend.repositories.PersonaRepository;
import com.example.backend.repositories.ProductRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final ProductService productService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PersonaRepository personaRepository;
    private final OrderItemRepository orderItemRepository;

    @GetMapping("/test")
    public String test() {
        return "Admin funcionando";
    }

    @GetMapping("/users")
    public List<AdminUserResponse> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new AdminUserResponse(
                        user.getId(),
                        user.getPersona() != null ? user.getPersona().getName() : "Administrador",
                        user.getEmail(),
                        user.getRole() != null ? user.getRole().getName() : "USER"
                )).toList();
    }

    // Rutas de Producto
    @GetMapping("/products")
    public List<AdminProductResponse> getProducts() {
        return productService.getAllAdminProducts();
    }

    @PostMapping("/products")
    public AdminProductResponse createProduct(
            @RequestBody ProductRequest request
    ) {
        return productService.createProduct(request);
    }

    @PutMapping("/products/{id}")
    public AdminProductResponse updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequest request
    ) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/products/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    // 1. KPIs de Reportes
    @GetMapping("/reports/kpis")
    public Map<String, Object> getKPIs(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long categoryId
    ) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        try {
            if (startDate != null && !startDate.isEmpty()) {
                start = LocalDate.parse(startDate).atStartOfDay();
            }
            if (endDate != null && !endDate.isEmpty()) {
                end = LocalDate.parse(endDate).atTime(23, 59, 59);
            }
        } catch (Exception ex) {
            // Ignorar errores de parsing
        }

        String statusFilter = (status == null || status.isEmpty() || "ALL".equalsIgnoreCase(status)) ? null : status;
        List<Order> orders = orderRepository.findFilteredOrders(statusFilter, start, end, categoryId);

        double totalSales = orders.stream()
                .filter(o -> !"CANCELADO".equalsIgnoreCase(o.getStatus()))
                .mapToDouble(Order::getTotal)
                .sum();

        long clientCount = userRepository.findAll().stream()
                .filter(u -> u.getRole() != null && "CLIENTE".equalsIgnoreCase(u.getRole().getName()))
                .count();

        long activeProducts = productRepository.count();

        return Map.of(
                "totalOrders", orders.size(),
                "totalSales", totalSales,
                "registeredClients", clientCount,
                "activeProducts", activeProducts
        );
    }

    // 2. Alerta de Bajo Stock
    @GetMapping("/reports/low-stock")
    public List<Map<String, Object>> getLowStock() {
        return productRepository.findAll().stream()
                .filter(p -> p.getStock() != null && p.getStock() < 15)
                .map(p -> Map.<String, Object>of(
                        "id", p.getId(),
                        "name", p.getName(),
                        "stock", p.getStock(),
                        "category", p.getCategory() != null ? p.getCategory().getName() : "Sin Categoría"
                ))
                .toList();
    }

    // 3. Productos por Categoría
    @GetMapping("/reports/category-products")
    public Map<String, Long> getProductsByCategory() {
        return productRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCategory() != null ? p.getCategory().getName() : "Sin Categoría",
                        Collectors.counting()
                ));
    }

    // 4. Ventas Mensuales (Gráfico de barras)
    @GetMapping("/reports/sales-monthly")
    public List<Map<String, Object>> getSalesMonthly(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long categoryId
    ) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        try {
            if (startDate != null && !startDate.isEmpty()) {
                start = LocalDate.parse(startDate).atStartOfDay();
            }
            if (endDate != null && !endDate.isEmpty()) {
                end = LocalDate.parse(endDate).atTime(23, 59, 59);
            }
        } catch (Exception ex) {
            // Ignorar errores de parsing
        }

        String statusFilter = (status == null || status.isEmpty() || "ALL".equalsIgnoreCase(status)) ? null : status;
        List<Order> orders = orderRepository.findFilteredOrders(statusFilter, start, end, categoryId);

        String[] months = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
        double[] sales = new double[12];

        for (var order : orders) {
            if ("CANCELADO".equalsIgnoreCase(order.getStatus())) continue;
            if (order.getRegisterDate() != null) {
                int monthIndex = order.getRegisterDate().getMonthValue() - 1;
                if (monthIndex >= 0 && monthIndex < 12) {
                    sales[monthIndex] += order.getTotal();
                }
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            result.add(Map.of(
                    "month", months[i],
                    "sales", sales[i]
            ));
        }
        return result;
    }

    // 5. Top 5 Productos más vendidos
    @GetMapping("/reports/top-products")
    public List<Map<String, Object>> getTopProducts(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long categoryId
    ) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        try {
            if (startDate != null && !startDate.isEmpty()) {
                start = LocalDate.parse(startDate).atStartOfDay();
            }
            if (endDate != null && !endDate.isEmpty()) {
                end = LocalDate.parse(endDate).atTime(23, 59, 59);
            }
        } catch (Exception ex) {
            // Ignorar errores de parsing
        }

        String statusFilter = (status == null || status.isEmpty() || "ALL".equalsIgnoreCase(status)) ? null : status;
        List<Order> orders = orderRepository.findFilteredOrders(statusFilter, start, end, categoryId);

        Set<Integer> validOrderIds = orders.stream()
                .filter(o -> !"CANCELADO".equalsIgnoreCase(o.getStatus()))
                .map(Order::getId)
                .collect(Collectors.toSet());

        List<OrderItem> allItems = orderItemRepository.findAll();
        Map<String, Integer> productQuantities = new HashMap<>();
        
        for (OrderItem item : allItems) {
            if (item.getOrder() != null && validOrderIds.contains(item.getOrder().getId())) {
                if (item.getProduct() != null && item.getProduct().getName() != null) {
                    String productName = item.getProduct().getName();
                    productQuantities.put(productName, productQuantities.getOrDefault(productName, 0) + item.getQuantity());
                }
            }
        }

        return productQuantities.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(5)
                .map(e -> Map.<String, Object>of(
                        "name", e.getKey(),
                        "quantity", e.getValue()
                    ))
                .toList();
    }

    // 6. Pedidos filtrados
    @GetMapping("/orders")
    public List<Map<String, Object>> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long categoryId
    ) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        try {
            if (startDate != null && !startDate.isEmpty()) {
                start = LocalDate.parse(startDate).atStartOfDay();
            }
            if (endDate != null && !endDate.isEmpty()) {
                end = LocalDate.parse(endDate).atTime(23, 59, 59);
            }
        } catch (Exception ex) {
            // Ignorar errores de parsing
        }

        String statusFilter = (status == null || status.isEmpty() || "ALL".equalsIgnoreCase(status)) ? null : status;
        List<Order> orders = orderRepository.findFilteredOrders(statusFilter, start, end, categoryId);

        return orders.stream()
                .sorted((o1, o2) -> {
                    if (o1.getRegisterDate() == null && o2.getRegisterDate() == null) return 0;
                    if (o1.getRegisterDate() == null) return 1;
                    if (o2.getRegisterDate() == null) return -1;
                    return o2.getRegisterDate().compareTo(o1.getRegisterDate());
                })
                .map(o -> Map.<String, Object>of(
                        "id", o.getId(),
                        "client", o.getPersona() != null && o.getPersona().getName() != null ? o.getPersona().getName() : "Sin Cliente",
                        "date", o.getRegisterDate() != null ? o.getRegisterDate().toString() : "",
                        "status", o.getStatus() != null ? o.getStatus() : "",
                        "total", o.getTotal() != null ? o.getTotal() : 0.0
                ))
                .toList();
    }
}
