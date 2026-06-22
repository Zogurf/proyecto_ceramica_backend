package com.example.backend.dto;

import java.util.List;

public record CategoryIntentAnalyticsResponse(
        Long categoryId,
        String categoryName,
        long interactions,
        long uniqueCustomers,
        List<ProductInteractionResponse> topProducts
) {
    public record ProductInteractionResponse(Long productId, String productName, long interactions) {}
}
