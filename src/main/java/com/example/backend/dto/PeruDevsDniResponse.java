package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PeruDevsDniResponse(
        boolean estado,
        String mensaje,
        Resultado resultado
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Resultado(
            @JsonProperty("id")
            String dni,
            @JsonProperty("nombres")
            String firstName,
            @JsonProperty("apellido_paterno")
            String lastName,
            @JsonProperty("apellido_materno")
            String motherLastName,
            @JsonProperty("fecha_nacimiento")
            String birthDate,
            @JsonProperty("codigo_verificacion")
            String verificationCode,
            @JsonProperty("nombre_completo")
            String fullName
    ) {
    }
}
