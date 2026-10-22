package com.gastonnicora.trips.dtos.request.pricePerKilometer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO utilizado para solicitar la creación de una configuración de precio por kilómetro.
 *
 * <p>
 * Contiene la información necesaria para definir un precio base que cubre una
 * cantidad determinada de kilómetros, y un precio adicional por cada kilómetro
 * que exceda ese tramo.
 * </p>
 *
 * <p>
 * Los campos cuentan con validaciones mediante Jakarta Bean Validation para
 * garantizar que se proporcionen valores válidos.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-10-22
 */
@Schema(description = "DTO para la creación de una configuración de precio por kilómetro")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class PricePerKilometerCreate {

    /**
     * Cantidad de kilómetros incluidos en el precio base.
     * <p>
     * El campo es obligatorio y debe ser mayor o igual a 0.
     * </p>
     */
    @Schema(description = "Cantidad de kilómetros incluidos en el precio base", example = "100.0")
    @NotNull(message = "La cantidad de kilómetros incluidos en el precio base es obligatorio")
    @DecimalMin(value = "0.0", message = "La cantidad de kilómetros incluidos en el precio base debe ser mayor o igual a 0")
    private BigDecimal baseKm;

    /**
     * Precio del tramo base, es decir, el costo de recorrer los kilómetros incluidos.
     * <p>
     * El campo es obligatorio y debe ser mayor o igual a 0.
     * </p>
     */
    @Schema(description = "Precio del tramo base", example = "10000.0")
    @NotNull(message = "El precio del tramo base es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio del tramo base debe ser mayor o igual a 0")
    private BigDecimal basePrice;

    /**
     * Precio por cada kilómetro adicional al tramo base.
     * <p>
     * El campo es obligatorio y debe ser mayor o igual a 0.
     * </p>
     */
    @Schema(description = "Precio de cada kilómetro adicional al tramo base", example = "10000.0")
    @NotNull(message = "El precio de cada kilómetro adicional al tramo base es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio de cada kilómetro adicional al tramo base debe ser mayor o igual a 0")
    private BigDecimal pricePerKm;

}