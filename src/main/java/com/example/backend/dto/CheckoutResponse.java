package com.example.backend.dto;

public record CheckoutResponse(
        String checkoutUrl,
        String sessionId,
        Long orderId
) {
}
