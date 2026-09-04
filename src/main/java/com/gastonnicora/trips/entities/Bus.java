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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa un autobús.
 * <p>
 * Contiene información sobre el autobús, como su identificador, modelo,
 * capacidad y estado.
 * </p>
 * <p>
 * Campos principales:
 * </p>
 * <ul>
 * <li>{@code uuid}: Identificador único del autobús.</li>
 * <li>{@code company}: Empresa a la que pertenece el autobús.</li>
 * <li>{@code plate}: Matrícula del autobús.</li>
 * <li>{@code model}: Modelo del autobús.</li>
 * <li>{@code capacity}: Capacidad del autobús.</li>
 * <li>{@code createdAt}: Fecha de creación del autobús.</li>
 * <li>{@code updatedAt}: Fecha de última actualización del autobús.</li>
 * <li>{@code active}: Indica si el autobús está activo.</li>
 * </ul>
 *
 * <p>
 * Se utiliza para la gestión de autobuses.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-20
 */
@Entity
@Table(name = "buses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bus {

    @Id
    @Column(name = "uuid", nullable = false, unique = true)
    @GeneratedValue
    @UuidGenerator
    private UUID uuid;

    @ManyToOne(optional = false)
    @Column(name = "company_uuid", nullable = false)
    private Company company;

    @Column(name = "plate", nullable = false)
    private String plate;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "active", nullable = false)
    private Boolean active;

    /**
     * Constructor para crear un nuevo autobús.
     *
     * @param company  La empresa a la que pertenece el autobús.
     * @param model    El modelo del autobús.
     * @param capacity La capacidad del autobús.
     * @param status   El estado del autobús.
     */
    public Bus(Company company, String plate, String model, Integer capacity) {
        this.company = company;
        this.plate = plate;
        this.model = model;
        this.capacity = capacity;
        this.active = true;
    }
}
