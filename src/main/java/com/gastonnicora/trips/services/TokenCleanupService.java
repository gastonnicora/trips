package com.gastonnicora.trips.services;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.gastonnicora.trips.repositories.RefreshTokenRepository;

/**
 * Servicio programado para limpiar tokens expirados o inactivos de la base de
 * datos.
 * <p>
 * Se utilizan tareas programadas (cron) para eliminar:
 * </p>
 * <ul>
 * <li>Tokens cuya fecha de expiración ya pasó.</li>
 * <li>Tokens que están desactivados (no activos).</li>
 * </ul>
 * <p>
 * Esto ayuda a mantener la base de datos limpia y evitar acumulación
 * innecesaria de tokens.
 * </p>
 * 
 * Cron de ejemplo utilizado: "0 0 * * * *" → se ejecuta al inicio de cada hora.
 * 
 * Autor: Gastón
 * Versión: 1.0
 * Desde: 2026-05-04
 */
@Service
public class TokenCleanupService {

    private final RefreshTokenRepository repo;

    TokenCleanupService(RefreshTokenRepository repo) {
        this.repo = repo;
    }

    /**
     * Elimina todos los refresh tokens cuya fecha de expiración es anterior al
     * momento actual.
     * <p>
     * Se ejecuta automáticamente cada hora según el cron definido.
     * </p>
     */
    @Scheduled(cron = "0 0 * * * *")
    public void clean() {
        repo.deleteAllByExpiryDateBefore(Instant.now());
    }

    /**
     * Elimina todos los refresh tokens que están inactivos.
     * <p>
     * Se ejecuta automáticamente cada hora según el cron definido.
     * </p>
     */
    @Scheduled(cron = "0 0 * * * *")
    public void cleanInactive() {
        repo.deleteAllByActiveFalse();
    }
}