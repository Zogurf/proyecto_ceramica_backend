package com.example.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String status,
        LocalDateTime registerDate,
        Double total,
        String customerName,
        List<OrderItemResponse> items
) {
}
