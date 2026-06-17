package com.example.backend.controllers; // CORREGIDO: "controllers" en plural

import com.example.backend.dto.ProductRequest;
import com.example.backend.models.Category;
import com.example.backend.models.Product;
import com.example.backend.repositories.CategoryRepository;
import com.example.backend.repositories.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct() throws Exception {
        Category category = new Category();
        category.setName("ceramica test");
        category.setLabel("TEST");
        category.setDescription("Categora para testing");
        category.setImageUrl("test.png");
        category.setEventStatus(true);
        category = categoryRepository.save(category);

        ProductRequest request = new ProductRequest();
        request.setName("Jarron test");
        request.setPrice(BigDecimal.valueOf(120.50));
        request.setStock(15);
        request.setImageUrl("/img/jarron.png");
        request.setStatus(true);
        request.setCategoryId(category.getId());

        mockMvc.perform(post("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jarron test"));

        List<Product> productosEnBd = productRepository.findAll();

        assertFalse(productosEnBd.isEmpty(), "La base de datos debería tener al menos un producto");

        Product productoGuardado = productosEnBd.stream()
                .filter(p -> p.getName().equals("Jarron test"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("El producto no llego a guardarse"));

        assertEquals(0, BigDecimal.valueOf(120.50).compareTo(productoGuardado.getPrice()), "El precio no coincide");
        assertEquals(15, productoGuardado.getStock(), "El stock no coincide");
        assertEquals(category.getId(), productoGuardado.getCategory().getId(), "La categoría no coincide");
    }
}