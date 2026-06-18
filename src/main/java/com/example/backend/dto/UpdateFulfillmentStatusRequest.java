package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateFulfillmentStatusRequest(
        @NotBlank String fulfillmentStatus
) {
}
