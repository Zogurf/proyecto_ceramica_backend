package com.example.backend.dto;

// guava
import com.google.common.net.InternetDomainName;

public record RegisterRequest(
        String dni,
        String firstName,
        String lastName,
        String motherLastName,
        String birthDate,
        String email,
        String password
) {

    public boolean validateEmail(String email) {
        if (email == null || !email.contains("@")) return false;

        String domain = email.substring(email.indexOf("@") + 1);

        return InternetDomainName.isValid(domain) && InternetDomainName.from(domain).isUnderPublicSuffix();
    }
}
