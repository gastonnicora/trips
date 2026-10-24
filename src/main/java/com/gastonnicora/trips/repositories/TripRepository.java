package com.gastonnicora.trips.repositories;

import com.gastonnicora.trips.entities.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para gestionar las entidades {@link Trip}.
 *
 * <p>
 * Extiende {@link JpaRepository}, por lo que hereda las operaciones básicas de
 * persistencia. Los identificadores de los viajes son de tipo {@link UUID}.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-10-23
 */
public interface TripRepository extends JpaRepository<Trip, UUID> {

    /**
     * Busca un viaje activo por su identificador único, siempre que la ruta a la
     * que pertenece esté asociada a la empresa indicada y la empresa esté activa.
     *
     * <p>
     * El viaje no tiene una relación directa con la empresa: la empresa se obtiene
     * a través de su ruta.
     * </p>
     *
     * @param uuid        Identificador único del viaje.
     * @param companyUuid Identificador único de la empresa dueña de la ruta del viaje.
     *
     * @return {@link Optional} que contiene el viaje encontrado, o vacío si el viaje
     * no existe, está inactivo, no pertenece a una ruta de la empresa indicada o la
     * empresa está inactiva.
     */
    Optional<Trip> findByUuidAndRouteCompanyUuidAndActiveTrueAndRouteCompanyActiveTrue(UUID uuid, UUID companyUuid);

    /**
     * Busca un viaje activo por su identificador único.
     *
     *
     * @param uuid Identificador único del viaje.
     *
     * @return {@link Optional} que contiene el viaje encontrado si está activo, o
     * vacío si no existe o está inactivo.
     */
    Optional<Trip> findByUuidAndActiveTrue(UUID uuid);

}
