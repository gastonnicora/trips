package com.gastonnicora.trips.dtos.request.trip;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * DTO utilizado para actualizar los datos de un viaje existente.
 *
 * <p>
 * Permite reasignar el vehículo y el conductor que realizarán el viaje. Ambos
 * campos son obligatorios: la actualización reemplaza las asignaciones actuales.
 * </p>
 *
 * <p>
 * Los datos se validan mediante anotaciones de Bean Validation antes de llegar
 * a la capa de servicio.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-10-23
 */
@Schema(description = "DTO de viajes para actualización")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class TripUpdate {

    /**
     * Identificador único (UUID) del vehículo que realizará el viaje.
     */
    @Schema(
            description = "UUID del vehículo que realizará el viaje.",
            example = "7c9e6679-7425-40de-944b-e07fc1f90ae7"
    )
    @NotNull(message = "El viaje debe tener asignado un vehículo")
    private UUID vehicleUuid;

    /**
     * Identificador único (UUID) del conductor asignado al viaje.
     */
    @Schema(
            description = "UUID del conductor asignado al viaje.",
            example = "f47ac10b-58cc-4372-a567-0e02b2c3d479"
    )
    @NotNull(message = "El viaje debe tener asignado un conductor")
    private UUID driverUuid;

}