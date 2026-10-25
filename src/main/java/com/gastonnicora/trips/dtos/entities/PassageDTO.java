package com.gastonnicora.trips.dtos.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO que representa un pasaje reservado por un usuario dentro de un viaje.
 *
 * <p>
 * Expone el viaje y el usuario asociados, el precio del tramo, las coordenadas de
 * subida y de bajada, y los datos de auditoría del pasaje.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-10-23
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información de un pasaje")
public class PassageDTO {

    /**
     * Identificador único del pasaje.
     */
    @Schema(
            description = "Identificador único del pasaje.",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private UUID uuid;

    /**
     * Viaje al que pertenece el pasaje.
     */
    @Schema(
            description = "Viaje al que pertenece el pasaje.",
            implementation = TripDTO.class
    )
    private TripDTO trip;

    /**
     * Pasajero que reservó el pasaje.
     */
    @Schema(
            description = "Pasajero que reservó el pasaje.",
            implementation = UserDTO.class
    )
    private UserDTO user;

    /**
     * Precio del tramo reservado.
     */
    @Schema(
            description = "Precio del tramo reservado.",
            example = "1500.00"
    )
    private BigDecimal price;

    /**
     * Latitud del punto de subida del pasajero.
     */
    @Schema(
            description = "Latitud del punto de subida del pasajero.",
            example = "-34.9214"
    )
    private Double fromLatitude;

    /**
     * Longitud del punto de subida del pasajero.
     */
    @Schema(
            description = "Longitud del punto de subida del pasajero.",
            example = "-57.9544"
    )
    private Double fromLongitude;

    /**
     * Latitud del punto de bajada del pasajero.
     */
    @Schema(
            description = "Latitud del punto de bajada del pasajero.",
            example = "-34.6037"
    )
    private Double toLatitude;

    /**
     * Longitud del punto de bajada del pasajero.
     */
    @Schema(
            description = "Longitud del punto de bajada del pasajero.",
            example = "-58.3816"
    )
    private Double toLongitude;

    /**
     * Fecha y hora en la que se creó el pasaje.
     */
    @Schema(
            description = "Fecha y hora de creación del pasaje.",
            example = "2026-01-01T00:00:00"
    )
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización del pasaje.
     */
    @Schema(
            description = "Fecha y hora de la última actualización del pasaje.",
            example = "2026-01-01T00:00:00"
    )
    private LocalDateTime updatedAt;

    /**
     * Indica si el pasaje se encuentra activo.
     */
    @Schema(
            description = "Indica si el pasaje se encuentra activo.",
            example = "true"
    )
    private boolean active;
}
