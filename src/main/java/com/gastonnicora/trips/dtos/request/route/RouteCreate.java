package com.gastonnicora.trips.dtos.request.route;

import com.gastonnicora.trips.enums.PricingType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO utilizado para solicitar la creación de una nueva ruta.
 *
 * <p>
 * Contiene la información necesaria para registrar una ruta y asociarla a una
 * empresa.
 * </p>
 *
 * <p>
 * Los campos cuentan con validaciones mediante Jakarta Bean Validation para
 * garantizar que se proporcione un nombre y un tipo de precio válidos.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-10-20
 */
@Schema(description = "DTO de rutas para creación")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class RouteCreate {

    /**
     * Nombre de la ruta.
     * <p>
     * El campo es obligatorio y no puede superar los 255 caracteres.
     * </p>
     */
    @Schema(description = "Nombre de la ruta", example = "25 de Mayo - La Plata")
    @NotBlank(message = "El nombre no puede quedar en blanco")
    @Size(max = 255, message = "El nombre no puede tener mas de 255 caracteres")
    private String name;


    /**
     * Tipo de precio de la ruta.
     * <p>
     * La Ruta debe tener al menos un tipo de precio. Los tipos están definidos en {@link PricingType}.
     * </p>
     */
    @Schema(
            description = "Tipo de precio que tendrá la ruta",
            example = "PER_KM"
    )
    @NotNull(message = "La ruta debe tener asignado un tipo de precio")
    private PricingType pricingType;
}
