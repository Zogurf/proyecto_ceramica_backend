package com.example.backend.repositories;

import com.example.backend.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value = "SELECT * FROM products WHERE id_category = :categoryId AND id_product != :productId LIMIT 5", nativeQuery = true)
    List<Product> recommendByCategory(@Param("categoryId") Long categoryId, @Param("productId") Long productId);

    List<Product> findTop4ByCategoryIdAndStatusTrue(Long categoryId);

    List<Product> findByStatusTrue();

    @Query(value = "SELECT price FROM product_size_pricing WHERE id_product = :productId AND id_size = :sizeId", nativeQuery = true)
    java.math.BigDecimal findPriceByProductAndSize(@Param("productId") Long productId, @Param("sizeId") Long sizeId);
}
