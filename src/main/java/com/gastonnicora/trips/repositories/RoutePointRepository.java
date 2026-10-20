package com.gastonnicora.trips.repositories;

import com.gastonnicora.trips.entities.RoutePoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio encargado de gestionar la persistencia de entidades
 * {@link RoutePoint}.
 *
 * <p>
 * Proporciona operaciones para consultar los puntos de una ruta por
 * identificador único.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-10-20
 */
public interface RoutePointRepository extends JpaRepository<RoutePoint, UUID> {

    /**
     * Busca un punto de ruta por su identificador único.
     *
     * @param uuid Identificador único del punto de ruta.
     *
     * @return {@link Optional} que contiene el punto de ruta encontrado, o vacío si
     * no existe.
     */
    Optional<RoutePoint> findByUuid(UUID uuid);
}
