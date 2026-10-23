package com.gastonnicora.trips.dtos.request.sectionPrice;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO utilizado para solicitar la creación del precio de un tramo entre dos puntos.
 *
 * <p>
 * Contiene los identificadores del punto de origen y de destino del tramo, y el
 * precio que se cobra por recorrerlo.
 * </p>
 *
 * <p>
 * Los campos cuentan con validaciones mediante Jakarta Bean Validation para
 * garantizar que se proporcionen valores válidos.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-10-23
 */
@Schema(description = "DTO para la creación del precio de un tramo entre dos puntos")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class SectionPriceCreate {

    /**
     * Identificador del punto de origen del tramo.
     * <p>
     * El campo es obligatorio.
     * </p>
     */
    @Schema(description = "Identificador del punto de origen del tramo", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    @NotNull(message = "El punto de origen es obligatorio")
    private UUID fromPoint;

    /**
     * Identificador del punto de destino del tramo.
     * <p>
     * El campo es obligatorio.
     * </p>
     */
    @Schema(description = "Identificador del punto de destino del tramo", example = "7c9e6679-7425-40de-944b-e07fc1f90ae7")
    @NotNull(message = "El punto de destino es obligatorio")
    private UUID toPoint;

    /**
     * Precio del tramo entre el punto de origen y el de destino.
     * <p>
     * El campo es obligatorio y debe ser mayor o igual a 0.
     * </p>
     */
    @Schema(description = "Precio del tramo", example = "10000.0")
    @NotNull(message = "El precio del tramo es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio del tramo debe ser mayor o igual a 0")
    private BigDecimal price;
}