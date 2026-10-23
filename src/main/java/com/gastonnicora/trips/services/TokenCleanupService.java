package com.gastonnicora.trips.services;

import com.gastonnicora.trips.repositories.RefreshTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Servicio encargado de limpiar periódicamente los refresh tokens expirados o
 * inactivos de la base de datos.
 *
 * <p>
 * Utiliza tareas programadas para eliminar tokens cuya fecha de expiración ya
 * pasó y tokens que se encuentran desactivados.
 * </p>
 *
 * <p>
 * Estas tareas permiten mantener limpia la base de datos y evitar la
 * acumulación innecesaria de tokens.
 * </p>
 *
 * <p>
 * Las tareas programadas utilizan el cron {@code 0 0 * * * *}, por lo que se
 * ejecutan al inicio de cada hora.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-04-11
 */
@Service
public class TokenCleanupService {

    private final RefreshTokenRepository repo;

    /**
     * Crea una instancia del servicio de limpieza tokens de refresco e inicializa las
     * dependencias necesarias.
     *
     * @param repo Repositorio de tokens de refresco ({@link RefreshTokenRepository}) utilizado para
     *             acceder a
     *             la
     *             base de datos.
     */
    public TokenCleanupService(RefreshTokenRepository repo) {
        this.repo = repo;
    }

    /**
     * Elimina todos los refresh tokens cuya fecha de expiración es anterior al
     * momento actual.
     *
     * <p>
     * La tarea se ejecuta automáticamente al inicio de cada hora según el cron
     * definido.
     * </p>
     *
     * @see RefreshTokenRepository#deleteAllByExpiryDateBefore(Instant)
     */
    @Scheduled(cron = "0 0 * * * *")
    public void clean() {
        repo.deleteAllByExpiryDateBefore(Instant.now());
    }

    /**
     * Elimina todos los refresh tokens que se encuentran inactivos.
     *
     * <p>
     * La tarea se ejecuta automáticamente al inicio de cada hora según el cron
     * definido.
     * </p>
     *
     * @see RefreshTokenRepository#deleteAllByActiveFalse()
     */
    @Scheduled(cron = "0 0 * * * *")
    public void cleanInactive() {
        repo.deleteAllByActiveFalse();
    }
}