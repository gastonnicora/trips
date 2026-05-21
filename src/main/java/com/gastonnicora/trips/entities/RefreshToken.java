package com.gastonnicora.trips.entities;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
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
 * Representa un token de refresco para mantener sesiones de usuario.
 * <p>
 * Contiene información sobre el token de acceso, token de refresco, usuario
 * asociado,
 * dispositivo y fecha de expiración.
 * </p>
 * <p>
 * Campos principales:
 * </p>
 * <ul>
 * <li>{@code uuid}: Identificador único del token.</li>
 * <li>{@code token}: Token JWT de acceso.</li>
 * <li>{@code refreshToken}: Token único de refresco.</li>
 * <li>{@code user}: Usuario asociado al refreshToken.</li>
 * <li>{@code ip}: Dirección IP desde donde se creó el token.</li>
 * <li>{@code userAgent}: Información del navegador o cliente.</li>
 * <li>{@code device}: Tipo de dispositivo (web, android, etc.).</li>
 * <li>{@code active}: Indica si el token está activo.</li>
 * <li>{@code createdAt}: Fecha de creación del token.</li>
 * <li>{@code expiryDate}: Fecha de expiración del token.</li>
 * <li>{@code version}: Versión del token (incrementa al actualizar).</li>
 * </ul>
 * 
 * <p>
 * Se utiliza para la gestión de sesiones y renovación de tokens en la
 * aplicación.
 * </p>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
@Entity
@Table(name = "refreshTokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @Column(name = "uuid", nullable = false, unique = true)
    @GeneratedValue
    @UuidGenerator
    private UUID uuid;

    @Column(name = "token", nullable = false, columnDefinition = "TEXT")
    private String token;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_uuid", nullable = false)
    private User user;

    @Column(name = "ip", nullable = false)
    private String ip;

    @Column(name = "user_agent", nullable = false)
    private String userAgent;

    @Column(name = "device")
    private String device;

    @Column(name = "active", nullable = false)
    private boolean active;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    @Column(name = "version", nullable = false)
    private int version;

    /**
     * Constructor para crear un nuevo RefreshToken.
     * <p>
     * Genera automáticamente un {@code refreshToken} único y define la fecha de
     * expiración
     * a 7 días a partir de la creación.
     * </p>
     * 
     * @param token     JWT de acceso asociado
     * @param user      usuario relacionado con el token
     * @param ip        Dirección IP desde donde se crea el token
     * @param userAgent Información del cliente (navegador, app, etc.)
     * @param device    Tipo de dispositivo (web, android, etc.)
     * @param version   Versión inicial del token
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
     * Incrementa la versión del token.
     */
    public void addVersion() {
        this.version++;
    }

}