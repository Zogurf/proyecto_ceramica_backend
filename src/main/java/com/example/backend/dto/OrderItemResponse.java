package com.example.backend.dto;

public record OrderItemResponse(
        Long productId,
        String productName,
        String imageUrl,
        Integer quantity,
        Double unitPrice,
        Double subtotal,
        String sizeName,
        String sizeDimension
) {
}
