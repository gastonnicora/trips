package com.gastonnicora.trips.entities;

import com.gastonnicora.trips.enums.TypeTrip;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad que representa un viaje programado dentro de una ruta.
 *
 * <p>
 * Cada viaje asocia una ruta con una fecha y hora de salida, el vehículo que
 * realiza el recorrido y el conductor asignado. Un mismo vehículo no puede
 * tener dos viajes con la misma fecha, y un mismo conductor tampoco.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-10-23
 */
@Entity
@Table(
        name = "trips",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_trips_vehicle_date",
                        columnNames = {"vehicle_uuid", "date"}
                ),
                @UniqueConstraint(
                        name = "uk_trips_driver_date",
                        columnNames = {"driver_uuid", "date"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trip {

    /**
     * Identificador único del viaje, generado automáticamente como UUID.
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "uuid", nullable = false, updatable = false)
    private UUID uuid;

    /**
     * Ruta a la que pertenece el viaje.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_uuid", nullable = false)
    private Route route;

    /**
     * Fecha y hora de salida del viaje.
     */
    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    /**
     * Vehículo asignado para realizar el viaje.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_uuid", nullable = false)
    private Vehicle vehicle;

    /**
     * Conductor asignado al viaje.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_uuid", nullable = false)
    private User driver;

    /**
     * Tipo del viaje: {@link TypeTrip#TOUR} recorre solo el camino de la ruta;
     * {@link TypeTrip#DOMICILE} puede desviarse para buscar pasajeros a domicilio.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeTrip type;

    /**
     * Distancia máxima de desvío para buscar pasajeros a domicilio. Solo tiene
     * valor si el tipo del viaje es {@link TypeTrip#DOMICILE}.
     */
    @Column(name = "distance_to_route")
    private Integer distanceToRoute;

    /**
     * Fecha y hora en la que se creó el viaje.
     */
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización del viaje.
     */
    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Indica si el viaje se encuentra activo. Se usa para la baja lógica.
     */
    @Column(name = "active", nullable = false)
    private boolean active;

    /**
     * Crea un nuevo viaje asociado a una ruta, un vehículo y un conductor.
     *
     * <p>
     * El viaje se crea activo. Los campos {@code uuid}, {@code createdAt} y
     * {@code updatedAt} los completa JPA al persistir.
     * </p>
     *
     * @param route           Ruta a la que pertenece el viaje.
     * @param date            Fecha y hora de salida del viaje.
     * @param vehicle         Vehículo que realizará el viaje.
     * @param driver          Conductor asignado al viaje.
     * @param type            Tipo del viaje ({@link TypeTrip}).
     * @param distanceToRoute Distancia máxima de desvío para buscar pasajeros a
     *                        domicilio. Debe ser {@code null} si el tipo es
     *                        {@link TypeTrip#TOUR}.
     */
    public Trip(Route route, LocalDateTime date, Vehicle vehicle, User driver, TypeTrip type, Integer distanceToRoute) {
        this.route = route;
        this.date = date;
        this.vehicle = vehicle;
        this.driver = driver;
        this.type = type;
        this.distanceToRoute = distanceToRoute;
        this.active = true;
    }

}