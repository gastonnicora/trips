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
 * Entidad que representa un punto de una ruta.
 *
 * <p>
 * El orden del punto dentro de la ruta lo administra
 * mediante {@link OrderColumn}, por lo que no se define una posición en esta
 * entidad.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-10-19
 */
@Entity
@Table(name = "route_points")
@Getter
@Setter
@NoArgsConstructor
public class RoutePoint {

    /**
     * Identificador único del punto.
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "uuid", nullable = false, updatable = false)
    private UUID uuid;

    /**
     * Ruta a la que pertenece el punto.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_uuid", nullable = false)
    private Route route;

    /**
     * Nombre del punto (por ejemplo, el nombre de una parada).
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Latitud del punto.
     */
    @Column(name = "latitude", nullable = false)
    private Double latitude;

    /**
     * Longitud del punto.
     */
    @Column(name = "longitude", nullable = false)
    private Double longitude;


    /**
     * Fecha y hora en la que se creó el punto de ruta.
     */
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización del punto de ruta.
     */
    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Indica si el punto ruta se encuentra activo.
     */
    @Column(name = "active", nullable = false)
    private boolean active;

    /**
     * Constructor para crear un nuevo punto.
     *
     * @param name      Nombre del punto.
     * @param latitude  Latitud del punto.
     * @param longitude Longitud del punto.
     */
    public RoutePoint(String name, Double latitude, Double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.active = true;
    }
}