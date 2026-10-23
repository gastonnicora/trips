package com.gastonnicora.trips.dtos.response.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO utilizado para representar la respuesta del inicio de sesión.
 *
 * <p>
 * Contiene el token JWT utilizado para autenticar las solicitudes posteriores
 * y, cuando corresponde, el refresh token utilizado para renovar el token de
 * acceso.
 * </p>
 *
 * <p>
 * En clientes web, el refresh token puede gestionarse mediante una cookie HTTP
 * y, por este motivo, el campo {@code refreshToken} puede no estar presente en
 * la respuesta.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-04-11
 */
@Schema(description = "Respuesta generada tras una autenticación exitosa")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginResponse {

    /**
     * Token JWT utilizado para autenticar las solicitudes del usuario.
     */
    @Schema(
            description = "Token JWT de acceso.",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwiaWF0Ijox.NTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
    )
    private String token;

    /**
     * Token utilizado para renovar el token JWT de acceso.
     *
     * <p>
     * Este campo puede ser {@code null} para clientes web, ya que el refresh
     * token se almacena en una cookie HTTP.
     * </p>
     */
    @Schema(
            description = "Token de refresco UUID. Puede ser nulo para clientes web.",
            example = "550e8400-e29b-41d4-a716-446655440000",
            nullable = true
    )
    private String refreshToken;

}
