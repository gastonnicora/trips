package com.gastonnicora.trips.dtos.response.company;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Address", description = "Dirección generada a partir de coordenadas")
public record AddressResponse(
                @Schema(description = "Dirección exacta", example = "Calle 123 #456") @JsonProperty("display_name") String displayName,
                @Schema(description = "Dirección desglosada") Address address) {
        /**
         * Dirección desglosada.
         * <p>
         * Cuenta con los siguientes campos:
         * </p>
         * <ul>
         * <li>{@code road}: Calle</li>
         * <li>{@code number}: Número de calle</li>
         * <li>{@code suburb}: Barrio</li>
         * <li>{@code city}: Ciudad</li>
         * <li>{@code department}: Departamento</li>
         * <li>{@code state}: Estado</li>
         * <li>{@code country}: País</li>
         * </ul>
         * 
         * @author Gastón
         * @version 1.0
         * @since 2026-05-21
         */
        @Schema(description = "Dirección desglosada")
        public record Address(
                        @Schema(description = "Dirección exacta", example = "Calle 123 #456") String road,
                        @Schema(description = "Número de calle", example = "123") String number,
                        @Schema(description = "Barrio", example = "Centro") String suburb,
                        @Schema(description = "Ciudad", example = "Bogotá") String city,
                        @Schema(description = "Departamento", example = "Cundinamarca") String department,
                        @Schema(description = "Estado", example = "Cundinamarca") String state,
                        @Schema(description = "País", example = "Colombia") String country) {
        }

}
