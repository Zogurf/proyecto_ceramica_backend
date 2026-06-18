package com.example.backend.dto;

import jakarta.validation.constraints.NotNull;

public record PurchaseIntentRequest(
        @NotNull Long productId
) {
}
