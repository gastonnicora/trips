package com.gastonnicora.trips.repositories;

import com.gastonnicora.trips.entities.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


/**
 * Repositorio encargado de gestionar la persistencia de entidades
 * {@link Route}.
 *
 * <p>
 * Proporciona operaciones para consultar rutas por identificador único y estado
 * de actividad.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-10-20
 */
public interface RouteRepository extends JpaRepository<Route, UUID> {

    /**
     * Busca una ruta activa por su identificador único.
     *
     * @param uuid Identificador único de la ruta.
     *
     * @return {@link Optional} que contiene la ruta activa encontrada, o vacío si
     * no existe.
     */
    Optional<Route> findByUuidAndActiveTrue(UUID uuid);
}
