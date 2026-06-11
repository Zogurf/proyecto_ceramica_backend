package com.example.backend.controllers;

import com.example.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/reset")
@RequiredArgsConstructor
public class AdminResetController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.example.backend.repositories.PersonaRepository personaRepository;

    /**
     * Endpoint temporal para restablecer contraseña del admin
     * SOLO USAR EN DESARROLLO - ELIMINAR EN PRODUCCIÓN
     * 
     * Método: POST
     * URL: http://localhost:8080/api/admin/reset/password
     * Body: {"email": "AireSeco@hotmail.com", "newPassword": "admin123"}
     */
    @PostMapping("/password")
    public Map<String, String> resetAdminPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        if (email == null || newPassword == null) {
            return Map.of("error", "Email y nueva contraseña son requeridos");
        }

        if (newPassword.length() < 6) {
            return Map.of("error", "La contraseña debe tener al menos 6 caracteres");
        }

        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return Map.of("error", "Usuario no encontrado");
        }

        var user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        
        if (user.getPersona() == null) {
            var personaOpt = personaRepository.findById(1L);
            if (personaOpt.isPresent()) {
                user.setPersona(personaOpt.get());
            } else {
                var persona = new com.example.backend.models.Persona();
                persona.setName("Administrador");
                persona.setFirstName("Admin");
                persona.setLastName("Usuario");
                persona.setMotherLastName("");
                persona = personaRepository.save(persona);
                user.setPersona(persona);
            }
        }
        
        userRepository.save(user);

        return Map.of("message", "Contraseña actualizada correctamente");
    }

    /**
     * Endpoint para verificar usuarios con contraseñas inválidas
     * SOLO USAR EN DESARROLLO - ELIMINAR EN PRODUCCIÓN
     */
    @GetMapping("/check-users")
    public Map<String, Object> checkUsers() {
        var users = userRepository.findAll();
        int validPasswords = 0;
        int invalidPasswords = 0;
        java.util.List<Map<String, Object>> list = new java.util.ArrayList<>();

        for (var user : users) {
            boolean isValid = user.getPassword() != null && user.getPassword().startsWith("$2a$");
            if (isValid) {
                validPasswords++;
            } else {
                invalidPasswords++;
            }
            list.add(Map.of(
                "email", user.getEmail() != null ? user.getEmail() : "null",
                "role", user.getRole() != null && user.getRole().getName() != null ? user.getRole().getName() : "null",
                "hasValidPassword", isValid
            ));
        }

        return Map.of(
            "totalUsers", users.size(),
            "validBCryptPasswords", validPasswords,
            "invalidPasswords", invalidPasswords,
            "users", list
        );
    }
}