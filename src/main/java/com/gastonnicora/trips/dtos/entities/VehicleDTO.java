package com.gastonnicora.trips.dtos.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) que representa la información de un vehículo.
 * <p>
 * Se utiliza para exponer los datos de vehículo en respuestas de la API, sin
 * incluir información sensible.
 * </p>
 *
 * <p>
 * Campos principales:
 * </p>
 * <ul>
 * <li>{@code uuid}: Identificador único del vehículo.</li>
 * <li>{@code company}: DTO de la empresa a la que pertenece el vehículo.</li>
 * <li>{@code plate}: Matrícula del vehículo.</li>
 * <li>{@code model}: Modelo del vehículo.</li>
 * <li>{@code capacity}: Capacidad del vehículo.</li>
 * <li>{@code createdAt}: Fecha de creación del vehículo.</li>
 * <li>{@code updatedAt}: Fecha de última actualización del vehículo.</li>
 * <li>{@code active}: Indica si el vehículo está activo.</li>
 * </ul>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-09-04
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de vehículo.")
public class VehicleDTO {

    @Schema(description = "UUID del vehículo.", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID uuid;

    @Schema(description = "DTO de la empresa", implementation = CompanyDTO.class)
    private CompanyDTO company;

    @Schema(description = "Matrícula del vehículo.", example = "ABC123")
    private String plate;

    @Schema(description = "Modelo del vehículo.", example = "Model X")
    private String model;

    @Schema(description = "Capacidad del vehículo.", example = "50")
    private int capacity;

    @Schema(description = "Fecha de creación del vehículo.", example = "2026-01-01T00:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de actualización del vehículo.", example = "2026-01-01T00:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Indica si el vehículo está activo.", example = "true")
    private boolean active;

}
