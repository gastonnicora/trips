package com.gastonnicora.trips.exceptions.dtos;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Representa un error de validación (HTTP 400).
 * <p>
 * Se utiliza cuando la entrada de datos no cumple con las restricciones de
 * validación.
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
 *   "message": "Error en la validación",
 *   "timestamp": "2023-05-04T12:34:56",
 *   "errors": {
 *     "email": ["El email no puede quedar en blanco"]
 *   }
 * }
 * </pre>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2023-05-04
 */
@Schema(description = "Error de validación")
public class ValidationApiError extends ApiError {

    /**
     * Constructor que inicializa los errores de validación.
     * <p>
     * Establece automáticamente {@code status} a 400, {@code message} a "Error en
     * la validación",
     * {@code timestamp} a la fecha y hora actual y {@code errors} al mapa
     * proporcionado.
     * </p>
     *
     * @param errors Mapa de campos con sus mensajes de error
     */
    public ValidationApiError(Map<String, List<String>> errors) {
        super(400, "Error en la validación", java.time.LocalDateTime.now(), errors);
    }
}