package com.gastonnicora.trips.exceptions.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Representa un error genérico en la API.
 * <p>
 * Se utiliza para encapsular información sobre errores ocurridos en las
 * operaciones, incluyendo estado HTTP, mensaje, timestamp y detalles de
 * validación.
 * </p>
 * <p>
 * Campos principales:
 * </p>
 * <ul>
 *   <li>{@code status}: Código HTTP del error.</li>
 *   <li>{@code message}: Mensaje descriptivo del error.</li>
 *   <li>{@code timestamp}: Fecha y hora en que se generó el error.</li>
 *   <li>{@code errors}: Map de errores detallados (por ejemplo, validaciones de campos).</li>
 * </ul>
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
@Getter
@Setter
@ToString
@AllArgsConstructor
@Schema(description = "Error genérico de la API")
public class ApiError {

    @Schema(description = "Código HTTP de la respuesta", example = "400")
    private int status;

    @Schema(description = "Mensaje descriptivo del error", example = "Error en la validación")
    private String message;

    @Schema(description = "Fecha y hora en que se generó el error")
    private LocalDateTime timestamp;

    @Schema(description = "Listado de errores detallados por campo",
            example = "{\"email\":[\"El email no puede quedar en blanco\"]}")
    private Map<String, List<String>> errors;

    /**
     * Constructor simplificado para errores sin detalle de campos.
     * <p>
     * Inicializa el {@code timestamp} automáticamente con la fecha y hora actual.
     * </p>
     *
     * @param status Código HTTP del error
     * @param message Mensaje descriptivo del error
     */
    public ApiError(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}