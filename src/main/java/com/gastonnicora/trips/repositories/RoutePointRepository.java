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
     * Busca un punto de ruta activo que pertenezca a una ruta activa de una empresa activa.
     *
     * <p>
     * La búsqueda verifica en conjunto que el punto de ruta, su ruta y la empresa
     * a la que pertenece la ruta estén activos, y que la ruta pertenezca a la
     * empresa indicada. Si alguna de estas condiciones no se cumple, el resultado
     * es vacío.
     * </p>
     *
     * @param companyUuid    Identificador único de la empresa a la que debe pertenecer la ruta.
     * @param routeUuid      Identificador único de la ruta a la que debe pertenecer el punto.
     * @param routePointUuid Identificador único del punto de ruta.
     *
     * @return {@link Optional} que contiene el punto de ruta encontrado, o vacío si no
     * existe o si no cumple todas las condiciones de actividad y pertenencia.
     */
    Optional<RoutePoint> findByRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndUuidAndActiveTrue(
            UUID companyUuid, UUID routeUuid, UUID routePointUuid);
}
