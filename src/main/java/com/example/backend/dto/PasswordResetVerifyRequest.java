package com.example.backend.dto;

public record PasswordResetVerifyRequest(
        String email,
        String code
) {
}
