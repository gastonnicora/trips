package com.gastonnicora.trips.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa un vehículo.
 * <p>
 * Contiene información sobre el vehículo, como su identificador, modelo,
 * capacidad y estado.
 * </p>
 * <p>
 * Campos principales:
 * </p>
 * <ul>
 * <li>{@code uuid}: Identificador único del vehículo.</li>
 * <li>{@code company}: Empresa a la que pertenece el vehículo.</li>
 * <li>{@code plate}: Matrícula del vehículo.</li>
 * <li>{@code model}: Modelo del vehículo.</li>
 * <li>{@code capacity}: Capacidad del vehículo.</li>
 * <li>{@code createdAt}: Fecha de creación del vehículo.</li>
 * <li>{@code updatedAt}: Fecha de última actualización del vehículo.</li>
 * <li>{@code active}: Indica si el vehículo está activo.</li>
 * </ul>
 *
 * <p>
 * Se utiliza para la gestión de vehículos.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-09-04
 */
@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @Column(name = "uuid", nullable = false, unique = true)
    @GeneratedValue
    @UuidGenerator
    private UUID uuid;

    @ManyToOne(optional = false)
    @JoinColumn(name = "company_uuid", nullable = false)
    private Company company;

    @Column(name = "plate", nullable = false)
    private String plate;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "active", nullable = false)
    private boolean active;

    /**
     * Constructor para crear un nuevo vehículo.
     *
     * @param company La empresa a la que pertenece el vehículo.
     * @param plate La matrícula del vehículo.
     * @param model El modelo del vehículo.
     * @param capacity La capacidad del vehículo.
     */
    public Vehicle(Company company, String plate, String model, Integer capacity) {
        this.company = company;
        this.plate = plate;
        this.model = model;
        this.capacity = capacity;
        this.active = true;
    }
}
