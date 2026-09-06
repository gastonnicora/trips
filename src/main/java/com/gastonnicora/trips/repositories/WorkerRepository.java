package com.gastonnicora.trips.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gastonnicora.trips.entities.Worker;
import com.gastonnicora.trips.enums.RoleCompany;

/**
 * Repositorio para gestionar la entidad {@link Worker}.
 * <p>
 * Proporciona métodos para consultar trabajadores por UUID entre otras opciones
 * Utiliza Spring Data JPA para el acceso a la base de datos.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-06-03
 */
public interface WorkerRepository extends JpaRepository<Worker, UUID> {

    /**
     * Busca un trabajador por su UUID y la empresa asociada.
     *
     * @param userUuid UUID del usuario
     * @param companyUuid UUID de la empresa
     * @return {@link Optional} con el trabajador si existe
     */
    Optional<Worker> findByUserUuidAndCompanyUuid(
            UUID userUuid,
            UUID companyUuid);

    /**
     * Busca todos los trabajadores asociados a una empresa.
     *
     * @param companyUuid UUID de la empresa
     * @return Lista de trabajadores asociados a la empresa
     */
    List<Worker> findAllByCompanyUuid(UUID companyUuid);

    /**
     * Busca todos los trabajadores asociados a un usuario.
     *
     * @param userUuid UUID del usuario
     * @return Lista de trabajadores asociados al usuario
     */
    List<Worker> findAllByUserUuid(UUID userUuid);

    /**
     * Busca todos los trabajadores asociados a un usuario con un rol
     * específico.
     *
     * @param userUuid UUID del usuario
     * @param role Rol del trabajador
     * @return Lista de trabajadores asociados al usuario con el rol específico
     */
    List<Worker> findAllByUserUuidAndRolesContains(UUID userUuid, RoleCompany role);
}
