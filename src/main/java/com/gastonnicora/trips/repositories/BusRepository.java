package com.gastonnicora.trips.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gastonnicora.trips.entities.Bus;

/**
 * Repositorio para gestionar la entidad {@link Bus}.
 * <p>
 * Proporciona métodos para consultar autobuses por UUID y otras opciones.
 * Utiliza Spring Data JPA para el acceso a la base de datos.
 * </p>
 */
public interface BusRepository extends JpaRepository<Bus, UUID> {

    Optional<Bus> findByUuid(UUID uuid);

    List<Bus> findAllByCompanyUuid(UUID companyUuid);

    Optional<Bus> findByCompanyUuidAndPlate(UUID companyUuid, String plate);
}
