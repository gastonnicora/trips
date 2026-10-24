package com.gastonnicora.trips.entities;

import jakarta.persistence.*;
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

}