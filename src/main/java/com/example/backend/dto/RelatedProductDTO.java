package com.example.backend.dto;


import com.example.backend.models.Product;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RelatedProductDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private String imageUrl;

    public RelatedProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.imageUrl = product.getImageUrl();
    }
}
