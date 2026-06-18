package com.example.backend.controllers;

import com.example.backend.dto.CheckoutRequest;
import com.example.backend.dto.CheckoutResponse;
import com.example.backend.dto.OrderResponse;
import com.example.backend.services.CheckoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
