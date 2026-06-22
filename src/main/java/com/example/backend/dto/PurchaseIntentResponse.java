package com.example.backend.dto;

import java.time.LocalDateTime;

public record PurchaseIntentResponse(
        Long id,
        Long productId,
        String productName,
        String customerName,
        String email,
        String categoryName,
        String interactionType,
        LocalDateTime viewedAt
) {
}
