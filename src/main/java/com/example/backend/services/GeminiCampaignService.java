package com.example.backend.services;

import com.example.backend.dto.CampaignRequest;
import com.example.backend.dto.CampaignResponse;
import com.example.backend.models.Product;
import com.example.backend.models.PurchaseIntent;
import com.example.backend.models.User;
import com.example.backend.repositories.ProductRepository;
import com.example.backend.repositories.PurchaseIntentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiCampaignService {
    private final ProductRepository productRepository;
    private final PurchaseIntentRepository purchaseIntentRepository;
    private final EmailService emailService;

    @Value("${gemini.api-key:}")
    private String geminiApiKey;

    @Value("${gemini.model:gemini-2.5-flash}")
    private String geminiModel;

    private final RestClient restClient = RestClient.create("https://generativelanguage.googleapis.com");

    public CampaignResponse sendCampaign(CampaignRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        LocalDate start = request.startDate() != null ? request.startDate() : LocalDate.now().minusDays(7);
        LocalDate end = request.endDate() != null ? request.endDate() : LocalDate.now();

        List<PurchaseIntent> intentions = purchaseIntentRepository.findCampaignAudience(
                product.getId(),
                start.atStartOfDay(),
                end.atTime(LocalTime.MAX)
        );

        Map<String, User> audience = new LinkedHashMap<>();
        for (PurchaseIntent intent : intentions) {
            audience.putIfAbsent(intent.getUser().getEmail(), intent.getUser());
        }

        if (audience.isEmpty()) {
            throw new RuntimeException("No hay clientes con intencion de compra para ese producto en el rango seleccionado");
        }

        String subject = StringUtils.hasText(request.subject())
                ? request.subject()
                : "Oferta especial en " + product.getName();
        String htmlTemplate = generateHtmlTemplate(request, product);

        audience.values().forEach(user -> {
            String customerName = user.getPersona() != null ? user.getPersona().getName() : user.getEmail();
            String personalizedHtml = htmlTemplate
                    .replace("{{customerName}}", escapeHtml(customerName))
                    .replace("{{productName}}", escapeHtml(product.getName()))
                    .replace("{{offerText}}", escapeHtml(request.offerText()))
                    .replace("{{price}}", "S/ " + product.getPrice());
            emailService.sendMarketingEmail(user.getEmail(), subject, personalizedHtml);
        });

        return new CampaignResponse(audience.size(), subject, htmlTemplate);
    }

    private String generateHtmlTemplate(CampaignRequest request, Product product) {
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            return fallbackTemplate(request.theme());
        }

        String prompt = """
                Genera solo el contenido HTML para el cuerpo de un correo promocional de ecommerce.
                No incluyas markdown, explicaciones ni etiquetas script.
                Usa estilos inline compatibles con Gmail.
                Debe estar en espanol, tono cercano y artesanal.
                Debe incluir estos placeholders exactamente:
                {{customerName}}, {{productName}}, {{offerText}}, {{price}}.
                Tema de campana: %s.
                Producto: %s.
                Oferta: %s.
                """.formatted(request.theme(), product.getName(), request.offerText());

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", prompt))
                )),
                "generationConfig", Map.of(
                        "temperature", 0.7,
                        "maxOutputTokens", 1800
                )
        );

        try {
            Map<?, ?> response = restClient.post()
                    .uri("/v1beta/models/{model}:generateContent?key={key}", geminiModel, geminiApiKey)
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            return extractText(response);
        } catch (Exception ex) {
            return fallbackTemplate(request.theme());
        }
    }

    private String extractText(Map<?, ?> response) {
        if (response == null) {
            return fallbackTemplate("Oferta especial");
        }

        List<?> candidates = (List<?>) response.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            return fallbackTemplate("Oferta especial");
        }

        Map<?, ?> candidate = (Map<?, ?>) candidates.get(0);
        Map<?, ?> content = (Map<?, ?>) candidate.get("content");
        List<?> parts = content != null ? (List<?>) content.get("parts") : null;
        if (parts == null || parts.isEmpty()) {
            return fallbackTemplate("Oferta especial");
        }

        Object text = ((Map<?, ?>) parts.get(0)).get("text");
        return text != null ? text.toString() : fallbackTemplate("Oferta especial");
    }

    private String fallbackTemplate(String theme) {
        return """
                <div style="background:#f6efe8;padding:28px 16px;font-family:Arial,Helvetica,sans-serif;color:#2d211b;">
                  <div style="max-width:560px;margin:0 auto;background:#fffdfb;border:1px solid #eadbd0;border-radius:20px;overflow:hidden;">
                    <div style="background:#2d211b;padding:24px;">
                      <p style="margin:0;color:#d9b49d;font-size:12px;font-weight:700;letter-spacing:1px;text-transform:uppercase;">%s</p>
                      <h1 style="margin:8px 0 0;color:white;font-size:28px;">Hola {{customerName}}</h1>
                    </div>
                    <div style="padding:24px;">
                      <p style="font-size:16px;line-height:1.7;margin:0 0 16px;">Vimos que te intereso <strong>{{productName}}</strong> y preparamos una oferta especial para ti.</p>
                      <p style="font-size:20px;font-weight:700;color:#7f4c31;margin:0 0 16px;">{{offerText}}</p>
                      <p style="font-size:15px;line-height:1.7;color:#70594b;margin:0;">Precio actual: <strong>{{price}}</strong>. Responde a este correo o visita la tienda para aprovecharlo.</p>
                    </div>
                  </div>
                </div>
                """.formatted(escapeHtml(theme));
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
