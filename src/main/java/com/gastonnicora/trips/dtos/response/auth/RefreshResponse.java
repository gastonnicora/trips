package com.gastonnicora.trips.dtos.response.auth;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para el endpoint de refresh de tokens.
 * <p>
 * Contiene un nuevo token JWT de acceso y, opcionalmente, un nuevo token de
 * refresco para reemplazar el anterior.
 * </p>
 * <p>
 * Campos principales:
 * </p>
 * <ul>
 *   <li>{@code token}: Nuevo token JWT de acceso.</li>
 *   <li>{@code refreshToken}: Nuevo token UUID de refresco (puede ser null para web si se usa cookie).</li>
 * </ul>
 * 
 * <p>
 * Ejemplo de respuesta JSON:
 * </p>
 * <pre>
 * {
 *   "token": "eyJhbGciOiJIUzI1NiJ9...",
 *   "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
 * }
 * </pre>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2023-05-04
 */
@Schema(description = "DTO de respuesta de refresh")
public class RefreshResponse {

    @Schema(description = "Token de acceso", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwiaWF0Ijox.NTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
    private String token;

    @Schema(description = "Token de refresco(UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
    private String refreshToken;

    /**
     * Constructor completo de respuesta de refresh.
     * 
     * @param token        Nuevo token JWT de acceso
     * @param refreshToken Nuevo token de refresco UUID (opcional para web)
     */
    public RefreshResponse(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }

    /**
     * Obtiene el nuevo token JWT de acceso.
     * 
     * @return Token JWT de acceso
     */
    public String getToken() {
        return token;
    }

    /**
     * Establece el nuevo token JWT de acceso.
     * 
     * @param token Token JWT de acceso
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Obtiene el nuevo token de refresco UUID.
     * 
     * @return Token de refresco UUID
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Establece el nuevo token de refresco UUID.
     *
     * @param refreshToken Token de refresco UUID
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}