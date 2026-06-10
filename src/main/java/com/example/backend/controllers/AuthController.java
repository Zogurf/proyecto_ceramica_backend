package com.example.backend.controllers;

import com.example.backend.dto.GoogleLoginRequest;
import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.LoginResponse;
import com.example.backend.dto.PasswordResetConfirmRequest;
import com.example.backend.dto.PasswordResetRequest;
import com.example.backend.dto.PasswordResetVerifyRequest;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody GoogleLoginRequest request) {
        try {
            return ResponseEntity.ok(authService.loginWithGoogle(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (!request.validateEmail(request.email())) {
            return ResponseEntity.badRequest().body("Porfavor ingrese un correo valido");
        }

        try {
            authService.register(request);
            return ResponseEntity.ok("User registered successfully");

        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Esta persona ya tiene una cuenta registrada"));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ocurrio un error durante el registro. Intenta nuevamente."));
        }
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<?> requestPasswordReset(@RequestBody PasswordResetRequest request) {
        try {
            authService.requestPasswordReset(request);
            return ResponseEntity.ok(Map.of("message", "Codigo enviado correctamente"));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/password-reset/verify")
    public ResponseEntity<?> verifyPasswordResetCode(@RequestBody PasswordResetVerifyRequest request) {
        try {
            authService.verifyPasswordResetCode(request);
            return ResponseEntity.ok(Map.of("message", "Codigo validado correctamente"));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<?> confirmPasswordReset(@RequestBody PasswordResetConfirmRequest request) {
        try {
            authService.confirmPasswordReset(request);
            return ResponseEntity.ok(Map.of("message", "Contrasena actualizada correctamente"));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }
}
