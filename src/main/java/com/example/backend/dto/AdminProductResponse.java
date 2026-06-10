package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminProductResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private Boolean status;
    private Long categoryId;
    private String categoryName;
}
