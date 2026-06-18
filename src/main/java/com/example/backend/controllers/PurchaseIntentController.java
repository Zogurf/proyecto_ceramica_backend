package com.example.backend.controllers;

import com.example.backend.dto.PurchaseIntentRequest;
import com.example.backend.services.PurchaseIntentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/purchase-intentions")
@RequiredArgsConstructor
public class PurchaseIntentController {
    private final PurchaseIntentService purchaseIntentService;

    @PostMapping
    public ResponseEntity<Void> registerIntent(@Valid @RequestBody PurchaseIntentRequest request) {
        purchaseIntentService.registerIntent(request);
        return ResponseEntity.noContent().build();
    }
}
