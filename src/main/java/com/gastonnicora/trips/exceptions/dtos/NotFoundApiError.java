package com.gastonnicora.trips.exceptions.dtos;

import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Representa un error de recurso no encontrado (HTTP 404).
 * <p>
 * Se utiliza cuando el recurso solicitado no existe.
 * Hereda de {@link ApiError} y establece automáticamente el código de estado a
 * 404.
 * </p>
 *
 * <p>
 * Ejemplo de respuesta JSON:
 * </p>
 *
 * <pre>
 * {
 *   "status": 404,
 *   "message": "Recurso no encontrado",
 *   "timestamp": "2026-05-04T12:34:56",
 *   "errors": null
 * }
 * </pre>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-05-06
 */
@Schema(description = "Recurso no encontrado", example = """
        {
          "status": 404,
          "message": "Recurso no encontrado",
          "timestamp": "2026-05-04T12:34:56",
          "errors": null
        }
        """)
public class NotFoundApiError extends ApiError {

    /**
     * Constructor por defecto.
     */
    public NotFoundApiError() {
        super(HttpStatus.NOT_FOUND.value(), "Recurso no encontrado");
    }

    /**
     * Constructor con mensaje personalizado.
     *
     * @param message Mensaje específico del error
     */
    public NotFoundApiError(String message) {
        super(HttpStatus.NOT_FOUND.value(), message);
    }
}