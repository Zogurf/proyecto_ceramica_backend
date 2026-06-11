package com.example.backend.services;

import com.example.backend.dto.GoogleTokenInfoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    private final ObjectMapper objectMapper;

    @Value("${google.auth.client-id:}")
    private String googleClientId;

    @Value("${google.auth.token-info-url:https://oauth2.googleapis.com/tokeninfo}")
    private String googleTokenInfoUrl;

    public GoogleTokenInfoResponse verifyCredential(String credential) {
        if (credential == null || credential.isBlank()) {
            throw new RuntimeException("No se recibió la credencial de Google");
        }

        if (googleClientId == null || googleClientId.isBlank()) {
            throw new RuntimeException("El Google Client ID no está configurado en application.properties");
        }

        RestClient client = RestClient.builder()
                .baseUrl(googleTokenInfoUrl)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .build();

        try {
            String responseBody = client.get()
                    .uri(uriBuilder -> uriBuilder.queryParam("id_token", credential).build())
                    .retrieve()
                    .body(String.class);

            GoogleTokenInfoResponse tokenInfo = objectMapper.readValue(responseBody, GoogleTokenInfoResponse.class);
            if (tokenInfo == null || tokenInfo.subject() == null || tokenInfo.subject().isBlank()) {
                throw new RuntimeException("Google no devolvió un usuario válido");
            }

            if (!googleClientId.equals(tokenInfo.aud())) {
                throw new RuntimeException("La credencial de Google no pertenece a esta aplicación");
            }

            if (!tokenInfo.isEmailVerified()) {
                throw new RuntimeException("La cuenta de Google no tiene el correo verificado");
            }

            return tokenInfo;
        } catch (RestClientException ex) {
            throw new RuntimeException("No se pudo validar la credencial con Google", ex);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo procesar la respuesta de Google", ex);
        }
    }
}
