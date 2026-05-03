package com.gastonnicora.trips.entities;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "refreshTokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @Column(name = "uuid", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Column(name = "token", nullable = false,columnDefinition = "TEXT")
    private String token;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @Column(name = "user_uuid", nullable = false)
    private UUID userUuid;

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

    public RefreshToken(String token, UUID userUuid, String ip, String userAgent, String device, int version) {
        this.token = token;
        this.refreshToken = UUID.randomUUID().toString();
        this.active = true;
        this.userUuid = userUuid;
        this.ip = ip;
        this.userAgent = (userAgent != null && !userAgent.isBlank()) ? userAgent : "web";
        this.device = device;
        this.expiryDate = Instant.now().plus(7, ChronoUnit.DAYS);
        this.version = version;
    }

    public void addVersion() {
        this.version++;
    }

}
