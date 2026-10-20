package com.gastonnicora.trips.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entidad que representa el precio por kilómetro de una ruta.
 *
 * <p>
 * El precio se compone de un tramo base (los primeros {@code baseKm}
 * kilómetros cuestan {@code basePrice}) y un valor por cada kilómetro
 * adicional ({@code pricePerKm}).
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-10-19
 */
@Entity
@Table(name = "price_per_kilometer")
@Getter
@Setter
@NoArgsConstructor
public class PricePerKilometer {

    /**
     * Identificador único del precio por kilómetro.
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "uuid", nullable = false, updatable = false)
    private UUID uuid;

    /**
     * Ruta a la que pertenece el precio.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_uuid", nullable = false, unique = true)
    private Route route;

    /**
     * Cantidad de kilómetros incluidos en el precio base.
     */
    @Column(name = "base_km", nullable = false, precision = 12, scale = 4)
    private BigDecimal baseKm;

    /**
     * Precio del tramo base.
     */
    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;

    /**
     * Precio de cada kilómetro adicional al tramo base.
     */
    @Column(name = "price_per_km", nullable = false, precision = 12, scale = 4)
    private BigDecimal pricePerKm;

    /**
     * Constructor para crear un nuevo precio por kilómetro.
     *
     * @param route      Ruta a la que pertenece el precio.
     * @param baseKm     Kilómetros incluidos en el precio base.
     * @param basePrice  Precio del tramo base.
     * @param pricePerKm Precio de cada kilómetro adicional.
     */
    public PricePerKilometer(Route route, BigDecimal baseKm, BigDecimal basePrice, BigDecimal pricePerKm) {
        this.route = route;
        this.baseKm = baseKm;
        this.basePrice = basePrice;
        this.pricePerKm = pricePerKm;
    }
}