package com.example.backend.dto;

import java.math.BigDecimal;

public record FavoriteResponse(Long productId, String name, BigDecimal price, String imageUrl,
                               Integer stock, String categoryName) {}
