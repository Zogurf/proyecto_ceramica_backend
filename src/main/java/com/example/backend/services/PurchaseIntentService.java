package com.example.backend.services;

import com.example.backend.dto.PurchaseIntentRequest;
import com.example.backend.dto.PurchaseIntentResponse;
import com.example.backend.models.Product;
import com.example.backend.models.PurchaseIntent;
import com.example.backend.models.User;
import com.example.backend.repositories.ProductRepository;
import com.example.backend.repositories.PurchaseIntentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseIntentService {
    private final CurrentUserService currentUserService;
    private final ProductRepository productRepository;
    private final PurchaseIntentRepository purchaseIntentRepository;

    @Transactional
    public void registerIntent(PurchaseIntentRequest request) {
        User user = currentUserService.getCurrentUser();
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        LocalDateTime now = LocalDateTime.now();

        boolean alreadyRegistered = purchaseIntentRepository.existsByUser_IdAndProduct_IdAndViewedAtAfter(
                user.getId(),
                product.getId(),
                now.minusMinutes(5)
        );

        if (alreadyRegistered) {
            return;
        }

        PurchaseIntent intent = new PurchaseIntent();
        intent.setProduct(product);
        intent.setUser(user);
        intent.setViewedAt(now);
        purchaseIntentRepository.save(intent);
    }

    @Transactional(readOnly = true)
    public List<PurchaseIntentResponse> getAdminIntentions(LocalDate startDate, LocalDate endDate) {
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(30);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        return purchaseIntentRepository
                .findAllByViewedAtBetweenOrderByViewedAtDesc(start.atStartOfDay(), end.atTime(LocalTime.MAX))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PurchaseIntentResponse toResponse(PurchaseIntent intent) {
        User user = intent.getUser();
        return new PurchaseIntentResponse(
                intent.getId(),
                intent.getProduct().getId(),
                intent.getProduct().getName(),
                user.getPersona() != null ? user.getPersona().getName() : user.getEmail(),
                user.getEmail(),
                intent.getViewedAt()
        );
    }
}
