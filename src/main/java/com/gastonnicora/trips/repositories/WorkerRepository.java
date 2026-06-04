package com.gastonnicora.trips.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gastonnicora.trips.entities.Worker;

/**
 * Repositorio para gestionar la entidad {@link Worker}.
 * <p>
 * Proporciona métodos para consultar workers por UUID entre otras opciones
 * Utiliza Spring Data JPA para el acceso a la base de datos.
 * </p>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-06-03
 */
public interface WorkerRepository extends JpaRepository<Worker, UUID> {

    /**
     * Busca un worker por su UUID y la empresa asociada.
     * 
     * @param userUuid    UUID del usuario
     * @param companyUuid UUID de la empresa
     * @return {@link Optional} con el worker si existe
     */
    Optional<Worker> findByUserUuidAndCompanyUuid(
            UUID userUuid,
            UUID companyUuid);
}
