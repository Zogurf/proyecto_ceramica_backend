package com.example.backend.controllers;

import com.example.backend.dto.CategoryDTO;
import com.example.backend.dto.CategoryShowcaseDTO;
import com.example.backend.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.createCategory(categoryDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<CategoryShowcaseDTO>> getCategoriesForHome() {
        List<CategoryShowcaseDTO> response = categoryService.getHomeCategoriesWithProducts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<List<CategoryDTO>> getCategories() {
        List<CategoryDTO> response = categoryService.getHomeCategories();
        return ResponseEntity.ok(response);
    }
}
