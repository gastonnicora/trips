package com.gastonnicora.trips.entities;

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
 * Entidad que representa un vehículo asociado a una empresa.
 *
 * <p>
 * Contiene la información identificatoria del vehículo, la empresa a la que
 * pertenece, su matrícula, modelo, capacidad, estado y fechas de creación y
 * última actualización.
 * </p>
 *
 * <p>
 * Se utiliza para la gestión y persistencia de los vehículos asociados a las
 * empresas.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-09-06
 */
@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    /**
     * Identificador único del vehículo.
     */
    @Id
    @Column(name = "uuid", nullable = false, unique = true)
    @GeneratedValue
    @UuidGenerator
    private UUID uuid;

    /**
     * Empresa a la que pertenece el vehículo.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "company_uuid", nullable = false)
    private Company company;

    /**
     * Matrícula o patente del vehículo.
     */
    @Column(name = "plate", nullable = false)
    private String plate;

    /**
     * Modelo del vehículo.
     */
    @Column(name = "model", nullable = false)
    private String model;

    /**
     * Capacidad del vehículo.
     */
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    /**
     * Fecha y hora en la que se creó el vehículo.
     *
     * <p>
     * Su valor es gestionado automáticamente mediante
     * {@link CreationTimestamp}.
     * </p>
     */
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización del vehículo.
     *
     * <p>
     * Su valor es gestionado automáticamente mediante {@link UpdateTimestamp}.
     * </p>
     */
    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Indica si el vehículo se encuentra activo.
     */
    @Column(name = "active", nullable = false)
    private boolean active;

    /**
     * Constructor para crear un nuevo vehículo.
     *
     * <p>
     * Inicializa los datos básicos del vehículo y establece su estado como
     * activo.
     * </p>
     *
     * @param company  Empresa a la que pertenece el vehículo.
     * @param plate    Matrícula o patente del vehículo.
     * @param model    Modelo del vehículo.
     * @param capacity Capacidad del vehículo.
     */
    public Vehicle(Company company, String plate, String model, Integer capacity) {
        this.company = company;
        this.plate = plate;
        this.model = model;
        this.capacity = capacity;
        this.active = true;
    }
}
