package com.gastonnicora.trips.dtos.request.trip;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gastonnicora.trips.enums.TypeTrip;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO utilizado para solicitar la creación de un viaje programado.
 *
 * <p>
 * Asocia una ruta con una fecha y hora de salida, el vehículo que realizará el
 * recorrido y el conductor asignado. Todos los campos son obligatorios.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-10-23
 */
@Schema(description = "DTO de viajes para creación")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class TripCreate {

    /**
     * Identificador de la ruta a la que pertenece el viaje.
     */
    @Schema(
            description = "UUID de la ruta a la que pertenece el viaje.",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    @NotNull(message = "El viaje debe tener asignada una ruta")
    private UUID routeUuid;

    /**
     * Fecha y hora de salida del viaje.
     */
    @Schema(
            description = "Fecha y hora de salida del viaje.",
            example = "2026-11-01T08:00:00"
    )
    @Future(message = "La fecha de salida debe ser posterior a la fecha y hora actual")
    @NotNull(message = "El viaje debe tener una fecha y hora de salida")
    private LocalDateTime date;

    /**
     * Identificador del vehículo que realizará el viaje.
     */
    @Schema(
            description = "UUID del vehículo que realizará el viaje.",
            example = "7c9e6679-7425-40de-944b-e07fc1f90ae7"
    )
    @NotNull(message = "El viaje debe tener asignado un vehículo")
    private UUID vehicleUuid;

    /**
     * Identificador del conductor asignado al viaje.
     */
    @Schema(
            description = "UUID del conductor asignado al viaje.",
            example = "f47ac10b-58cc-4372-a567-0e02b2c3d479"
    )
    @NotNull(message = "El viaje debe tener asignado un conductor")
    private UUID driverUuid;

    /**
     * Tipo del viaje. Es obligatorio.
     * <p>
     * {@link TypeTrip#TOUR} recorre solo el camino de la ruta;
     * {@link TypeTrip#DOMICILE} puede desviarse para buscar pasajeros a domicilio.
     * </p>
     */
    @Schema(
            description = "Tipo del viaje. TOUR recorre solo la ruta; DOMICILE puede desviarse para buscar pasajeros.",
            example = "DOMICILE"
    )
    @NotNull(message = "El viaje debe tener un tipo")
    private TypeTrip type;

    /**
     * Distancia máxima de desvío para buscar pasajeros a domicilio.
     * <p>
     * Es obligatoria y mayor que cero solo si el tipo es {@link TypeTrip#DOMICILE}.
     * Debe ser {@code null} si el tipo es {@link TypeTrip#TOUR}.
     * </p>
     */
    @Schema(
            description = "Distancia máxima de desvío en kilómetros. Obligatoria solo si el tipo es DOMICILE.",
            example = "5",
            nullable = true
    )
    private Integer distanceToRoute;

    /**
     * Verifica que {@link #distanceToRoute} sea coherente con {@link #type}.
     * <p>
     * Para {@link TypeTrip#DOMICILE} exige un valor mayor que cero; para
     * {@link TypeTrip#TOUR} exige que no se envíe valor. Si el tipo es {@code null},
     * se devuelve {@code true} porque {@link #type} ya informa ese error.
     * </p>
     *
     * @return {@code true} si la combinación de tipo y distancia es válida.
     */
    @JsonIgnore
    @AssertTrue(message = "Los viajes a domicilio deben tener una distancia mayor que cero, y los tours no deben tener distancia")
    public boolean isDistanceToRouteValid() {
        if (type == null) {
            return true;
        }
        if (type == TypeTrip.DOMICILE) {
            return distanceToRoute != null && distanceToRoute > 0;
        }
        return distanceToRoute == null;
    }
}
