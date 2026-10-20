package com.gastonnicora.trips.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entidad que representa el precio de un tramo de una ruta, definido entre dos
 * puntos de la misma.
 *
 * <p>
 * El servicio que cree o modifique tramos debe validar que ambos puntos
 * pertenezcan a la misma ruta y que el punto de origen esté antes que el de
 * destino.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-10-19
 */
@Entity
@Table(
        name = "section_prices",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_section_price_points",
                        columnNames = {"from_point_uuid", "to_point_uuid"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class SectionPrice {

    /**
     * Identificador único del precio por tramo.
     */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "uuid", nullable = false, updatable = false)
    private UUID uuid;

    /**
     * Ruta a la que pertenece el tramo.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_uuid", nullable = false)
    private Route route;

    /**
     * Punto de origen del tramo.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_point_uuid", nullable = false)
    private RoutePoint fromPoint;

    /**
     * Punto de destino del tramo.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_point_uuid", nullable = false)
    private RoutePoint toPoint;

    /**
     * Precio del tramo.
     */
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    /**
     * Constructor para crear un nuevo precio por tramo.
     *
     * @param route     Ruta a la que pertenece el tramo.
     * @param fromPoint Punto de origen del tramo.
     * @param toPoint   Punto de destino del tramo.
     * @param price     Precio del tramo.
     */
    public SectionPrice(Route route, RoutePoint fromPoint, RoutePoint toPoint, BigDecimal price) {
        this.route = route;
        this.fromPoint = fromPoint;
        this.toPoint = toPoint;
        this.price = price;
    }
}