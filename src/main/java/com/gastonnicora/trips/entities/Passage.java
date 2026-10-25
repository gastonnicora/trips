package com.gastonnicora.trips.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
/**
 * Entidad que representa el tramo de un viaje que un pasajero reserva.
 *
 * <p>
 * Cada pasaje asocia un viaje con un usuario y define el punto de subida y el punto
 * de bajada mediante sus coordenadas geográficas, junto con el precio acordado para
 * ese tramo. El pasaje se crea activo y se da de baja de forma lógica mediante el
 * campo {@code active}.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-10-23
 */
@Entity
@Table(name = "passage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Passage {

    /**
     * Identificador único del pasaje, generado automáticamente como UUID.
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "uuid", nullable = false, updatable = false)
    private UUID uuid;

    /**
     * Viaje al que pertenece el pasaje.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_uuid", nullable = false)
    private Trip trip;

    /**
     * Pasajero que reserva el pasaje.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_uuid", nullable = false)
    private User user;

    /**
     * Precio del tramo reservado.
     */
    @Column(name = "price", nullable = false)
    private BigDecimal price;

    /**
     * Latitud del punto de subida del pasajero.
     */
    @Column(name = "fromLatitude", nullable = false)
    private Double fromLatitude;

    /**
     * Longitud del punto de subida del pasajero.
     */
    @Column(name = "fromLongitude", nullable = false)
    private Double fromLongitude;

    /**
     * Latitud del punto de bajada del pasajero.
     */
    @Column(name = "toLatitude", nullable = false)
    private Double toLatitude;

    /**
     * Longitud del punto de bajada del pasajero.
     */
    @Column(name = "toLongitude", nullable = false)
    private Double toLongitude;

    /**
     * Fecha y hora en la que se creó el pasaje.
     *
     * <p>
     * Su valor es gestionado automáticamente mediante {@link CreationTimestamp}.
     * </p>
     */
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización del pasaje.
     *
     * <p>
     * Su valor es gestionado automáticamente mediante {@link UpdateTimestamp}.
     * </p>
     */
    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Indica si el pasaje se encuentra activo. Se usa para la baja lógica.
     */
    @Column(name = "active", nullable = false)
    private boolean active;

    /**
     * Crea un nuevo pasaje para un usuario dentro de un viaje.
     *
     * <p>
     * El pasaje se crea activo. Los campos {@code uuid}, {@code createdAt} y
     * {@code updatedAt} los completa JPA al persistir.
     * </p>
     *
     * @param trip          Viaje al que pertenece el pasaje.
     * @param user          Pasajero que reserva el pasaje.
     * @param price         Precio del tramo reservado.
     * @param fromLatitude  Latitud del punto de subida.
     * @param fromLongitude Longitud del punto de subida.
     * @param toLatitude    Latitud del punto de bajada.
     * @param toLongitude   Longitud del punto de bajada.
     */
    public Passage(
            Trip trip, User user, BigDecimal price, Double fromLatitude, Double fromLongitude, Double toLatitude,
            Double toLongitude
                  ) {
        this.trip = trip;
        this.user = user;
        this.price = price;
        this.fromLatitude = fromLatitude;
        this.fromLongitude = fromLongitude;
        this.toLatitude = toLatitude;
        this.toLongitude = toLongitude;
        this.active = true;
    }
}