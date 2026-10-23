package com.gastonnicora.trips.repositories;

import com.gastonnicora.trips.entities.PricePerKilometer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


/**
 * Repositorio encargado de gestionar la persistencia de entidades
 * {@link PricePerKilometer}.
 *
 * <p>
 * Proporciona operaciones para consultar la configuración de precio por
 * kilómetro de una ruta por identificador único.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-10-20
 */
public interface PricePerKilometerRepository extends JpaRepository<PricePerKilometer, UUID> {

    /**
     * Busca la configuración de precio por kilómetro activa de una ruta activa,
     * perteneciente a una empresa activa.
     *
     * <p>
     * La búsqueda devuelve la configuración solo si la configuración, la ruta y
     * la empresa se encuentran activas, y si la ruta pertenece a la empresa
     * indicada.
     * </p>
     *
     * @param companyUuid Identificador único de la empresa a la que pertenece la ruta.
     * @param routeUuid   Identificador único de la ruta a la que pertenece el precio.
     *
     * @return {@link Optional} que contiene la configuración encontrada, o vacío si
     * no existe o no cumple alguna de las condiciones.
     */
    Optional<PricePerKilometer> findByRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndActiveTrue(
            UUID companyUuid, UUID routeUuid);


    /**
     * Busca la configuración de precio por kilómetro por su identificador único,
     * validando que pertenezca a una ruta activa de una empresa activa.
     *
     * <p>
     * La búsqueda devuelve la configuración solo si la configuración, la ruta y
     * la empresa se encuentran activas, y si la ruta pertenece a la empresa
     * indicada.
     * </p>
     *
     * @param uuid        Identificador único de la configuración de precio por kilómetro.
     * @param companyUuid Identificador único de la empresa a la que pertenece la ruta.
     * @param routeUuid   Identificador único de la ruta a la que pertenece el precio.
     *
     * @return {@link Optional} que contiene la configuración encontrada, o vacío si
     * no existe o no cumple alguna de las condiciones.
     */
    Optional<PricePerKilometer> findByUuidAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndActiveTrue(
            UUID uuid, UUID companyUuid, UUID routeUuid);
}