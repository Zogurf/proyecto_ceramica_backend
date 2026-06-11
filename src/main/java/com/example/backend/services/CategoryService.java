package com.example.backend.services;

import com.example.backend.dto.CategoryDTO;
import com.example.backend.dto.CategoryShowcaseDTO;
import com.example.backend.dto.ProductSummaryDTO;
import com.example.backend.models.Category;
import com.example.backend.repositories.CategoryRepository;
import com.example.backend.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<CategoryShowcaseDTO> getHomeCategoriesWithProducts() {
        List<Category> homeCategories = categoryRepository.findByEventStatusTrue();

        return homeCategories.stream().map(category -> {
            List<ProductSummaryDTO> topProducts = productRepository
                    .findTop4ByCategoryIdAndStatusTrue(category.getId())
                    .stream()
                    .map(product -> new ProductSummaryDTO(
                            product.getId(),
                            product.getName(),
                            product.getPrice(),
                            product.getImageUrl()
                    ))
                    .collect(Collectors.toList());

            return new CategoryShowcaseDTO(
                    category.getId(),
                    category.getLabel(),
                    category.getName(),
                    category.getDescription(),
                    category.getImageUrl(),
                    topProducts
            );
        }).collect(Collectors.toList());
    }

    public List<CategoryDTO> getHomeCategories() {

        return categoryRepository.findByEventStatusTrue().stream().map(category -> new CategoryDTO(
                category.getId(),
                category.getLabel(),
                category.getName(),
                category.getDescription(),
                category.getImageUrl()
        )).toList();
    }
}