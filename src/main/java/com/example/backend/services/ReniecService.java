package com.example.backend.services;

import com.example.backend.dto.PeruDevsDniResponse;
import com.example.backend.dto.ReniecPersonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
@RequiredArgsConstructor
public class ReniecService {

    private final ObjectMapper objectMapper;

    @Value("${perudevs.api.url:https://api.perudevs.com/api/v1/dni/complete}")
    private String peruDevsApiUrl;

    @Value("${perudevs.api.key:}")
    private String peruDevsApiKey;

    public ReniecPersonResponse findByDni(String dni) {
        if (peruDevsApiKey == null || peruDevsApiKey.isBlank()) {
            throw new RuntimeException("La key de PeruDevs no está configurada en application.properties");
        }

        RestClient client = RestClient.builder()
                .baseUrl(peruDevsApiUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();

        try {
            String responseBody = client.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("document", dni)
                            .queryParam("key", peruDevsApiKey)
                            .build())
                    .retrieve()
                    .body(String.class);

            PeruDevsDniResponse apiResponse = objectMapper.readValue(responseBody, PeruDevsDniResponse.class);
            if (apiResponse == null || !apiResponse.estado() || apiResponse.resultado() == null) {
                throw new RuntimeException("No se encontraron datos para el DNI proporcionado");
            }

            PeruDevsDniResponse.Resultado result = apiResponse.resultado();
            if (result.dni() == null || result.dni().isBlank()) {
                throw new RuntimeException("No se encontraron datos para el DNI proporcionado");
            }

            return new ReniecPersonResponse(
                    result.dni(),
                    result.firstName(),
                    result.lastName(),
                    result.motherLastName(),
                    result.birthDate(),
                    result.verificationCode(),
                    result.fullName()
            );
        } catch (RestClientException ex) {
            throw new RuntimeException("No se pudo consultar la API de PeruDevs", ex);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo procesar la respuesta de PeruDevs", ex);
        }
    }
}
