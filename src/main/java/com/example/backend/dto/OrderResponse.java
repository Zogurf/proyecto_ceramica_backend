package com.example.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String status,
        String fulfillmentStatus,
        LocalDateTime registerDate,
        Double total,
        String customerName,
        String customerEmail,
        String shippingAddress,
        String shippingReference,
        List<OrderItemResponse> items
) {
}
