package com.gastonnicora.trips.exceptions.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

/**
 * Representa un error de solicitud incorrecta correspondiente al código de
 * estado HTTP {@code 400}.
 *
 * <p>
 * Se utiliza cuando una solicitud no puede ser procesada debido a un error
 * asociado a la petición del cliente que no corresponde a una validación
 * específica de campos.
 * </p>
 *
 * <p>
 * Hereda de {@link ApiError} y establece automáticamente el código de estado
 * HTTP {@link HttpStatus#BAD_REQUEST}.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-06
 */
@Schema(
        description = "Error correspondiente a una solicitud incorrecta.",
        example = """
                {
                  "status": 400,
                  "message": "Solicitud incorrecta",
                  "timestamp": "2026-05-04T12:34:56",
                  "errors": null
                }
                """
)
public class BadRequestApiError extends ApiError {

    /**
     * Constructor para crear un error de solicitud incorrecta con el mensaje
     * predeterminado.
     */
    public BadRequestApiError() {
        super(HttpStatus.BAD_REQUEST.value(), "Solicitud incorrecta");
    }

    /**
     * Constructor para crear un error de solicitud incorrecta con un mensaje
     * personalizado.
     *
     * @param message Mensaje descriptivo del error.
     */
    public BadRequestApiError(String message) {
        super(HttpStatus.BAD_REQUEST.value(), message);
    }
}
