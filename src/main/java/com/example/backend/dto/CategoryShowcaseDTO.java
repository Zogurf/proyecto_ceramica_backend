package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryShowcaseDTO {
    private Long categoryId;
    private String label;
    private String categoryName;
    private String description;
    private String imageUrl;
    private List<ProductSummaryDTO> products;
}
