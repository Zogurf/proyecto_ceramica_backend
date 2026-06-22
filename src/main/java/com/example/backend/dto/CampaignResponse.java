package com.example.backend.dto;

public record CampaignResponse(
        int recipients,
        String subject,
        String htmlPreview,
        String categoryName,
        java.util.List<ProductSummaryDTO> products
) {
}
