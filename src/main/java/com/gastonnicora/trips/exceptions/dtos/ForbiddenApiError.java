package com.gastonnicora.trips.exceptions.dtos;

import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Representa un error de acceso prohibido (HTTP 403).
 * <p>
 * Se utiliza cuando un usuario intenta acceder a un recurso para el que no
 * tiene permisos.
 * Hereda de {@link ApiError} y establece automáticamente el código de estado a
 * 403.
 * </p>
 * 
 * <p>
 * Ejemplo de respuesta JSON:
 * </p>
 * 
 * <pre>
 * {
 *   "status": 403,
 *   "message": "Acceso denegado",
 *   "timestamp": "2026-05-04T12:34:56",
 *   "errors": null
 * }
 * </pre>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
@Schema(description = "Error de acceso prohibido", example = """
        {
          "status": 403,
          "message": "Acceso denegado",
          "timestamp": "2026-05-04T12:34:56",
          "errors": null
        }
        """)
public class ForbiddenApiError extends ApiError {

    /**
     * Constructor por defecto.
     */
    public ForbiddenApiError() {
        super(HttpStatus.FORBIDDEN.value(), "Acceso denegado");
    }

    /**
     * Constructor con mensaje personalizado.
     *
     * @param message Mensaje descriptivo del error
     */
    public ForbiddenApiError(String message) {
        super(HttpStatus.FORBIDDEN.value(), message);
    }
}