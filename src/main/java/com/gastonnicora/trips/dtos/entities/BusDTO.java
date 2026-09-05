package com.gastonnicora.trips.dtos.entities;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) que representa la información de un autobús.
 * <p>
 * Se utiliza para exponer los datos de autobús en respuestas de la API, sin
 * incluir información sensible.
 * </p>
 * 
 * <p>
 * Campos principales:
 * </p>
 * <ul>
 * <li>{@code uuid}: Identificador único del autobús.</li>
 * <li>{@code company}: DTO de la empresa a la que pertenece el autobús.</li>
 * <li>{@code plate}: Matrícula del autobús.</li>
 * <li>{@code model}: Modelo del autobús.</li>
 * <li>{@code capacity}: Capacidad del autobús.</li>
 * <li>{@code createdAt}: Fecha de creación del autobús.</li>
 * <li>{@code updatedAt}: Fecha de última actualización del autobús.</li>
 * <li>{@code active}: Indica si el autobús está activo.</li>
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
@Schema(description = "DTO de autobús.")
public class BusDTO {
    @Schema(description = "UUID del autobús.", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID uuid;

    @Schema(description = "DTO de la empresa", implementation = CompanyDTO.class)
    private CompanyDTO company;

    @Schema(description = "Matrícula del autobús.", example = "ABC123")
    private String plate;

    @Schema(description = "Modelo del autobús.", example = "Model X")
    private String model;

    @Schema(description = "Capacidad del autobús.", example = "50")
    private int capacity;

    @Schema(description = "Fecha de creación del autobús.", example = "2026-01-01T00:00:00")
    private String createdAt;

    @Schema(description = "Fecha de actualización del autobús.", example = "2026-01-01T00:00:00")
    private String updatedAt;

    @Schema(description = "Indica si el autobús está activo.", example = "true")
    private boolean active;

}
