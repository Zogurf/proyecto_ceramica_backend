package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CampaignRequest(
        Long categoryId,
        @NotBlank String offerText,
        LocalDate startDate,
        LocalDate endDate,
        @NotBlank String subject,
        String htmlTemplate
) {
}
