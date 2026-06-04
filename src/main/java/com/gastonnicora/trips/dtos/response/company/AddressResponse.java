package com.gastonnicora.trips.dtos.response.company;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Address", description = "Dirección generada a partir de coordenadas")
public record AddressResponse(
                @Schema(description = "Dirección exacta", example = "Calle 123 #456") @JsonProperty("display_name") String displayName,
                @Schema(description = "Dirección desglosada") Address address) {
        /**
         * Dirección desglosada.
         * 
         * @param road Calle
         * @param number Número de calle
         * @param suburb Barrio
         * @param city Ciudad
         * @param department Departamento
         * @param state Estado
         * @param country País
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
