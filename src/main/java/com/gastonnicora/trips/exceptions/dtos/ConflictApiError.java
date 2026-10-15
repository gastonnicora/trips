package com.gastonnicora.trips.exceptions.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

/**
 * Representa un error de conflicto correspondiente al código de estado HTTP
 * {@code 409}.
 *
 * <p>
 * Se utiliza cuando el estado actual de un recurso entra en conflicto con la
 * operación solicitada.
 * </p>
 *
 * <p>
 * Hereda de {@link ApiError} y establece automáticamente el código de estado
 * HTTP {@link HttpStatus#CONFLICT}.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-06
 */
@Schema(
        description = "Error correspondiente a un conflicto con el estado actual de un recurso.",
        example = """
                {
                  "status": 409,
                  "message": "Conflicto de recursos",
                  "timestamp": "2026-05-06T12:34:56",
                  "errors": null
                }
                """
)
public class ConflictApiError extends ApiError {

    /**
     * Constructor para crear un error de conflicto con el mensaje
     * predeterminado.
     */
    public ConflictApiError() {
        super(HttpStatus.CONFLICT.value(), "Conflicto de recursos");
    }

    /**
     * Constructor para crear un error de conflicto con un mensaje
     * personalizado.
     *
     * @param message Mensaje descriptivo del error.
     */
    public ConflictApiError(String message) {
        super(HttpStatus.CONFLICT.value(), message);
    }
}
