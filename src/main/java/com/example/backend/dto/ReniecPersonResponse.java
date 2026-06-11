package com.example.backend.dto;

public record ReniecPersonResponse(
        String dni,
        String firstName,
        String lastName,
        String motherLastName,
        String birthDate,
        String verificationCode,
        String fullName
) {
}
