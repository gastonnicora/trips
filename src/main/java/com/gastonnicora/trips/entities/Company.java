package com.gastonnicora.trips.entities;

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
 * Representa una empresa de transporte.
 * <p>
 * Contiene información sobre la empresa, el dueño de la misma, dirección y
 * información de contacto (email y teléfono).
 * </p>
 * <p>
 * Campos principales:
 * </p>
 * <ul>
 * <li>{@code uuid}: Identificador único de la empresa.</li>
 * <li>{@code name}: Nombre de la empresa.</li>
 * <li>{@code owner}: Dueño de la empresa.</li>
 * <li>{@code address}: Dirección de la empresa.</li>
 * <li>{@code email}: Dirección de correo electrónico de la empresa.</li>
 * <li>{@code phone}: Número de teléfono de la empresa.</li>
 * <li>{@code createdAt}: Fecha de creación de la empresa.</li>
 * <li>{@code updatedAt}: Fecha de última actualización de la empresa.</li>
 * <li>{@code active}: Indica si la empresa está activa.</li>
 * </ul>
 * 
 * <p>
 * Se utiliza para la gestión de empresas de transporte.
 * </p>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-05-20
 */
@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    @Id
    @Column(name = "uuid", nullable = false, unique = true)
    @GeneratedValue
    @UuidGenerator
    private UUID uuid;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_uuid", nullable = false)
    private User owner;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private java.time.LocalDateTime updatedAt;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    /**
     * Constructor para crear una nueva empresa de transporte.
     * <p>
     * Genera una nueva {@code Company}
     * </p>
     * 
     * @param name      Nombre de la empresa
     * @param owner     Dueño de la empresa
     * @param address   Dirección de la empresa
     * @param latitude  Latitud de la empresa
     * @param longitude Longitud de la empresa
     * @param email     Dirección de correo electrónico de la empresa
     * @param phone     Número de teléfono de la empresa
     */
    public Company(String name, User owner, String address, double latitude, double longitude, String email,
            String phone) {
        this.name = name;
        this.owner = owner;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
