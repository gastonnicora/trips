package com.gastonnicora.trips.exceptions.dtos;

import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Representa un error de autenticación (HTTP 401).
 * <p>
 * Se utiliza cuando un usuario no está autenticado o el token es
 * inválido/expirado.
 * Hereda de {@link ApiError} y establece automáticamente el código de estado a
 * 401.
 * </p>
 *
 * <p>
 * Ejemplo de respuesta JSON:
 * </p>
 *
 * <pre>
 * {
 *   "status": 401,
 *   "message": "Token inválido o expirado",
 *   "timestamp": "2026-05-04T12:34:56",
 *   "errors": null
 * }
 * </pre>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
@Schema(description = "Error de autenticación", example = """
        {
          "status": 401,
          "message": "Token inválido o expirado",
          "timestamp": "2026-05-04T12:34:56",
          "errors": null
        }
        """)
public class UnauthorizedApiError extends ApiError {

    /**
     * Constructor por defecto.
     */
    public UnauthorizedApiError() {
        super(HttpStatus.UNAUTHORIZED.value(), "Token inválido o expirado");
    }

    /**
     * Constructor con mensaje personalizado.
     *
     * @param message Mensaje descriptivo del error
     */
    public UnauthorizedApiError(String message) {
        super(HttpStatus.UNAUTHORIZED.value(), message);
    }
}