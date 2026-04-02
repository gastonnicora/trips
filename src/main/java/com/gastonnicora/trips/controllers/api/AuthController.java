package com.gastonnicora.trips.controllers.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gastonnicora.trips.dtos.request.LoginRequest;
import com.gastonnicora.trips.dtos.response.LoginResponse;
import com.gastonnicora.trips.security.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth API", description = "Endpoints para autenticación y autorización")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService){
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    @Operation(summary = "Inicio de sesion",description = "Inicia sesión con email y contraseña y recibe un token")
    public LoginResponse login(@Valid @RequestBody LoginRequest entity) {
        //TODO agreagrar y configurar refreshtokon para mejorar seguridad
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(entity.getEmail(), entity.getPassword())
        );
        String token = jwtService.generateToken(entity.getEmail());
        return new LoginResponse(token);
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cerrar sesión", description = "Cierra la sesión actual.")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok("Logged out");
    }
    


    





}
