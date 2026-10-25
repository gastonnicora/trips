package com.gastonnicora.trips.dtos.request.passage;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * DTO utilizado para solicitar la creación de un pasaje dentro de un viaje.
 *
 * <p>
 * Indica el viaje y el pasajero, junto con el punto de subida y el punto de bajada
 * del tramo, expresados como coordenadas geográficas. Todos los campos son
 * obligatorios. Las coordenadas se validan con rangos válidos de latitud
 * ({@code -90} a {@code 90}) y de longitud ({@code -180} a {@code 180}).
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-10-23
 */
@Schema(description = "DTO de pasajes para creación")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class PassageCreate {

    /**
     * Identificador del viaje al que se asocia el pasaje.
     */
    @Schema(
            description = "UUID del viaje al que pertenece el pasaje.",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    @NotNull(message = "El pasaje debe tener un viaje asignado")
    private UUID tripUuid;

    /**
     * Identificador del pasajero que reserva el pasaje.
     */
    @Schema(
            description = "UUID del pasajero que reserva el pasaje.",
            example = "f47ac10b-58cc-4372-a567-0e02b2c3d479"
    )
    private UUID userUuid;

    /**
     * Latitud del punto de subida del pasajero.
     */
    @Schema(
            description = "Latitud del punto de subida del pasajero.",
            example = "-34.9214",
            minimum = "-90",
            maximum = "90"
    )
    @NotNull(message = "El pasaje debe tener una latitud de subida")
    @DecimalMin(value = "-90.0", message = "La latitud de subida debe estar entre -90 y 90")
    @DecimalMax(value = "90.0", message = "La latitud de subida debe estar entre -90 y 90")
    private Double fromLatitude;

    /**
     * Longitud del punto de subida del pasajero.
     */
    @Schema(
            description = "Longitud del punto de subida del pasajero.",
            example = "-57.9544",
            minimum = "-180",
            maximum = "180"
    )
    @NotNull(message = "El pasaje debe tener una longitud de subida")
    @DecimalMin(value = "-180.0", message = "La longitud de subida debe estar entre -180 y 180")
    @DecimalMax(value = "180.0", message = "La longitud de subida debe estar entre -180 y 180")
    private Double fromLongitude;

    /**
     * Latitud del punto de bajada del pasajero.
     */
    @Schema(
            description = "Latitud del punto de bajada del pasajero.",
            example = "-34.6037",
            minimum = "-90",
            maximum = "90"
    )
    @NotNull(message = "El pasaje debe tener una latitud de bajada")
    @DecimalMin(value = "-90.0", message = "La latitud de bajada debe estar entre -90 y 90")
    @DecimalMax(value = "90.0", message = "La latitud de bajada debe estar entre -90 y 90")
    private Double toLatitude;

    /**
     * Longitud del punto de bajada del pasajero.
     */
    @Schema(
            description = "Longitud del punto de bajada del pasajero.",
            example = "-58.3816",
            minimum = "-180",
            maximum = "180"
    )
    @NotNull(message = "El pasaje debe tener una longitud de bajada")
    @DecimalMin(value = "-180.0", message = "La longitud de bajada debe estar entre -180 y 180")
    @DecimalMax(value = "180.0", message = "La longitud de bajada debe estar entre -180 y 180")
    private Double toLongitude;

}