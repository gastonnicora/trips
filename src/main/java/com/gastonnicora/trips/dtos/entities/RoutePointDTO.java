package com.gastonnicora.trips.dtos.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

/**
 * DTO utilizado para representar un punto de una ruta en las respuestas de la
 * API.
 *
 * <p>
 * Contiene los datos del punto (nombre y coordenadas). El orden del punto
 * dentro de la ruta está dado por su posición en la lista
 * {@link RouteDTO#getPoints()}.
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
@Schema(description = "Información de un punto de una ruta")
public class RoutePointDTO {

    /**
     * Identificador único del punto.
     */
    @Schema(
            description = "Identificador único del punto.",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private UUID uuid;

    /**
     * Nombre del punto (por ejemplo, el nombre de una parada).
     */
    @Schema(
            description = "Nombre del punto.",
            example = "Terminal La Plata"
    )
    private String name;

    /**
     * Latitud geográfica del punto.
     */
    @Schema(
            description = "Latitud geográfica del punto.",
            example = "-34.9214"
    )
    private Double latitude;

    /**
     * Longitud geográfica del punto.
     */
    @Schema(
            description = "Longitud geográfica del punto.",
            example = "-57.9545"
    )
    private Double longitude;
}