package com.tienda_backend.controller;

import com.tienda_backend.model.User;
import com.tienda_backend.repository.UserRepository;
import com.tienda_backend.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Registro e inicio de sesión con generación de JWT")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario")
    public ResponseEntity<?> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "{\"name\": \"Jugador Prueba\", \"email\": \"jugador.swagger@duocuc.cl\", \"password\": \"gamer123\"}"
                    ))
            )
            @RequestBody Map<String, String> body) {
        String name = body.get("name");
        String email = body.get("email").toLowerCase();
        String password = body.get("password");

        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(Map.of("message", "El correo ya está registrado."));
        }

        boolean isDuoc = email.endsWith("@duocuc.cl") || email.endsWith("@profesor.duoc.cl");
        User user = new User(name, email, passwordEncoder.encode(password), "ROLE_USER", isDuoc);
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole(),
                "hasDiscount", user.isHasDiscount()
        ));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión")
    public ResponseEntity<?> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "{\"email\": \"admin@duocuc.cl\", \"password\": \"admin123\"}"
                    ))
            )
            @RequestBody Map<String, String> body) {
        String email = body.get("email").toLowerCase();
        String password = body.get("password");

        Optional<User> optUser = userRepository.findByEmail(email);
        if (optUser.isEmpty() || !passwordEncoder.matches(password, optUser.get().getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "Credenciales inválidas."));
        }

        User user = optUser.get();
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole(),
                "hasDiscount", user.isHasDiscount()
        ));
    }
}