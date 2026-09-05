package com.gastonnicora.trips.dtos.request.bus;

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
 * DTO de bus para Request
 */
@Schema(description = "DTO de bus para Request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusCreate {

    @NotBlank(message = "Debe introducir una placa")
    @Size(max = 255, message = "La placa no puede ser de mas de 255 caracteres")
    @Schema(description = "Placa del bus", example = "ABC123")
    private String plate;

    @NotBlank(message = "Debe introducir el modelo del bus")
    @Size(max = 255, message = "El modelo no puede ser de mas de 255 caracteres")
    @Schema(description = "Modelo del bus", example = "Model X")
    private String model;

    @NotNull(message = "Debe introducir la capacidad del bus")
    @Min(value = 1, message = "La capacidad del bus debe ser mayor a 0")
    @Schema(description = "Cantidad de asientos del bus", example = "50")
    private Integer capacity;

}
