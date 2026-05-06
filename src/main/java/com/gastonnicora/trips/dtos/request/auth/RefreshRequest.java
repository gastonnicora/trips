package com.gastonnicora.trips.dtos.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para solicitudes de renovación de token (refresh).
 * <p>
 * Contiene el refresh token que será enviado para obtener un nuevo access
 * token.
 * Puede ser usado tanto desde la web (cookie) como desde aplicaciones móviles
 * (body).
 * </p>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
public class RefreshRequest {

    /**
     * Refresh token enviado por el cliente.
     */
    @Schema(description = "Refresh token usado para renovar el access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    /**
     * Constructor completo.
     * 
     * @param refreshToken refresh token del usuario
     */
    public RefreshRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * Obtiene el refresh token.
     * 
     * @return refresh token
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Establece el refresh token.
     * 
     * @param refreshToken refresh token a establecer
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}