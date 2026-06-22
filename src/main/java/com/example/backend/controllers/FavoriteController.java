package com.example.backend.controllers;

import com.example.backend.dto.FavoriteResponse;
import com.example.backend.services.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/favorites") @RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;
    @GetMapping public List<FavoriteResponse> list() { return favoriteService.list(); }
    @PostMapping("/{productId}") public FavoriteResponse add(@PathVariable Long productId) { return favoriteService.add(productId); }
    @DeleteMapping("/{productId}") public ResponseEntity<Void> remove(@PathVariable Long productId) {
        favoriteService.remove(productId); return ResponseEntity.noContent().build();
    }
}
