package com.gastonnicora.trips.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

/**
 * Servicio para la generación, validación y extracción de información de tokens
 * JWT.
 * <p>
 * Utiliza HS256 con una clave secreta definida en
 * {@code application.properties}.
 * </p>
 * 
 * Funcionalidades:
 * <ul>
 * <li>Generar tokens JWT con email, versión y UUID del usuario.</li>
 * <li>Extraer información del token (email, versión, UUID).</li>
 * <li>Validar que un token sea válido.</li>
 * </ul>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET;

    private SecretKey key;

    /**
     * Inicializa la clave secreta a partir de la propiedad {@code jwt.secret}.
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Genera un token JWT para un usuario.
     * 
     * @param email   Correo electrónico del usuario
     * @param version Versión del token
     * @param uuid    UUID del usuario
     * @return JWT como String
     */
    public String generateToken(String email, int version, UUID uuid) {
        return Jwts.builder()
                .subject(email)
                .claim("ver", version)
                .claim("userId", uuid)
                .claim("jti", UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 5))
                .signWith(key)
                .compact();
    }

    /**
     * Parsea un JWT y obtiene sus claims.
     * 
     * @param token JWT
     * @return Claims del token
     * @throws JwtException si el token no es válido
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extrae el email (subject) del token.
     * 
     * @param token JWT
     * @return Email del usuario
     */
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Extrae la versión del token.
     * 
     * @param token JWT
     * @return Versión como Integer
     */
    public Integer extractVersion(String token) {
        return parseClaims(token).get("ver", Integer.class);
    }

    /**
     * Extrae el UUID del usuario del token.
     * 
     * @param token JWT
     * @return UUID del usuario
     */
    public UUID extractUUID(String token) {
        return UUID.fromString(parseClaims(token).get("userId", String.class));
    }

    /**
     * Valida si un token es válido.
     * 
     * @param token JWT
     * @return true si es válido, false si no lo es
     */
    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}