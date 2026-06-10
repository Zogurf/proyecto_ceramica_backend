package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleTokenInfoResponse(
        @JsonProperty("sub")
        String subject,
        String email,
        @JsonProperty("email_verified")
        String emailVerified,
        String name,
        @JsonProperty("given_name")
        String givenName,
        @JsonProperty("family_name")
        String familyName,
        String picture,
        String aud
) {
    public boolean isEmailVerified() {
        return "true".equalsIgnoreCase(emailVerified);
    }
}
