package com.gastonnicora.trips.dtos.response.company;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Address", description = "Dirección generada a partir de coordenadas")
public record AddressResponse(
                @Schema(description = "Dirección exacta", example = "Calle 123 #456") @JsonProperty("display_name") String displayName,
                @Schema(description = "Coordenadas de la dirección") Address address) {
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
