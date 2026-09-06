package com.gastonnicora.trips.dtos.request.vehicle;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de vehículo para Request
 */
@Schema(description = "DTO de vehículo para Request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleCreate {

    @NotBlank(message = "Debe introducir una patente")
    @Size(max = 255, message = "La patente no puede ser de mas de 255 caracteres")
    @Schema(description = "Patente del vehículo", example = "ABC123")
    private String plate;

    @NotBlank(message = "Debe introducir el modelo del vehículo")
    @Size(max = 255, message = "El modelo no puede ser de mas de 255 caracteres")
    @Schema(description = "Modelo del vehículo", example = "Model X")
    private String model;

    @NotNull(message = "Debe introducir la capacidad del vehículo")
    @Min(value = 1, message = "La capacidad del vehículo debe ser mayor a 0")
    @Schema(description = "Cantidad de asientos del vehículo", example = "50")
    private Integer capacity;

}
