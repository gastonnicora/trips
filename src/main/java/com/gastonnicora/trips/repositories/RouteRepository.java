package com.gastonnicora.trips.repositories;

import com.gastonnicora.trips.entities.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
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
     * Busca todas las rutas activas asociadas a una empresa activa.
     *
     * @param companyUuid Identificador único de la empresa activa.
     *
     * @return Lista de rutas activas de la empresa indicada, o una lista vacía
     * si no tiene ninguna.
     */
    List<Route> findAllByCompanyUuidAndCompanyActiveTrueAndActiveTrue(UUID companyUuid);


    /**
     * Busca una ruta activa por su uuid y el uuid de la empresa activa a la que pertenece.
     *
     * @param routeUuid   Identificador único de la ruta.
     * @param companyUuid Identificador único de la empresa.
     *
     * @return {@link Optional} que contiene la ruta activa encontrada, o vacío si
     * no existe.
     */
    Optional<Route> findByUuidAndCompanyUuidAndCompanyActiveTrueAndActiveTrue(UUID routeUuid, UUID companyUuid);
}
