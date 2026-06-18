package com.example.backend.repositories;

import com.example.backend.models.PurchaseIntent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PurchaseIntentRepository extends JpaRepository<PurchaseIntent, Long> {
    List<PurchaseIntent> findAllByViewedAtBetweenOrderByViewedAtDesc(LocalDateTime start, LocalDateTime end);

    boolean existsByUser_IdAndProduct_IdAndViewedAtAfter(Long userId, Long productId, LocalDateTime viewedAt);

    @Query("""
            SELECT intent
            FROM PurchaseIntent intent
            JOIN FETCH intent.product
            JOIN FETCH intent.user user
            JOIN FETCH user.persona
            WHERE intent.product.id = :productId
              AND intent.viewedAt BETWEEN :start AND :end
            ORDER BY intent.viewedAt DESC
            """)
    List<PurchaseIntent> findCampaignAudience(
            @Param("productId") Long productId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
