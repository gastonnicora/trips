package com.gastonnicora.trips.exceptions.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Representa un error de autenticación (HTTP 401).
 * <p>
 * Se utiliza cuando un usuario no está autenticado o el token es inválido/expirado.
 * Hereda de {@link ApiError} y establece automáticamente el código de estado a 401.
 * </p>
 *
 * <p>
 * Ejemplo de respuesta JSON:
 * </p>
 *
 * <pre>
 * {
 *   "status": 401,
 *   "message": "Token inválido o expirado",
 *   "timestamp": "2023-05-04T12:34:56",
 *   "errors": null
 * }
 * </pre>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2023-05-04
 */
@Schema(description = "Error de autenticación")
public class UnauthorizedApiError extends ApiError {


    @Schema(description = "Estatus de respuesta", example = "401")
    private int status;
    @Schema(description = "Mensaje de error", example = "Token inválido o expirado")
    private String message;
    @Schema(description = "Cuando se genero el error")
    private LocalDateTime timestamp;
    @Schema(description = "Listado de errores", example = "null")
    private Map<String, List<String>> errors;
    
    /**
     * Constructor que inicializa el mensaje de error.
     * <p>
     * Establece automáticamente el {@code status} a 401, el {@code timestamp} a la fecha
     * y hora actual, y {@code errors} a null.
     * </p>
     *
     * @param message Mensaje descriptivo del error
     */
    public UnauthorizedApiError(String message) {
        super(401, message);
    }
}