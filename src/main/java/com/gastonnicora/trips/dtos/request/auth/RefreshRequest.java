package com.gastonnicora.trips.dtos.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO utilizado para solicitar la renovación de un token de acceso.
 * <p>
 * Contiene el refresh token utilizado para obtener un nuevo access token. En
 * clientes web, el refresh token puede enviarse mediante una cookie HTTP,
 * mientras que en aplicaciones móviles puede enviarse mediante el cuerpo de la
 * solicitud.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
@Schema(
        description = "Solicitud para renovar el token de acceso mediante un refresh token"
)
@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class RefreshRequest {

    /**
     * Refresh token proporcionado por el cliente.
     * <p>
     * Este campo puede ser {@code null} cuando el refresh token se envía
     * mediante una cookie HTTP, como ocurre con los clientes web.
     * </p>
     */
    @Schema(
            description = "Refresh token utilizado para obtener un nuevo access token. "
            + "Opcional cuando el token se envía mediante cookie.",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String refreshToken;

}
