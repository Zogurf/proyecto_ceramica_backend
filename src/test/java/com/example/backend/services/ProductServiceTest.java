package com.example.backend.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.backend.dto.AdminProductResponse;
import com.example.backend.dto.ProductRequest;
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

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

//    crear test
    @Test
    void createProduct() {
        ProductRequest request = new ProductRequest();
        request.setName("Jarrón");
        request.setCategoryId(1L);
        request.setStock(10);
        request.setPrice(BigDecimal.valueOf(50.0));

        Category mockCategory = new Category();
        mockCategory.setId(1L);

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("Jarrón");
        savedProduct.setCategory(mockCategory);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        AdminProductResponse response = productService.createProduct(request);

        assertNotNull(response);
        assertEquals("Jarrón", response.getName());
        verify(productRepository, times(1)).save(any(Product.class)); // verificar en bd
    }

//    actualizar test
    @Test
    void updateProduct_WhenValidRequest() {
        Long productId = 1L;
        ProductRequest request = new ProductRequest();
        request.setName("Plato Actualizado");
        request.setCategoryId(2L);
        request.setPrice(BigDecimal.valueOf(25.0));
        request.setStock(15);
        request.setStatus(true);

        Product existingProduct = new Product();
        existingProduct.setId(productId);

        Category newCategory = new Category();
        newCategory.setId(2L);
        newCategory.setName("Hogar");

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AdminProductResponse response = productService.updateProduct(productId, request);

        assertNotNull(response);
        assertEquals("Plato Actualizado", response.getName());
        assertEquals(25.0, response.getPrice().doubleValue());
        verify(productRepository, times(1)).save(existingProduct); // verificar en bd
    }

//    borrar test
    @Test
    void deleteProduct_WhenProductExists() {
        Long productId = 1L;
        Product activeProduct = new Product();
        activeProduct.setId(productId);
        activeProduct.setStatus(true);

        when(productRepository.findById(productId)).thenReturn(Optional.of(activeProduct));

        productService.deleteProduct(productId);

        assertFalse(activeProduct.isStatus(), "El estado del producto debería ser false");
        verify(productRepository, times(1)).save(activeProduct);  // verificar en bd
    }
}
