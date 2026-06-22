package com.example.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record CheckoutRequest(
        @NotBlank String customerName,
        @NotBlank @Email String customerEmail,
        @NotBlank String shippingAddress,
        String shippingReference,
        @NotBlank @Pattern(regexp = "^[0-9+()\\s-]{7,20}$", message = "Numero de celular no valido") String customerPhone,
        @NotEmpty List<@Valid CheckoutItemRequest> items
) {
}
