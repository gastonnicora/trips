package com.gastonnicora.trips.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gastonnicora.trips.entities.Worker;
import com.gastonnicora.trips.enums.RoleCompany;

/**
 * Repositorio encargado de gestionar la persistencia de entidades
 * {@link Worker}.
 *
 * <p>
 * Proporciona operaciones para consultar trabajadores por usuario, empresa,
 * rol y estado de actividad.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-06-03
 */
public interface WorkerRepository extends JpaRepository<Worker, UUID> {

        /**
         * Busca un trabajador por el identificador del usuario y de la empresa
         * asociada.
         *
         * @param userUuid    Identificador único del usuario.
         * @param companyUuid Identificador único de la empresa.
         * @return {@link Optional} que contiene el trabajador encontrado, o vacío si no
         *         existe.
         */
        Optional<Worker> findByUserUuidAndCompanyUuid(
                        UUID userUuid,
                        UUID companyUuid);

        /**
         * Busca un trabajador por el identificador del usuario y de la empresa
         * asociada únicamente si se encuentra activo.
         *
         * @param userUuid    Identificador único del usuario.
         * @param companyUuid Identificador único de la empresa.
         * @return {@link Optional} que contiene el trabajador activo encontrado, o
         *         vacío si no existe.
         */
        Optional<Worker> findByUserUuidAndCompanyUuidAndActiveTrue(
                        UUID userUuid,
                        UUID companyUuid);

        /**
         * Busca todos los trabajadores activos asociados a una empresa.
         *
         * @param companyUuid Identificador único de la empresa.
         * @return Lista de trabajadores activos asociados a la empresa.
         */
        List<Worker> findAllByCompanyUuidAndActiveTrue(UUID companyUuid);

        /**
         * Busca todos los trabajadores asociados a un usuario.
         *
         * @param userUuid Identificador único del usuario.
         * @return Lista de trabajadores asociados al usuario.
         */
        List<Worker> findAllByUserUuid(UUID userUuid);

        /**
         * Busca todos los trabajadores activos asociados a un usuario.
         *
         * @param userUuid Identificador único del usuario.
         * @return Lista de trabajadores activos asociados al usuario.
         */
        List<Worker> findAllByUserUuidAndActiveTrue(UUID userUuid);

        /**
         * Busca todos los trabajadores asociados a un usuario que contengan un rol
         * específico.
         *
         * @param userUuid Identificador único del usuario.
         * @param role     Rol del trabajador que se desea buscar.
         * @return Lista de trabajadores asociados al usuario que contienen el rol
         *         indicado.
         */
        List<Worker> findAllByUserUuidAndRolesContains(UUID userUuid, RoleCompany role);

        /**
         * Busca todos los trabajadores activos asociados a un usuario que contengan
         * un rol específico.
         *
         * @param userUuid Identificador único del usuario.
         * @param role     Rol del trabajador que se desea buscar.
         * @return Lista de trabajadores activos asociados al usuario que contienen el
         *         rol indicado.
         */
        List<Worker> findAllByUserUuidAndRolesContainsAndActiveTrue(UUID userUuid, RoleCompany role);
}