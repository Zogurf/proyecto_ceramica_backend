package com.example.backend.services;

import com.example.backend.dto.GoogleLoginRequest;
import com.example.backend.dto.GoogleTokenInfoResponse;
import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.LoginResponse;
import com.example.backend.dto.PasswordResetConfirmRequest;
import com.example.backend.dto.PasswordResetRequest;
import com.example.backend.dto.PasswordResetVerifyRequest;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.models.PasswordResetCode;
import com.example.backend.models.Persona;
import com.example.backend.models.Role;
import com.example.backend.models.User;
import com.example.backend.repositories.PasswordResetCodeRepository;
import com.example.backend.repositories.PersonaRepository;
import com.example.backend.repositories.RoleRepository;
import com.example.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int RESET_CODE_EXPIRATION_MINUTES = 10;
    private static final int MAX_RESET_ATTEMPTS = 5;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PersonaRepository personaRepository;
    private final PasswordResetCodeRepository passwordResetCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final GoogleAuthService googleAuthService;
    private final EmailService emailService;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        var user = userRepository.findByEmail(request.email()).orElseThrow();
        String token = jwtService.generateToken(userDetails);

        return new LoginResponse(token, user.getPersona().getName(), user.getRole().getName());
    }

    public LoginResponse loginWithGoogle(GoogleLoginRequest request) {
        GoogleTokenInfoResponse tokenInfo = googleAuthService.verifyCredential(request.credential());

        User user = userRepository.findByGoogleId(tokenInfo.subject())
                .orElseGet(() -> userRepository.findByEmail(tokenInfo.email())
                        .map(existingUser -> attachGoogleIdentity(existingUser, tokenInfo))
                        .orElseGet(() -> createGoogleUser(tokenInfo)));

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().getName())
                .build();

        String token = jwtService.generateToken(userDetails);
        return new LoginResponse(token, user.getPersona().getName(), user.getRole().getName());
    }

    public void register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("El email ya existe");
        }

        if (StringUtils.isBlank(request.dni()) || StringUtils.isBlank(request.firstName()) || StringUtils.isBlank(request.lastName())
                || StringUtils.isBlank(request.motherLastName())) {
            throw new RuntimeException("Debes consultar y completar los datos del DNI antes de registrarte");
        }

        // Validar que el DNI no esté registrado
        if (personaRepository.findByDni(request.dni()).isPresent()) {
            throw new RuntimeException("Esta persona ya tiene una cuenta registrada");
        }

        Role role = roleRepository.findByName("CLIENTE")
                .orElseThrow(() -> new RuntimeException("Role CLIENTE not found"));

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setAuthProvider("LOCAL");
        user.setRole(role);

        Persona persona = new Persona();
        persona.setDni(request.dni());
        persona.setFirstName(request.firstName());
        persona.setLastName(request.lastName());
        persona.setMotherLastName(request.motherLastName());
        persona.setBirthDate(request.birthDate());
        persona.setName(buildFullName(request));
        persona = personaRepository.save(persona);

        user.setPersona(persona);
        userRepository.save(user);
    }

    public void requestPasswordReset(PasswordResetRequest request) {
        String email = normalizeEmail(request.email());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No existe una cuenta registrada con ese correo"));

        if (!"LOCAL".equalsIgnoreCase(user.getAuthProvider())) {
            throw new RuntimeException("Esta cuenta usa Google. Inicia sesion con Google para continuar");
        }

        String code = generateResetCode();
        PasswordResetCode resetCode = new PasswordResetCode();
        resetCode.setEmail(email);
        resetCode.setCodeHash(passwordEncoder.encode(code));
        resetCode.setCreatedAt(LocalDateTime.now());
        resetCode.setExpiresAt(LocalDateTime.now().plusMinutes(RESET_CODE_EXPIRATION_MINUTES));
        resetCode.setUsed(false);
        resetCode.setAttempts(0);
        passwordResetCodeRepository.save(resetCode);

        emailService.sendPasswordResetCode(email, code);
    }

    public void verifyPasswordResetCode(PasswordResetVerifyRequest request) {
        PasswordResetCode resetCode = getValidResetCode(request.email());

        if (!passwordEncoder.matches(sanitizeCode(request.code()), resetCode.getCodeHash())) {
            registerFailedResetAttempt(resetCode);
            throw new RuntimeException("El codigo no es valido");
        }
    }

    public void confirmPasswordReset(PasswordResetConfirmRequest request) {
        if (request.newPassword() == null || request.newPassword().length() < 6) {
            throw new RuntimeException("La nueva contrasena debe tener al menos 6 caracteres");
        }

        String email = normalizeEmail(request.email());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No existe una cuenta registrada con ese correo"));

        if (!"LOCAL".equalsIgnoreCase(user.getAuthProvider())) {
            throw new RuntimeException("Esta cuenta usa Google. Inicia sesion con Google para continuar");
        }

        PasswordResetCode resetCode = getValidResetCode(email);
        if (!passwordEncoder.matches(sanitizeCode(request.code()), resetCode.getCodeHash())) {
            registerFailedResetAttempt(resetCode);
            throw new RuntimeException("El codigo no es valido");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        resetCode.setUsed(true);
        passwordResetCodeRepository.save(resetCode);
    }

    private User attachGoogleIdentity(User user, GoogleTokenInfoResponse tokenInfo) {
        user.setGoogleId(tokenInfo.subject());
        user.setAuthProvider("GOOGLE");
        return userRepository.save(user);
    }

    private User createGoogleUser(GoogleTokenInfoResponse tokenInfo) {
        Role role = roleRepository.findByName("CLIENTE")
                .orElseThrow(() -> new RuntimeException("Role CLIENTE not found"));

        Persona persona = new Persona();
        persona.setName(resolveDisplayName(tokenInfo));
        persona.setFirstName(resolveFirstName(tokenInfo));
        persona.setLastName(resolveLastName(tokenInfo));
        persona.setMotherLastName("");
        persona.setBirthDate(null);
        persona = personaRepository.save(persona);

        User user = new User();
        user.setEmail(tokenInfo.email());
        user.setPassword(passwordEncoder.encode("GOOGLE-" + tokenInfo.subject()));
        user.setGoogleId(tokenInfo.subject());
        user.setAuthProvider("GOOGLE");
        user.setRole(role);
        user.setPersona(persona);

        return userRepository.save(user);
    }

    private String buildFullName(RegisterRequest request) {
        return String.join(" ",
                request.firstName().trim(),
                request.lastName().trim(),
                request.motherLastName().trim()
        ).trim();
    }

    private String resolveDisplayName(GoogleTokenInfoResponse tokenInfo) {
        if (!StringUtils.isBlank(tokenInfo.name())) {
            return tokenInfo.name().trim();
        }
        return String.join(" ", resolveFirstName(tokenInfo), resolveLastName(tokenInfo)).trim();
    }

    private String resolveFirstName(GoogleTokenInfoResponse tokenInfo) {
        if (!StringUtils.isBlank(tokenInfo.givenName())) {
            return tokenInfo.givenName().trim();
        }
        return !StringUtils.isBlank(tokenInfo.name()) ? tokenInfo.name().trim() : "Usuario";
    }

    private String resolveLastName(GoogleTokenInfoResponse tokenInfo) {
        if (!StringUtils.isBlank(tokenInfo.familyName())) {
            return tokenInfo.familyName().trim();
        }
        return "";
    }

    private PasswordResetCode getValidResetCode(String email) {
        PasswordResetCode resetCode = passwordResetCodeRepository
                .findTopByEmailAndUsedFalseOrderByCreatedAtDesc(normalizeEmail(email))
                .orElseThrow(() -> new RuntimeException("Solicita un nuevo codigo para continuar"));

        if (resetCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            resetCode.setUsed(true);
            passwordResetCodeRepository.save(resetCode);
            throw new RuntimeException("El codigo vencio. Solicita uno nuevo");
        }

        if (resetCode.getAttempts() >= MAX_RESET_ATTEMPTS) {
            resetCode.setUsed(true);
            passwordResetCodeRepository.save(resetCode);
            throw new RuntimeException("Demasiados intentos. Solicita un nuevo codigo");
        }

        return resetCode;
    }

    private void registerFailedResetAttempt(PasswordResetCode resetCode) {
        resetCode.setAttempts(resetCode.getAttempts() + 1);
        passwordResetCodeRepository.save(resetCode);
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new RuntimeException("Ingresa tu correo electronico");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String sanitizeCode(String code) {
        if (code == null || !code.matches("\\d{6}")) {
            throw new RuntimeException("Ingresa el codigo de 6 digitos");
        }
        return code;
    }

    private String generateResetCode() {
        return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
    }
}
