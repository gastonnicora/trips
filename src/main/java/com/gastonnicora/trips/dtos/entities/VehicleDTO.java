package com.gastonnicora.trips.dtos.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO utilizado para representar la información de un vehículo en las
 * respuestas de la API.
 *
 * <p>
 * Contiene los datos principales del vehículo y la información de la empresa a
 * la que pertenece.
 * </p>
 *
 * <p>
 * No incluye información sensible ni datos internos de persistencia que no sean
 * necesarios para los clientes de la API.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-09-06
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información de un vehículo")
public class VehicleDTO {

    /**
     * Identificador único del vehículo.
     */
    @Schema(
            description = "Identificador único del vehículo.",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private UUID uuid;

    /**
     * Empresa a la que pertenece el vehículo.
     */
    @Schema(
            description = "Empresa a la que pertenece el vehículo.",
            implementation = CompanyDTO.class
    )
    private CompanyDTO company;

    /**
     * Matrícula o patente del vehículo.
     */
    @Schema(
            description = "Matrícula o patente del vehículo.",
            example = "ABC123"
    )
    private String plate;

    /**
     * Modelo del vehículo.
     */
    @Schema(
            description = "Modelo del vehículo.",
            example = "Model X"
    )
    private String model;

    /**
     * Cantidad máxima de pasajeros que puede transportar el vehículo.
     */
    @Schema(
            description = "Capacidad máxima de pasajeros del vehículo.",
            example = "50"
    )
    private int capacity;

    /**
     * Fecha y hora en la que se creó el vehículo.
     */
    @Schema(
            description = "Fecha y hora de creación del vehículo.",
            example = "2026-01-01T00:00:00"
    )
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización del vehículo.
     */
    @Schema(
            description = "Fecha y hora de la última actualización del vehículo.",
            example = "2026-01-01T00:00:00"
    )
    private LocalDateTime updatedAt;

    /**
     * Indica si el vehículo se encuentra activo.
     */
    @Schema(
            description = "Indica si el vehículo se encuentra activo.",
            example = "true"
    )
    private boolean active;
}
