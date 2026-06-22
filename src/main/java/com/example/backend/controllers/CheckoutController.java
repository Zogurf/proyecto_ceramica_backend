package com.example.backend.controllers;

import com.example.backend.dto.CheckoutRequest;
import com.example.backend.dto.CheckoutResponse;
import com.example.backend.dto.OrderResponse;
import com.example.backend.services.CheckoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {
    private final CheckoutService checkoutService;

    @PostMapping("/session")
    public CheckoutResponse createCheckoutSession(@Valid @RequestBody CheckoutRequest request) {
        return checkoutService.createCheckoutSession(request);
    }

    @GetMapping("/confirm")
    public OrderResponse confirmCheckout(@RequestParam String sessionId) {
        return checkoutService.confirmCheckoutSession(sessionId);
    }

    @GetMapping("/orders")
    public List<OrderResponse> getMyOrders() {
        return checkoutService.getMyOrders();
    }

    @GetMapping("/orders/{orderId}")
    public OrderResponse getMyOrder(@PathVariable Long orderId) {
        return checkoutService.getMyOrder(orderId);
    }

    @PostMapping("/orders/{orderId}/payment-session")
    public CheckoutResponse retryPayment(@PathVariable Long orderId) {
        return checkoutService.retryPayment(orderId);
    }
}
