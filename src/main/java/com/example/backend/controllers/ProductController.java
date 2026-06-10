package com.example.backend.controllers;

import com.example.backend.dto.ProductDetailResponse;
import com.example.backend.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailResponse> getProduct(@PathVariable Long id) {
        ProductDetailResponse response = productService.getProductDetailsById(id);

        return ResponseEntity.ok(response);
    }
}
