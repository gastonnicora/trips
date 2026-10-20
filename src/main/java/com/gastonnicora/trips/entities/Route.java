package com.gastonnicora.trips.entities;

import com.gastonnicora.trips.enums.PricingType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidad que representa una ruta perteneciente a una empresa.
 *
 * <p>
 * Una ruta está formada por una lista ordenada de puntos y define cómo se
 * calcula el precio de sus pasajes mediante {@link PricingType}.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-10-19
 */
@Entity
@Table(name = "routes")
@Getter
@Setter
@NoArgsConstructor
public class Route {

    /**
     * Identificador único de la ruta.
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "uuid", nullable = false, updatable = false)
    private UUID uuid;

    /**
     * Empresa a la que pertenece la ruta.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_uuid", nullable = false)
    private Company company;

    /**
     * Nombre de la ruta.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Forma en que se calcula el precio de los pasajes de la ruta.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "pricing_type", nullable = false)
    private PricingType pricingType;

    /**
     * Puntos de la ruta, ordenados por su posición.
     *
     * <p>
     * La posición es administrada por JPA mediante {@link OrderColumn}.
     * </p>
     */
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "position")
    private List<RoutePoint> points = new ArrayList<>();

    /**
     * Precio por kilómetro de la ruta. Se utiliza cuando el tipo de precio es
     * {@link PricingType#PER_KM}.
     */
    @OneToOne(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    private PricePerKilometer pricePerKilometer;

    /**
     * Precios por tramo de la ruta. Se utilizan cuando el tipo de precio es
     * {@link PricingType#PER_SECTION}.
     */
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SectionPrice> sectionPrices = new ArrayList<>();

    /**
     * Fecha y hora en la que se creó la ruta.
     */
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización de la ruta.
     */
    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Indica si la ruta se encuentra activa.
     */
    @Column(name = "active", nullable = false)
    private boolean active;

    /**
     * Constructor para crear una nueva ruta.
     *
     * <p>
     * Inicializa los datos básicos de la ruta y establece su estado como
     * activa.
     * </p>
     *
     * @param company     Empresa a la que pertenece la ruta.
     * @param name        Nombre de la ruta.
     * @param pricingType Forma en que se calcula el precio de los pasajes.
     */
    public Route(Company company, String name, PricingType pricingType) {
        this.company = company;
        this.name = name;
        this.pricingType = pricingType;
        this.active = true;
    }

    /**
     * Agrega un punto al final de la ruta y mantiene sincronizada la relación
     * bidireccional.
     *
     * @param point Punto a agregar.
     */
    public void addPoint(RoutePoint point) {
        point.setRoute(this);
        this.points.add(point);
    }

    /**
     * Agrega un precio por tramo a la ruta y mantiene sincronizada la relación
     * bidireccional.
     *
     * @param sectionPrice Precio por tramo a agregar.
     */
    public void addSectionPrice(SectionPrice sectionPrice) {
        sectionPrice.setRoute(this);
        this.sectionPrices.add(sectionPrice);
    }
}