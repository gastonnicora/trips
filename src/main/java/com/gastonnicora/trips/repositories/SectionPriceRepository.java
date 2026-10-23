package com.gastonnicora.trips.repositories;

import com.gastonnicora.trips.entities.SectionPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio encargado de gestionar la persistencia de entidades
 * {@link SectionPrice}.
 *
 * <p>
 * Proporciona operaciones para consultar los precios por tramo de una ruta por
 * identificador único.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-10-20
 */
public interface SectionPriceRepository extends JpaRepository<SectionPrice, UUID> {

    /**
     * Busca los precios por tramo activos de una ruta activa, perteneciente a una
     * empresa activa.
     *
     * <p>
     * La búsqueda devuelve los tramos solo si el tramo, la ruta y la empresa se
     * encuentran activos, y si la ruta pertenece a la empresa indicada. Si alguna de
     * estas condiciones no se cumple, o la ruta no tiene tramos activos, la lista
     * resultante es vacía.
     * </p>
     *
     * <p>
     * El resultado no garantiza un orden particular. El orden de los tramos dentro de
     * la ruta lo administra la relación {@code sectionPrices} de {@code Route}, mediante
     * {@code @OrderColumn}.
     * </p>
     *
     * @param companyUuid Identificador único de la empresa a la que pertenece la ruta.
     * @param routeUuid   Identificador único de la ruta cuyos tramos se desean obtener.
     *
     * @return lista de precios por tramo activos de la ruta indicada; vacía si no hay
     * coincidencias.
     */
    List<SectionPrice> findByActiveTrueAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrue(
            UUID companyUuid, UUID routeUuid);

    /**
     * Busca un precio por tramo activo por su identificador único, validando que
     * pertenezca a una ruta activa de una empresa activa.
     *
     * <p>
     * La búsqueda devuelve el precio por tramo solo si el tramo, la ruta y la empresa
     * se encuentran activos, y si la ruta pertenece a la empresa indicada. Si alguna
     * de estas condiciones no se cumple, el resultado es vacío.
     * </p>
     *
     * @param uuid        Identificador único del precio por tramo.
     * @param companyUuid Identificador único de la empresa a la que pertenece la ruta.
     * @param routeUuid   Identificador único de la ruta a la que pertenece el tramo.
     *
     * @return {@link Optional} que contiene el precio por tramo encontrado, o vacío
     * si no existe o no cumple alguna de las condiciones.
     */
    Optional<SectionPrice> findByUuidAndActiveTrueAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrue(
            UUID uuid, UUID companyUuid, UUID routeUuid);
}
