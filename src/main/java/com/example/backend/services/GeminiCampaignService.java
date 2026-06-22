package com.example.backend.services;

import com.example.backend.dto.CampaignRequest;
import com.example.backend.dto.CampaignResponse;
import com.example.backend.dto.ProductSummaryDTO;
import com.example.backend.models.Category;
import com.example.backend.models.Product;
import com.example.backend.models.PurchaseIntent;
import com.example.backend.models.User;
import com.example.backend.repositories.CategoryRepository;
import com.example.backend.repositories.ProductRepository;
import com.example.backend.repositories.PurchaseIntentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiCampaignService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PurchaseIntentRepository purchaseIntentRepository;
    private final EmailService emailService;

    public CampaignResponse previewCampaign(CampaignRequest request) {
        CampaignAudience data = buildCampaignAudience(request);
        return response(data, request, false);
    }

    public CampaignResponse sendCampaign(CampaignRequest request) {
        CampaignAudience data = buildCampaignAudience(request);
        CampaignResponse response = response(data, request, true);
        return response;
    }

    private CampaignResponse response(CampaignAudience data, CampaignRequest request, boolean send) {
        String subject = StringUtils.hasText(request.subject())
                ? request.subject()
                : "Lo más buscado en " + data.category().getName();
        String template = buildStoreTemplate();
        String productCards = buildProductCards(data.products());

        if (send) {
            data.audience().values().forEach(user -> {
                String customerName = user.getPersona() != null ? user.getPersona().getName() : user.getEmail();
                String html = template
                        .replace("{{emailTitle}}", escapeHtml(subject))
                        .replace("{{customerName}}", escapeHtml(customerName))
                        .replace("{{categoryName}}", escapeHtml(data.category().getName()))
                        .replace("{{offerText}}", escapeHtml(request.offerText()))
                        .replace("{{productCards}}", productCards);
                emailService.sendMarketingEmail(user.getEmail(), subject, html);
            });
        }

        List<ProductSummaryDTO> products = data.products().stream()
                .map(p -> new ProductSummaryDTO(p.getId(), p.getName(), p.getPrice(), p.getImageUrl()))
                .toList();
        return new CampaignResponse(data.audience().size(), subject, template,
                data.category().getName(), products);
    }

    private CampaignAudience buildCampaignAudience(CampaignRequest request) {
        if (request.categoryId() == null) {
            throw new RuntimeException("Selecciona una categoria");
        }
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
        LocalDate start = request.startDate() != null ? request.startDate() : LocalDate.now().minusDays(30);
        LocalDate end = request.endDate() != null ? request.endDate() : LocalDate.now();
        List<PurchaseIntent> intentions = purchaseIntentRepository.findCategoryCampaignAudience(
                category.getId(), start.atStartOfDay(), end.atTime(LocalTime.MAX));

        Map<Long, Long> scores = new LinkedHashMap<>();
        Map<String, User> audience = new LinkedHashMap<>();
        for (PurchaseIntent intent : intentions) {
            long weight = switch (intent.getInteractionType() == null ? "VIEW" : intent.getInteractionType()) {
                case "FAVORITE" -> 4;
                case "CART" -> 3;
                default -> 1;
            };
            scores.merge(intent.getProduct().getId(), weight, Long::sum);
            audience.putIfAbsent(intent.getUser().getEmail(), intent.getUser());
        }
        if (audience.isEmpty()) {
            throw new RuntimeException("No hay clientes con interacciones en esa categoria y rango");
        }

        List<Product> products = productRepository.findByStatusTrue().stream()
                .filter(p -> p.getCategory() != null && p.getCategory().getId().equals(category.getId()))
                .sorted(Comparator.comparingLong((Product p) -> scores.getOrDefault(p.getId(), 0L)).reversed())
                .limit(3)
                .toList();
        return new CampaignAudience(category, products, audience);
    }

    private String buildProductCards(List<Product> products) {
        StringBuilder html = new StringBuilder();
        for (Product product : products) {
            html.append("<div style=\"padding:14px 0;border-bottom:1px solid #eadbd0\"><strong>")
                    .append(escapeHtml(product.getName())).append("</strong><span style=\"float:right;color:#7f4c31\">S/ ")
                    .append(product.getPrice()).append("</span></div>");
        }
        return html.toString();
    }

    private String buildStoreTemplate() {
        return """
                <div style="background:#f6efe8;padding:28px 16px;font-family:Arial;color:#2d211b">
                  <div style="max-width:560px;margin:auto;background:#fffdfb;border-radius:20px;overflow:hidden">
                    <div style="background:#2d211b;padding:24px;color:white"><h1>{{emailTitle}}</h1></div>
                    <div style="padding:24px"><p>Hola {{customerName}}, seleccionamos lo más buscado en <strong>{{categoryName}}</strong>.</p>
                      <p style="font-size:20px;font-weight:700;color:#7f4c31">{{offerText}}</p>{{productCards}}
                      <p style="margin-top:20px">Visita El mundo de Mery para descubrir estas piezas.</p>
                    </div>
                  </div>
                </div>
                """;
    }

    private String escapeHtml(String value) {
        return value == null ? "" : value.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }

    private record CampaignAudience(Category category, List<Product> products, Map<String, User> audience) {}
}
