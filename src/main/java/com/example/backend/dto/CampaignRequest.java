package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CampaignRequest(
        @NotNull Long productId,
        @NotBlank String theme,
        @NotBlank String offerText,
        LocalDate startDate,
        LocalDate endDate,
        String subject
) {
}
