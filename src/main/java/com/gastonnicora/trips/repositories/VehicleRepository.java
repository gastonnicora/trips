package com.gastonnicora.trips.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gastonnicora.trips.entities.Vehicle;

/**
 * Repositorio para gestionar la entidad {@link Vehicle}.
 * <p>
 * Proporciona métodos para consultar vehículos por UUID y otras opciones.
 * Utiliza Spring Data JPA para el acceso a la base de datos.
 * </p>
 */
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    Optional<Vehicle> findByUuid(UUID uuid);

    List<Vehicle> findAllByCompanyUuid(UUID companyUuid);

    Optional<Vehicle> findByCompanyUuidAndPlate(UUID companyUuid, String plate);
}
