package com.gastonnicora.trips.exceptions.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
 *   "timestamp": "2023-05-04T12:34:56",
 *   "errors": null
 * }
 * </pre>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2023-05-04
 */
@Schema(description = "Error de acceso prohibido")
public class ForbiddenApiError extends ApiError {

    @Schema(description = "Estatus de respuesta", example = "403")
    private int status;
    @Schema(description = "Mensaje de error", example = "Acceso denegado")
    private String message;
    @Schema(description = "Cuando se genero el error")
    private LocalDateTime timestamp;
    @Schema(description = "Listado de errores", example = "null")
    private Map<String, List<String>> errors;

    /**
     * Constructor que inicializa el mensaje de error.
     * <p>
     * Establece automáticamente el {@code status} a 403, el {@code timestamp} a la
     * fecha
     * y hora actual, y {@code errors} a null.
     * </p>
     * 
     * @param message Mensaje descriptivo del error
     */
    public ForbiddenApiError(String message) {
        super(403, message);
    }
}