package com.gastonnicora.trips.exceptions.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Clase base que representa un error de la API.
 *
 * <p>
 * Contiene información sobre el estado HTTP, el mensaje descriptivo, la fecha y
 * hora en la que se generó el error y, cuando corresponde, los detalles
 * asociados a errores específicos de campos.
 * </p>
 *
 * <p>
 * Se utiliza como estructura común para las respuestas de error de la API.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-04-21
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@Schema(description = "Clase base utilizada para representar un error de la API.")
public abstract class ApiError {

    /**
     * Código de estado HTTP asociado al error.
     */
    @Schema(
            description = "Código de estado HTTP asociado al error.",
            example = "400"
    )
    private int status;

    /**
     * Mensaje descriptivo asociado al error.
     */
    @Schema(
            description = "Mensaje descriptivo asociado al error.",
            example = "Error en la validación."
    )
    private String message;

    /**
     * Fecha y hora en la que se generó el error.
     */
    @Schema(
            description = "Fecha y hora en la que se generó el error.",
            example = "2026-05-04T12:34:56"
    )
    private LocalDateTime timestamp;

    /**
     * Mapa que contiene los errores detallados asociados a campos específicos.
     *
     * <p>
     * Cada clave identifica un campo y su valor contiene la lista de mensajes
     * correspondientes a los errores detectados.
     * </p>
     */
    @Schema(description = "Mapa de errores detallados asociados a campos específicos.")
    private Map<String, List<String>> errors;

    /**
     * Constructor simplificado para crear un error sin detalles específicos de
     * campos.
     *
     * <p>
     * Inicializa automáticamente {@code timestamp} con la fecha y hora actual.
     * </p>
     *
     * @param status  Código de estado HTTP asociado al error.
     * @param message Mensaje descriptivo asociado al error.
     */
    public ApiError(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
