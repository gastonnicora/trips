package com.gastonnicora.trips.exceptions.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

/**
 * Representa un error de acceso prohibido correspondiente al código de estado
 * HTTP {@code 403}.
 *
 * <p>
 * Se utiliza cuando un usuario intenta acceder a un recurso para el cual no
 * dispone de los permisos necesarios.
 * </p>
 *
 * <p>
 * Hereda de {@link ApiError} y establece automáticamente el código de estado
 * HTTP {@link HttpStatus#FORBIDDEN}.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-04-21
 */
@Schema(
        description = "Error correspondiente a un acceso prohibido.",
        example = """
                {
                  "status": 403,
                  "message": "Acceso denegado",
                  "timestamp": "2026-05-04T12:34:56",
                  "errors": null
                }
                """
)
public class ForbiddenApiError extends ApiError {

    /**
     * Constructor para crear un error de acceso prohibido con el mensaje
     * predeterminado.
     */
    public ForbiddenApiError() {
        super(HttpStatus.FORBIDDEN.value(), "Acceso denegado");
    }

    /**
     * Constructor para crear un error de acceso prohibido con un mensaje
     * personalizado.
     *
     * @param message Mensaje descriptivo del error.
     */
    public ForbiddenApiError(String message) {
        super(HttpStatus.FORBIDDEN.value(), message);
    }
}
