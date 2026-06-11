package com.example.backend.dto;

public record PasswordResetConfirmRequest(
        String email,
        String code,
        String newPassword
) {
}
