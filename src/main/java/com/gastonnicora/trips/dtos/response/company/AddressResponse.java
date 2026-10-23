package com.gastonnicora.trips.dtos.response.company;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO utilizado para representar una dirección obtenida a partir de coordenadas
 * geográficas.
 *
 * <p>
 * Contiene la dirección completa y sus diferentes componentes, permitiendo
 * exponer de forma estructurada la información obtenida mediante un servicio de
 * geocodificación.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-05-21
 */
@Schema(
        name = "Address",
        description = "Dirección generada a partir de coordenadas geográficas"
)
public record AddressResponse(
        @Schema(
                description = "Dirección completa generada a partir de las coordenadas.",
                example = "Calle 123 #456"
        )
        @JsonProperty("display_name")
        String displayName,
        @Schema(
                description = "Dirección desglosada en sus diferentes componentes.",
                implementation = Address.class
        )
        Address address) {

    /**
     * DTO que representa los componentes individuales de una dirección.
     *
     * <p>
     * Contiene la calle, número, barrio, ciudad, departamento, estado y país
     * correspondientes a una ubicación geográfica.
     * </p>
     *
     * @author Gastón Nicora
     * @version 1.0
     * @since 2026-05-21
     */
    @Schema(description = "Componentes individuales de una dirección")
    public record Address(
            @Schema(
                    description = "Nombre de la calle.",
                    example = "Calle 123"
            )
            String road,
            @Schema(
                    description = "Número de la calle.",
                    example = "123"
            )
            String number,
            @Schema(
                    description = "Barrio o zona de la dirección.",
                    example = "Centro"
            )
            String suburb,
            @Schema(
                    description = "Ciudad correspondiente a la dirección.",
                    example = "Bogotá"
            )
            String city,
            @Schema(
                    description = "Departamento correspondiente a la dirección.",
                    example = "Cundinamarca"
            )
            String department,
            @Schema(
                    description = "Estado o provincia correspondiente a la dirección.",
                    example = "Cundinamarca"
            )
            String state,
            @Schema(
                    description = "País correspondiente a la dirección.",
                    example = "Colombia"
            )
            String country) {

    }
}
