package com.gastonnicora.trips.dtos.request.vehicle;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * DTO utilizado para solicitar la creación de un nuevo vehículo.
 *
 * <p>
 * Contiene la información necesaria para registrar un vehículo y asociarlo a
 * una empresa.
 * </p>
 *
 * <p>
 * Los campos cuentan con validaciones mediante Jakarta Bean Validation para
 * garantizar que se proporcione una patente, un modelo y una capacidad válida.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-09-06
 */
@Schema(description = "Datos necesarios para crear un nuevo vehículo")
@Getter
@Setter
@NoArgsConstructor
public class VehicleCreate {

    /**
     * Patente del vehículo.
     * <p>
     * Es obligatoria, no puede superar los 255 caracteres y se convierte a
     * mayúsculas al asignarse mediante el constructor o el método {@link #setPlate(String)}.
     * </p>
     */
    @NotBlank(message = "Debe introducir una patente")
    @Size(max = 255, message = "La patente no puede ser de mas de 255 caracteres")
    @Schema(
            description = "Patente del vehículo.",
            example = "ABC123",
            maxLength = 255
    )
    private String plate;

    /**
     * Modelo del vehículo.
     *
     * <p>
     * Es obligatorio y no puede superar los 255 caracteres.
     * </p>
     */
    @NotBlank(message = "Debe introducir el modelo del vehículo")
    @Size(max = 255, message = "El modelo no puede ser de mas de 255 caracteres")
    @Schema(
            description = "Modelo del vehículo.",
            example = "Model X",
            maxLength = 255
    )
    private String model;

    /**
     * Capacidad del vehículo expresada en cantidad de asientos.
     *
     * <p>
     * Es obligatoria y debe ser mayor que cero.
     * </p>
     */
    @NotNull(message = "Debe introducir la capacidad del vehículo")
    @Min(value = 1, message = "La capacidad del vehículo debe ser mayor a 0")
    @Schema(
            description = "Cantidad de asientos disponibles en el vehículo.",
            example = "50",
            minimum = "1"
    )
    private Integer capacity;


    /**
     * Crea una instancia de {@code VehicleCreate}.
     *
     * @param plate    Patente del vehículo, se eliminan espacios y convierte las letras a mayúsculas.
     * @param model    Modelo del vehículo.
     * @param capacity Capacidad del vehículo.
     */
    public VehicleCreate(String plate, String model, Integer capacity) {
        this.plate = plate.replaceAll("\\s+", "").toUpperCase();
        this.model = model;
        this.capacity = capacity;
    }

    /**
     * Establece la patente del vehículo y la convierte a mayúsculas.
     *
     * @param plate Patente que se asignará al vehículo.
     */
    public void setPlate(String plate) {
        this.plate = plate.replaceAll("\\s+", "").toUpperCase();
    }
}
