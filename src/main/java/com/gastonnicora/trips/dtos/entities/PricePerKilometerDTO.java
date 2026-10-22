package com.gastonnicora.trips.dtos.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO utilizado para representar el precio por kilómetro de una ruta en las
 * respuestas de la API.
 *
 * <p>
 * El precio se compone de un tramo base (los primeros {@code baseKm}
 * kilómetros cuestan {@code basePrice}) y un valor por cada kilómetro
 * adicional ({@code pricePerKm}).
 * </p>
 *
 * <p>
 * No incluye la ruta a la que pertenece para evitar referencias circulares.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-10-20
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información del precio por kilómetro de una ruta")
public class PricePerKilometerDTO {

    /**
     * Identificador único del precio por kilómetro.
     */
    @Schema(
            description = "Identificador único del precio por kilómetro.",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private UUID uuid;

    /**
     * Cantidad de kilómetros incluidos en el precio base.
     */
    @Schema(
            description = "Kilómetros incluidos en el precio base.",
            example = "5.0"
    )
    private BigDecimal baseKm;

    /**
     * Precio del tramo base.
     */
    @Schema(
            description = "Precio del tramo base.",
            example = "1500.00"
    )
    private BigDecimal basePrice;

    /**
     * Precio de cada kilómetro adicional al tramo base.
     */
    @Schema(
            description = "Precio de cada kilómetro adicional al tramo base.",
            example = "120.5"
    )
    private BigDecimal pricePerKm;


    /**
     * Fecha y hora en la que se creó el precio por kilómetro.
     */
    @Schema(
            description = "Fecha y hora de creación del precio por kilómetro.",
            example = "2026-01-01T00:00:00"
    )
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización del precio por kilómetro.
     */
    @Schema(
            description = "Fecha y hora de la última actualización del precio por kilómetro.",
            example = "2026-01-01T00:00:00"
    )
    private LocalDateTime updatedAt;

    /**
     * Indica si el precio por kilómetro se encuentra activa.
     */
    @Schema(
            description = "Indica si el precio por kilometro se encuentra activo.",
            example = "true"
    )
    private boolean active;
}