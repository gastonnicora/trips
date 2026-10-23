package com.gastonnicora.trips.dtos.request.sectionPrice;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO utilizado para solicitar la actualización del precio de un tramo existente.
 *
 * <p>
 * Solo permite modificar el precio. Los puntos de origen y destino de un tramo
 * definen su identidad dentro de la cadena de la ruta, por lo que no se pueden
 * cambiar: para usar otros puntos se debe crear un tramo nuevo.
 * </p>
 *
 * <p>
 * El campo cuenta con validaciones mediante Jakarta Bean Validation para
 * garantizar que se proporcione un valor válido.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-10-23
 */
@Schema(description = "DTO para la actualización del precio de un tramo existente")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class SectionPriceUpdate {

    /**
     * Nuevo precio del tramo.
     * <p>
     * El campo es obligatorio y debe ser mayor o igual a 0.
     * </p>
     */
    @Schema(description = "Nuevo precio del tramo", example = "12000.0")
    @NotNull(message = "El precio del tramo es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio del tramo debe ser mayor o igual a 0")
    private BigDecimal price;
}