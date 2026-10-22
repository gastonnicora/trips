package com.gastonnicora.trips.security;

import com.gastonnicora.trips.entities.Worker;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.repositories.WorkerRepository;
import com.gastonnicora.trips.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

/**
 * Componente de seguridad encargado de verificar los permisos del usuario
 * actual dentro de una empresa.
 *
 * <p>
 * Permite comprobar si el usuario actual posee un rol específico o alguno de
 * los roles indicados en una empresa determinada.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-06-03
 */
@Component("companySecurity")
@RequiredArgsConstructor
public class CompanySecurity {

    private final WorkerRepository workerRepository;

    /**
     * Verifica si el usuario actual posee al menos uno de los roles indicados
     * en una empresa.
     *
     * @param companyUuid Identificador único de la empresa.
     * @param roles       Roles que se desean verificar.
     *
     * @return {@code true} si el usuario posee al menos uno de los roles
     * indicados en la empresa; {@code false} en caso contrario.
     */
    public boolean hasAnyRole(
            UUID companyUuid,
            RoleCompany... roles) {

        UUID userUuid = SecurityUtils.getCurrentUserUuid();

        Worker worker = workerRepository
                .findByUserUuidAndCompanyUuidAndUserEnabledTrueAndCompanyActiveTrue(
                        userUuid,
                        companyUuid)
                .orElse(null);

        if (worker == null) {
            return false;
        }

        return Arrays.stream(roles)
                .anyMatch(worker.getRoles()::contains);
    }

    /**
     * Verifica si el usuario actual posee un rol específico en una empresa.
     *
     * @param companyUuid Identificador único de la empresa.
     * @param role        Rol que se desea verificar.
     *
     * @return {@code true} si el usuario posee el rol indicado en la empresa;
     * {@code false} en caso contrario.
     */
    public boolean hasRole(
            UUID companyUuid,
            RoleCompany role) {

        UUID userUuid = SecurityUtils.getCurrentUserUuid();

        Worker worker = workerRepository
                .findByUserUuidAndCompanyUuidAndUserEnabledTrueAndCompanyActiveTrue(
                        userUuid,
                        companyUuid)
                .orElse(null);

        return worker != null
                && worker.getRoles().contains(role);
    }

    /**
     * Verifica si el usuario actual posee el rol de administrador de empresa.
     *
     * @param companyUuid Identificador único de la empresa.
     *
     * @return {@code true} si el usuario posee el rol de administrador en la
     * empresa; {@code false} en caso contrario.
     */
    public boolean isAdmin(UUID companyUuid) {
        return hasRole(companyUuid, RoleCompany.ADMIN);
    }

    /**
     * Verifica si el usuario actual posee el rol de conductor en una empresa.
     *
     * @param companyUuid Identificador único de la empresa.
     *
     * @return {@code true} si el usuario posee el rol de conductor en la empresa;
     * {@code false} en caso contrario.
     */
    public boolean isDriver(UUID companyUuid) {
        return hasRole(companyUuid, RoleCompany.DRIVER);
    }

    /**
     * Verifica si el usuario actual posee el rol de responsable de recursos
     * humanos en una empresa.
     *
     * @param companyUuid Identificador único de la empresa.
     *
     * @return {@code true} si el usuario posee el rol de responsable de recursos
     * humanos en la empresa; {@code false} en caso contrario.
     */
    public boolean isHrManager(UUID companyUuid) {
        return hasRole(companyUuid, RoleCompany.HR_MANAGER);
    }

    /**
     * Verifica si el usuario actual posee el rol de vendedor en una empresa.
     *
     * @param companyUuid Identificador único de la empresa.
     *
     * @return {@code true} si el usuario posee el rol de vendedor en la empresa;
     * {@code false} en caso contrario.
     */
    public boolean isSeller(UUID companyUuid) {
        return hasRole(companyUuid, RoleCompany.SELLER);

    }
}