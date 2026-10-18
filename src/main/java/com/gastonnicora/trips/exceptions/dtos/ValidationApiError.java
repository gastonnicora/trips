package com.gastonnicora.trips.exceptions.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

/**
 * Representa un error de validación correspondiente al código de estado HTTP
 * {@code 400}.
 *
 * <p>
 * Se utiliza cuando los datos recibidos en una solicitud no cumplen con las
 * restricciones de validación definidas para la entrada.
 * </p>
 *
 * <p>
 * Hereda de {@link ApiError} y establece automáticamente el código de estado
 * HTTP {@link HttpStatus#BAD_REQUEST}, el mensaje correspondiente al error de
 * validación y los detalles de los errores por campo.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-04-21
 */
@Schema(
        description = "Error correspondiente a datos de entrada que no cumplen con las restricciones de validación.",
        example = """
                {
                  "status": 400,
                  "message": "Error en la validación",
                  "timestamp": "2026-05-04T12:34:56",
                  "errors": {
                    "email": ["El email no puede quedar en blanco"]
                  }
                }
                """
)
public class ValidationApiError extends ApiError {

    /**
     * Constructor para crear un error de validación con los errores detallados
     * por campo.
     *
     * <p>
     * Establece automáticamente el código de estado HTTP en {@code 400}, el
     * mensaje en {@code "Error en la validación"} y la fecha y hora actual como
     * marca temporal.
     * </p>
     *
     * @param errors Mapa que contiene los mensajes de error asociados a cada
     *               campo.
     */
    public ValidationApiError(Map<String, List<String>> errors) {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "Error en la validación",
                java.time.LocalDateTime.now(),
                errors
        );
    }
}
