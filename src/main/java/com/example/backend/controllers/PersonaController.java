package com.example.backend.controllers;

import com.example.backend.dto.ReniecPersonResponse;
import com.example.backend.services.ReniecService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/personas")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
@RequiredArgsConstructor
public class PersonaController {

    private final ReniecService reniecService;

    @GetMapping("/dni/{dni}")
    public ResponseEntity<?> findByDni(@PathVariable String dni) {
        try {
            return ResponseEntity.ok(reniecService.findByDni(dni));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", ex.getMessage()));
        }
    }
}
