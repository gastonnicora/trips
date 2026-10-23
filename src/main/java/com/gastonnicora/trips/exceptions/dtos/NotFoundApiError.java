package com.gastonnicora.trips.exceptions.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

/**
 * Representa un error de recurso no encontrado correspondiente al código de
 * estado HTTP {@code 404}.
 *
 * <p>
 * Se utiliza cuando el recurso solicitado no existe o no puede ser encontrado.
 * </p>
 *
 * <p>
 * Hereda de {@link ApiError} y establece automáticamente el código de estado
 * HTTP {@link HttpStatus#NOT_FOUND}.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-05-06
 */
@Schema(
        description = "Error correspondiente a un recurso no encontrado.",
        example = """
                {
                  "status": 404,
                  "message": "Recurso no encontrado",
                  "timestamp": "2026-05-04T12:34:56",
                  "errors": null
                }
                """
)
public class NotFoundApiError extends ApiError {

    /**
     * Constructor para crear un error de recurso no encontrado con el mensaje
     * predeterminado.
     */
    public NotFoundApiError() {
        super(HttpStatus.NOT_FOUND.value(), "Recurso no encontrado");
    }

    /**
     * Constructor para crear un error de recurso no encontrado con un mensaje
     * personalizado.
     *
     * @param message Mensaje descriptivo del error.
     */
    public NotFoundApiError(String message) {
        super(HttpStatus.NOT_FOUND.value(), message);
    }
}
