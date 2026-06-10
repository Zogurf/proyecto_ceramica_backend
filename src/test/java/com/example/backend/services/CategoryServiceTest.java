package com.example.backend.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.backend.dto.CategoryDTO;
import com.example.backend.dto.CategoryShowcaseDTO;
import com.example.backend.dto.ProductSummaryDTO;
import com.example.backend.models.Category;
import com.example.backend.models.Product;
import com.example.backend.repositories.CategoryRepository;
import com.example.backend.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryService categoryService;

//    crear test
    @Test
    void createCategory_ShouldSaveAndReturnCategoryDTO() {
        CategoryDTO request = new CategoryDTO(null, "NEW", "Baño", "Cosas de baño", "bano.png");

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setLabel("NEW");
        savedCategory.setName("Baño");
        savedCategory.setDescription("Cosas de baño");
        savedCategory.setImageUrl("bano.png");

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        CategoryDTO result = categoryService.createCategory(request);

        assertNotNull(result);
        assertEquals(1L, result.getCategoryId());
        assertEquals("Baño", result.getCategoryName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

//    actualizar test
    @Test
    void updateCategory_WhenCategoryExists() {
        Long categoryId = 1L;
        CategoryDTO request = new CategoryDTO(null, "UPDATED", "Baño Premium", "Desc", "img.png");

        Category existingCategory = new Category();
        existingCategory.setId(categoryId);
        existingCategory.setName("Baño");

        when(categoryRepository.findById(categoryId)).thenReturn(java.util.Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));

        CategoryDTO result = categoryService.updateCategory(categoryId, request);

        assertEquals("Baño Premium", result.getCategoryName());
        assertEquals("UPDATED", result.getLabel());
        verify(categoryRepository, times(1)).save(existingCategory);
    }

//    actualizar test
    @Test
    void updateCategory_WhenNotFound() {
        Long invalidId = 99L;
        CategoryDTO request = new CategoryDTO();
        when(categoryRepository.findById(invalidId)).thenReturn(java.util.Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.updateCategory(invalidId, request);
        });

        assertEquals("No existe la categoria", exception.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    // borrar test
    @Test
    void deleteCategory_WhenCategoryExists() {
        Long categoryId = 1L;
        Category existingCategory = new Category();
        existingCategory.setId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(java.util.Optional.of(existingCategory));

        categoryService.deleteCategory(categoryId);

        verify(categoryRepository, times(1)).delete(existingCategory);
    }

    // listar en el home si no existe
    @Test
    void getHomeCategories_WhenCategoriesExist() {
        Category category = new Category();
        category.setId(1L);
        category.setLabel("HOT");
        category.setName("Ofertas");
        category.setDescription("Productos en oferta");
        category.setImageUrl("ofertas.png");
        category.setEventStatus(true);

        when(categoryRepository.findByEventStatusTrue()).thenReturn(List.of(category));

        List<CategoryDTO> result = categoryService.getHomeCategories();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        CategoryDTO dto = result.get(0);
        assertEquals(1L, dto.getCategoryId());
        assertEquals("Ofertas", dto.getCategoryName());
        assertEquals("HOT", dto.getLabel());
        assertEquals("Productos en oferta", dto.getDescription());
        assertEquals("ofertas.png", dto.getImageUrl());

        verify(categoryRepository, times(1)).findByEventStatusTrue();  // verificar en bd
    }
}