package com.gastonnicora.trips.exceptions.dtos;

import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Representa un error de recurso en conflicto (HTTP 409).
 * <p>
 * Se utiliza cuando el estado actual del recurso entra en conflicto con la
 * operación solicitada,
 * por ejemplo: intento de crear un usuario con un email ya registrado.
 * </p>
 *
 * <p>
 * Ejemplo de respuesta JSON:
 * </p>
 *
 * <pre>
 * {
 *   "status": 409,
 *   "message": "Conflicto de recursos",
 *   "timestamp": "2026-05-06T12:34:56",
 *   "errors": null
 * }
 * </pre>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-05-06
 */
@Schema(description = "Conflicto de recursos", example = """
        {
          "status": 409,
          "message": "Conflicto de recursos",
          "timestamp": "2026-05-06T12:34:56",
          "errors": null
        }
        """)
public class ConflictApiError extends ApiError {

    /**
     * Constructor por defecto.
     */
    public ConflictApiError() {
        super(HttpStatus.CONFLICT.value(), "Conflicto de recursos");
    }

    /**
     * Constructor con mensaje personalizado.
     *
     * @param message Mensaje descriptivo del error
     */
    public ConflictApiError(String message) {
        super(HttpStatus.CONFLICT.value(), message);
    }
}