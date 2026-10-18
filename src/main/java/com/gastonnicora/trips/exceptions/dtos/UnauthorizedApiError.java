package com.gastonnicora.trips.exceptions.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

/**
 * Representa un error de autenticación correspondiente al código de estado HTTP
 * {@code 401}.
 *
 * <p>
 * Se utiliza cuando la solicitud no cuenta con credenciales de autenticación
 * válidas, por ejemplo, cuando el token es inválido o ha expirado.
 * </p>
 *
 * <p>
 * Hereda de {@link ApiError} y establece automáticamente el código de estado
 * HTTP {@link HttpStatus#UNAUTHORIZED}.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-04-21
 */
@Schema(
        description = "Error correspondiente a una solicitud que requiere autenticación válida.",
        example = """
                {
                  "status": 401,
                  "message": "Token inválido o expirado",
                  "timestamp": "2026-05-04T12:34:56",
                  "errors": null
                }
                """
)
public class UnauthorizedApiError extends ApiError {

    /**
     * Constructor para crear un error de autenticación con el mensaje
     * predeterminado.
     */
    public UnauthorizedApiError() {
        super(HttpStatus.UNAUTHORIZED.value(), "Token inválido o expirado");
    }

    /**
     * Constructor para crear un error de autenticación con un mensaje
     * personalizado.
     *
     * @param message Mensaje descriptivo del error.
     */
    public UnauthorizedApiError(String message) {
        super(HttpStatus.UNAUTHORIZED.value(), message);
    }
}
