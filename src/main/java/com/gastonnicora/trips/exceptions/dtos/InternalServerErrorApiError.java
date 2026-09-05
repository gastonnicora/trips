package com.gastonnicora.trips.exceptions.dtos;

import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Representa un error del Servidor (HTTP 500).
 * <p>
 * Se utiliza cuando el servidor no puede procesar la solicitud debido a un
 * error interno. Hereda de {@link ApiError} y establece automáticamente el
 * código de estado a 500.
 * </p>
 *
 * <p>
 * Ejemplo de respuesta JSON:
 * </p>
 *
 * <pre>
 * {
 *   "status": 500,
 *   "message": "Error del servidor",
 *   "timestamp": "2026-05-04T12:34:56",
 *   "errors": null
 * }
 * </pre>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-22
 */
@Schema(description = "Error del servidor", example = """
            {
              "status": 500,
              "message": "Error del servidor",
              "timestamp": "2026-05-04T12:34:56",
              "errors": null
              }
        """)
public class InternalServerErrorApiError extends ApiError {

    /**
     * Constructor por defecto.
     */
    public InternalServerErrorApiError() {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error del servidor");
    }

    /**
     * Constructor con mensaje personalizado.
     *
     * @param message Mensaje específico del error
     */
    public InternalServerErrorApiError(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
    }
}
