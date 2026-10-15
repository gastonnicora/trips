package com.gastonnicora.trips.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

/**
 * Entidad que representa una empresa de transporte.
 *
 * <p>
 * Contiene la información identificatoria, de contacto y ubicación de la
 * empresa, junto con las fechas de creación y última actualización y su estado
 * de actividad.
 * </p>
 *
 * <p>
 * Se utiliza para la gestión y persistencia de las empresas de transporte.
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

    /**
     * Identificador único de la empresa.
     */
    @Id
    @Column(name = "uuid", nullable = false, unique = true)
    @GeneratedValue
    @UuidGenerator
    private UUID uuid;

    /**
     * Nombre de la empresa.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Dirección de la empresa.
     */
    @Column(name = "address", nullable = false)
    private String address;

    /**
     * Latitud correspondiente a la ubicación de la empresa.
     */
    @Column(name = "latitude", nullable = false)
    private double latitude;

    /**
     * Longitud correspondiente a la ubicación de la empresa.
     */
    @Column(name = "longitude", nullable = false)
    private double longitude;

    /**
     * Dirección de correo electrónico de la empresa.
     */
    @Column(name = "email", nullable = false)
    private String email;

    /**
     * Número de teléfono de la empresa.
     */
    @Column(name = "phone", nullable = false)
    private String phone;

    /**
     * Fecha y hora en la que se creó la empresa.
     *
     * <p>
     * Su valor es gestionado automáticamente mediante
     * {@link CreationTimestamp}.
     * </p>
     */
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private java.time.LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización de la empresa.
     *
     * <p>
     * Su valor es gestionado automáticamente mediante {@link UpdateTimestamp}.
     * </p>
     */
    @Column(name = "updated_at")
    @UpdateTimestamp
    private java.time.LocalDateTime updatedAt;

    /**
     * Indica si la empresa se encuentra activa.
     */
    @Column(name = "active", nullable = false)
    private boolean active = true;

    /**
     * Constructor para crear una nueva empresa de transporte.
     *
     * <p>
     * Inicializa los datos básicos de la empresa. El identificador y las fechas
     * de auditoría son gestionados por la persistencia.
     * </p>
     *
     * @param name      Nombre de la empresa.
     * @param address   Dirección de la empresa.
     * @param latitude  Latitud de la empresa.
     * @param longitude Longitud de la empresa.
     * @param email     Dirección de correo electrónico de la empresa.
     * @param phone     Número de teléfono de la empresa.
     */
    public Company(String name, String address, double latitude, double longitude, String email,
                   String phone) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
