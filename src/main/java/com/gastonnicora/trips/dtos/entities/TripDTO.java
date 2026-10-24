package com.gastonnicora.trips.dtos.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO que representa un viaje programado dentro de una ruta.
 *
 * <p>
 * Expone la ruta, el vehículo y el conductor asignados, junto con la fecha de
 * salida y los datos de auditoría del viaje.
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
@Schema(description = "Información del viaje")
public class TripDTO {

    /**
     * Identificador único del viaje.
     */
    @Schema(
            description = "Identificador único del viaje.",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private UUID uuid;

    /**
     * Ruta a la que pertenece el viaje.
     */
    @Schema(
            description = "Ruta a la que pertenece el viaje.",
            implementation = RouteDTO.class
    )
    private RouteDTO route;

    /**
     * Vehículo que realizará el viaje.
     */
    @Schema(
            description = "Vehículo que realizará el viaje.",
            implementation = VehicleDTO.class
    )
    private VehicleDTO vehicle;

    /**
     * Conductor asignado al viaje.
     */
    @Schema(
            description = "Conductor asignado al viaje.",
            implementation = UserDTO.class
    )
    private UserDTO driver;

    /**
     * Fecha y hora de salida del viaje.
     */
    @Schema(
            description = "Fecha y hora en la que se va a realizar el viaje.",
            example = "2026-01-01T00:00:00"
    )
    private LocalDateTime date;

    /**
     * Fecha y hora en la que se creó el viaje.
     */
    @Schema(
            description = "Fecha y hora de creación del viaje.",
            example = "2026-01-01T00:00:00"
    )
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización del viaje.
     */
    @Schema(
            description = "Fecha y hora de la última actualización del viaje.",
            example = "2026-01-01T00:00:00"
    )
    private LocalDateTime updatedAt;

    /**
     * Indica si el viaje se encuentra activo.
     */
    @Schema(
            description = "Indica si el viaje se encuentra activo.",
            example = "true"
    )
    private boolean active;
}