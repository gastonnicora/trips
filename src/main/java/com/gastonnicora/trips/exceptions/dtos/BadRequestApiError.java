package com.gastonnicora.trips.exceptions.dtos;

import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Representa un error de solicitud incorrecta (HTTP 400).
 * <p>
 * Se utiliza cuando la petición no puede ser procesada debido a un error
 * del cliente que no está relacionado con validaciones de campos.
 * Hereda de {@link ApiError} y establece automáticamente el código de estado a
 * 400.
 * </p>
 *
 * <p>
 * Ejemplo de respuesta JSON:
 * </p>
 *
 * <pre>
 * {
 *   "status": 400,
 *   "message": "Solicitud incorrecta",
 *   "timestamp": "2026-05-04T12:34:56",
 *   "errors": null
 * }
 * </pre>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-06
 */
@Schema(description = "Solicitud incorrecta", example = """
        {
          "status": 400,
          "message": "Solicitud incorrecta",
          "timestamp": "2026-05-04T12:34:56",
          "errors": null
        }
        """)
public class BadRequestApiError extends ApiError {

    /**
     * Constructor para errores de tipo bad request sin detalle de campos.
     */
    public BadRequestApiError() {
        super(HttpStatus.BAD_REQUEST.value(), "Solicitud incorrecta");
    }

    /**
     * Constructor opcional si querés personalizar el mensaje.
     * 
     * @param message Mensaje descriptivo del error
     */
    public BadRequestApiError(String message) {
        super(HttpStatus.BAD_REQUEST.value(), message);
    }
}