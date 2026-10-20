package com.gastonnicora.trips.dtos.entities;

import com.gastonnicora.trips.enums.PricingType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO utilizado para representar una ruta en las respuestas de la API.
 *
 * <p>
 * Contiene los datos principales de la ruta, la empresa a la que pertenece, sus
 * puntos ordenados y la información de precios.
 * </p>
 *
 * <p>
 * Según el tipo de precio ({@link PricingType}) se completa solo uno de los dos
 * esquemas: {@code pricePerKilometer} para {@link PricingType#PER_KM} o
 * {@code sectionPrices} para {@link PricingType#PER_SECTION}.
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
@Schema(description = "Información de una ruta")
public class RouteDTO {

    /**
     * Identificador único de la ruta.
     */
    @Schema(
            description = "Identificador único de la ruta.",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private UUID uuid;

    /**
     * Empresa a la que pertenece la ruta.
     */
    @Schema(
            description = "Empresa a la que pertenece la ruta.",
            implementation = CompanyDTO.class
    )
    private CompanyDTO company;

    /**
     * Nombre de la ruta.
     */
    @Schema(
            description = "Nombre de la ruta.",
            example = "La Plata - Buenos Aires"
    )
    private String name;

    /**
     * Forma en que se calcula el precio de los pasajes de la ruta.
     */
    @Schema(
            description = "Forma en que se calcula el precio de los pasajes.",
            example = "PER_KM"
    )
    private PricingType pricingType;

    /**
     * Puntos de la ruta, ordenados según su posición.
     */
    @Schema(
            description = "Puntos de la ruta, ordenados según su posición."
    )
    private List<RoutePointDTO> points;

    /**
     * Precio por kilómetro de la ruta. Solo se completa cuando el tipo de
     * precio es {@link PricingType#PER_KM}.
     */
    @Schema(
            description = "Precio por kilómetro. Solo se completa si el tipo de precio es PER_KM.",
            implementation = PricePerKilometerDTO.class,
            nullable = true
    )
    private PricePerKilometerDTO pricePerKilometer;

    /**
     * Precios por tramo de la ruta. Solo se completan cuando el tipo de precio
     * es {@link PricingType#PER_SECTION}.
     */
    @Schema(
            description = "Precios por tramo. Solo se completan si el tipo de precio es PER_SECTION."
    )
    private List<SectionPriceDTO> sectionPrices;

    /**
     * Fecha y hora en la que se creó la ruta.
     */
    @Schema(
            description = "Fecha y hora de creación de la ruta.",
            example = "2026-01-01T00:00:00"
    )
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización de la ruta.
     */
    @Schema(
            description = "Fecha y hora de la última actualización de la ruta.",
            example = "2026-01-01T00:00:00"
    )
    private LocalDateTime updatedAt;

    /**
     * Indica si la ruta se encuentra activa.
     */
    @Schema(
            description = "Indica si la ruta se encuentra activa.",
            example = "true"
    )
    private boolean active;
}