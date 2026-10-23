package com.gastonnicora.trips.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * Servicio encargado de la generación, validación y extracción de información
 * de tokens JWT.
 *
 * <p>
 * Utiliza una clave secreta configurada mediante la propiedad
 * {@code jwt.secret} para firmar y validar los tokens.
 * </p>
 *
 * <p>
 * Proporciona operaciones para generar tokens JWT, extraer información de sus
 * claims y comprobar su validez.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-03-18
 */
@Service
public class JwtService {

    /**
     * Clave secreta, tomada de la propiedad {@code jwt.secret}, a partir de la
     * cual se genera la clave de firma de los tokens.
     */
    @Value("${jwt.secret}")
    private String SECRET;

    /**
     * Clave criptográfica con la que se firman y verifican los tokens JWT.
     *
     * <p>
     * Se genera en {@link #init()} a partir de {@code SECRET}.
     * </p>
     */
    private SecretKey key;

    /**
     * Extrae el identificador único del usuario almacenado en el token.
     *
     * @param token Token JWT.
     *
     * @return Identificador único del usuario.
     */
    public UUID extractUUID(String token) {
        return UUID.fromString(parseClaims(token).get("userId", String.class));
    }

    /**
     * Extrae el correo electrónico del usuario almacenado como subject del
     * token.
     *
     * @param token Token JWT.
     *
     * @return Correo electrónico del usuario.
     */
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Extrae la versión del token.
     *
     * @param token Token JWT.
     *
     * @return Versión del token.
     */
    public Integer extractVersion(String token) {
        return parseClaims(token).get("ver", Integer.class);
    }

    /**
     * Genera un token JWT para un usuario.
     *
     * <p>
     * El token contiene el correo electrónico como subject, la versión del token,
     * el identificador del usuario, un identificador único del token (jti), la
     * fecha de emisión y la fecha de expiración. El token expira a los 5 minutos
     * de su emisión.
     * </p>
     *
     * @param email   Correo electrónico del usuario.
     * @param version Versión del token.
     * @param uuid    Identificador único del usuario.
     *
     * @return Token JWT generado.
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
     * Inicializa la clave utilizada para firmar y validar los tokens JWT a partir
     * de la propiedad {@code jwt.secret}.
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Verifica si un token JWT es válido.
     *
     * <p>
     * La validación se realiza mediante el análisis de los claims del token.
     * </p>
     *
     * @param token Token JWT que se desea validar.
     *
     * @return {@code true} si el token es válido; {@code false} en caso contrario.
     */
    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Parsea un token JWT y obtiene sus claims.
     *
     * @param token Token JWT que se desea procesar.
     *
     * @return Claims contenidos en el token.
     *
     * @throws JwtException Si el token no es válido.
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}