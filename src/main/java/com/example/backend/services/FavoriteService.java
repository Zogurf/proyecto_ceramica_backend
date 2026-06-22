package com.example.backend.services;

import com.example.backend.dto.FavoriteResponse;
import com.example.backend.dto.PurchaseIntentRequest;
import com.example.backend.models.Favorite;
import com.example.backend.models.Product;
import com.example.backend.models.User;
import com.example.backend.repositories.FavoriteRepository;
import com.example.backend.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service @RequiredArgsConstructor
public class FavoriteService {
    private final CurrentUserService currentUserService;
    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;
    private final PurchaseIntentService purchaseIntentService;

    @Transactional(readOnly = true)
    public List<FavoriteResponse> list() {
        User user = currentUserService.getCurrentUser();
        return favoriteRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(f -> toResponse(f.getProduct())).toList();
    }

    @Transactional
    public FavoriteResponse add(Long productId) {
        User user = currentUserService.getCurrentUser();
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        favoriteRepository.findByUserIdAndProductId(user.getId(), productId).orElseGet(() -> {
            Favorite favorite = new Favorite(); favorite.setUser(user); favorite.setProduct(product); favorite.setCreatedAt(LocalDateTime.now());
            return favoriteRepository.save(favorite);
        });
        purchaseIntentService.registerIntent(new PurchaseIntentRequest(productId, "FAVORITE"));
        return toResponse(product);
    }

    @Transactional
    public void remove(Long productId) {
        User user = currentUserService.getCurrentUser();
        favoriteRepository.findByUserIdAndProductId(user.getId(), productId).ifPresent(favoriteRepository::delete);
    }

    private FavoriteResponse toResponse(Product p) {
        return new FavoriteResponse(p.getId(), p.getName(), p.getPrice(), p.getImageUrl(), p.getStock(),
                p.getCategory() != null ? p.getCategory().getName() : "Sin categoria");
    }
}
