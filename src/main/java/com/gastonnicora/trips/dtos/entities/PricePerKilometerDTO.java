package com.gastonnicora.trips.dtos.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
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
}