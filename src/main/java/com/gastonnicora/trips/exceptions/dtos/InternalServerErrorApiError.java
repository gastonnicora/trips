package com.gastonnicora.trips.exceptions.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

/**
 * Representa un error interno del servidor correspondiente al código de estado
 * HTTP {@code 500}.
 *
 * <p>
 * Se utiliza cuando el servidor no puede procesar una solicitud debido a un
 * error interno.
 * </p>
 *
 * <p>
 * Hereda de {@link ApiError} y establece automáticamente el código de estado
 * HTTP {@link HttpStatus#INTERNAL_SERVER_ERROR}.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-05-22
 */
@Schema(
        description = "Error correspondiente a un error interno del servidor.",
        example = """
                {
                  "status": 500,
                  "message": "Error del servidor",
                  "timestamp": "2026-05-04T12:34:56",
                  "errors": null
                }
                """
)
public class InternalServerErrorApiError extends ApiError {

    /**
     * Constructor para crear un error interno del servidor con el mensaje
     * predeterminado.
     */
    public InternalServerErrorApiError() {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error del servidor");
    }

    /**
     * Constructor para crear un error interno del servidor con un mensaje
     * personalizado.
     *
     * @param message Mensaje descriptivo del error.
     */
    public InternalServerErrorApiError(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
    }
}
