package com.gastonnicora.trips.dtos.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO utilizado para representar el precio de un tramo de una ruta en las
 * respuestas de la API.
 *
 * <p>
 * Un tramo queda definido por un punto de origen y un punto de destino, ambos
 * pertenecientes a la misma ruta.
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
@Schema(description = "Información del precio de un tramo de una ruta")
public class SectionPriceDTO {

    /**
     * Identificador único del precio por tramo.
     */
    @Schema(
            description = "Identificador único del precio por tramo.",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private UUID uuid;

    /**
     * Punto de origen del tramo.
     */
    @Schema(
            description = "Punto de origen del tramo.",
            implementation = RoutePointDTO.class
    )
    private RoutePointDTO fromPoint;

    /**
     * Punto de destino del tramo.
     */
    @Schema(
            description = "Punto de destino del tramo.",
            implementation = RoutePointDTO.class
    )
    private RoutePointDTO toPoint;

    /**
     * Precio del tramo.
     */
    @Schema(
            description = "Precio del tramo.",
            example = "2500.00"
    )
    private BigDecimal price;


    /**
     * Fecha y hora en la que se creó el tramo.
     */
    @Schema(
            description = "Fecha y hora de creación del tramo.",
            example = "2026-01-01T00:00:00"
    )
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización del tramo.
     */
    @Schema(
            description = "Fecha y hora de la última actualización del tramo.",
            example = "2026-01-01T00:00:00"
    )
    private LocalDateTime updatedAt;

    /**
     * Indica si el tramo se encuentra activo.
     */
    @Schema(
            description = "Indica si el tramo se encuentra activo.",
            example = "true"
    )
    private boolean active;
}