package com.gastonnicora.trips.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Entidad que representa un token de refresco asociado a una sesión de usuario.
 *
 * <p>
 * Contiene el token de acceso, el refresh token, el usuario asociado, la
 * información del cliente desde el que se creó la sesión, su estado, las fechas
 * de creación y expiración y la versión del token.
 * </p>
 *
 * <p>
 * Se utiliza para la gestión de sesiones de usuario y la renovación de tokens
 * de acceso en la aplicación.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-04-21
 */
@Entity
@Table(name = "refreshTokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    /**
     * Identificador único del token de refresco.
     */
    @Id
    @Column(name = "uuid", nullable = false, unique = true)
    @GeneratedValue
    @UuidGenerator
    private UUID uuid;

    /**
     * Token JWT de acceso asociado a la sesión.
     */
    @Column(name = "token", nullable = false, columnDefinition = "TEXT")
    private String token;

    /**
     * Token único utilizado para renovar el token de acceso.
     */
    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    /**
     * Usuario asociado a la sesión y al token de refresco.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_uuid", nullable = false)
    private User user;

    /**
     * Dirección IP desde la que se creó la sesión.
     */
    @Column(name = "ip", nullable = false)
    private String ip;

    /**
     * Información del navegador, aplicación o cliente utilizado para crear la
     * sesión.
     */
    @Column(name = "user_agent", nullable = false)
    private String userAgent;

    /**
     * Tipo de dispositivo desde el que se creó la sesión.
     */
    @Column(name = "device")
    private String device;

    /**
     * Indica si el token de refresco se encuentra activo.
     */
    @Column(name = "active", nullable = false)
    private boolean active;

    /**
     * Fecha y hora en la que se creó el token de refresco.
     *
     * <p>
     * Su valor es gestionado automáticamente mediante {@link CreationTimestamp}.
     * </p>
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Fecha y hora en la que expira el token de refresco.
     */
    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    /**
     * Versión actual del token de refresco.
     */
    @Column(name = "version", nullable = false)
    private int version;

    /**
     * Constructor para crear un nuevo token de refresco.
     *
     * <p>
     * Genera automáticamente un refresh token mediante {@link UUID}, establece
     * el token como activo y define su fecha de expiración a siete días a partir
     * del momento de creación.
     * </p>
     *
     * <p>
     * Si el {@code userAgent} recibido es {@code null} o está vacío, se utiliza
     * {@code "web"} como valor predeterminado.
     * </p>
     *
     * @param token     Token JWT de acceso asociado.
     * @param user      Usuario asociado al token de refresco.
     * @param ip        Dirección IP desde la que se crea el token.
     * @param userAgent Información del cliente utilizado para crear el token.
     * @param device    Tipo de dispositivo desde el que se crea el token.
     * @param version   Versión inicial del token.
     */
    public RefreshToken(String token, User user, String ip, String userAgent, String device, int version) {
        this.token = token;
        this.refreshToken = UUID.randomUUID().toString();
        this.active = true;
        this.user = user;
        this.ip = ip;
        this.userAgent = (userAgent != null && !userAgent.isBlank()) ? userAgent : "web";
        this.device = device;
        this.expiryDate = Instant.now().plus(7, ChronoUnit.DAYS);
        this.version = version;
    }

    /**
     * Incrementa en uno la versión actual del token de refresco.
     */
    public void addVersion() {
        this.version++;
    }

}
