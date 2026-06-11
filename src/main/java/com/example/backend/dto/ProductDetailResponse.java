package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ProductDetailResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private boolean status;
    private String categoryName;
    private String description; // viene de la categoria

    private List<SizeDTO> sizes;

    // recomendados
    private List<RelatedProductDTO> relatedProducts;
}