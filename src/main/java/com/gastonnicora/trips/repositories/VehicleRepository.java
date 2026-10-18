package com.gastonnicora.trips.repositories;

import com.gastonnicora.trips.entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio encargado de gestionar la persistencia de entidades
 * {@link Vehicle}.
 *
 * <p>
 * Proporciona operaciones para consultar vehículos por identificador único,
 * empresa, patente y estado de actividad.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-09-06
 */
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    /**
     * Busca todos los vehículos asociados a una empresa.
     *
     * @param companyUuid Identificador único de la empresa.
     *
     * @return Lista de vehículos asociados a la empresa indicada.
     */
    List<Vehicle> findAllByCompanyUuid(UUID companyUuid);

    /**
     * Busca un vehículo por el identificador de su empresa y su patente.
     *
     * @param companyUuid Identificador único de la empresa.
     * @param plate       Patente del vehículo.
     *
     * @return {@link Optional} que contiene el vehículo encontrado, o vacío si no
     * existe.
     */
    Optional<Vehicle> findByCompanyUuidAndPlate(UUID companyUuid, String plate);

    /**
     * Busca un vehículo por el identificador de su empresa y su patente
     * únicamente si se encuentra activo.
     *
     * @param companyUuid Identificador único de la empresa.
     * @param plate       Patente del vehículo.
     *
     * @return {@link Optional} que contiene el vehículo activo encontrado, o vacío
     * si no existe.
     */
    Optional<Vehicle> findByCompanyUuidAndPlateAndActiveTrue(UUID companyUuid, String plate);

    /**
     * Busca un vehículo por su identificador único.
     *
     * @param uuid Identificador único del vehículo.
     *
     * @return {@link Optional} que contiene el vehículo encontrado, o vacío si no
     * existe.
     */
    Optional<Vehicle> findByUuid(UUID uuid);

    /**
     * Busca un vehículo por su identificador único únicamente si se encuentra
     * activo.
     *
     * @param uuid Identificador único del vehículo.
     *
     * @return {@link Optional} que contiene el vehículo activo encontrado, o vacío
     * si no existe.
     */
    Optional<Vehicle> findByUuidAndActiveTrue(UUID uuid);
}