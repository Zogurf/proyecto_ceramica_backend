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

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream().map(category -> new CategoryDTO(
                category.getId(),
                category.getLabel(),
                category.getName(),
                category.getDescription(),
                category.getImageUrl()
        )).toList();
    }

    public CategoryDTO createCategory(CategoryDTO request) {
        Category category = new Category();
        category.setLabel(request.getLabel());
        category.setName(request.getCategoryName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setEventStatus(true);

        Category savedCategory = categoryRepository.save(category);

        return new CategoryDTO(
                savedCategory.getId(),
                savedCategory.getLabel(),
                savedCategory.getName(),
                savedCategory.getDescription(),
                savedCategory.getImageUrl()
        );
    }

    public CategoryDTO updateCategory(Long id, CategoryDTO request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe la categoria"));

        category.setLabel(request.getLabel());
        category.setName(request.getCategoryName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());

        Category updatedCategory = categoryRepository.save(category);

        return new CategoryDTO(
                updatedCategory.getId(),
                updatedCategory.getLabel(),
                updatedCategory.getName(),
                updatedCategory.getDescription(),
                updatedCategory.getImageUrl()
        );
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        categoryRepository.delete(category);
    }

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