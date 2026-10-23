package com.gastonnicora.trips.dtos.request.routePoint;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO utilizado para solicitar la creación de un nuevo punto de ruta.
 *
 * <p>
 * Contiene la información necesaria para registrar un punto de ruta y asociarlo a una ruta.
 * </p>
 *
 * <p>
 * Los campos cuentan con validaciones mediante Jakarta Bean Validation para
 * garantizar que los campos proporcionados sean válidos.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-10-21
 */
@Schema(description = "DTO de puntos de rutas para creación")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class RoutePointCreate {
    /**
     * Nombre de la ruta.
     * <p>
     * El campo es obligatorio y no puede superar los 255 caracteres.
     * </p>
     */
    @Schema(description = "Nombre del punto ruta", example = "25 de Mayo")
    @NotBlank(message = "El nombre no puede quedar en blanco")
    @Size(max = 255, message = "El nombre no puede tener mas de 255 caracteres")
    private String name;

    /**
     * Latitud geográfica de la ubicación del punto de ruta.
     * <p>
     * El campo es obligatorio y debe encontrarse dentro del rango válido de
     * latitudes, comprendido entre -90 y 90 grados.
     * </p>
     */
    @NotNull(message = "La latitud es obligatoria")
    @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90")
    @DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90")
    @Schema(description = "Latitud de la dirección del punto de ruta", example = "-34.6037")
    private Double latitude;

    /**
     * Longitud geográfica de la ubicación del punto de ruta.
     * <p>
     * El campo es obligatorio y debe encontrarse dentro del rango válido de
     * longitudes, comprendido entre -180 y 180 grados.
     * </p>
     */
    @NotNull(message = "La longitud es obligatoria")
    @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180")
    @DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180")
    @Schema(description = "Longitud de la dirección del punto de ruta", example = "-58.3816")
    private Double longitude;
}
