package com.gastonnicora.trips.security;

import java.util.Arrays;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.gastonnicora.trips.entities.Worker;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.repositories.WorkerRepository;
import com.gastonnicora.trips.utils.SecurityUtils;

import lombok.RequiredArgsConstructor;

/**
 * Clase de seguridad para empresas.
 * <p>
 * Se utiliza para verificar si el usuario actual tiene permisos para realizar
 * ciertas acciones en una empresa.
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
     * Verifica si el usuario actual tiene un rol específico en una empresa.
     * 
     * @param companyUuid UUID de la empresa
     * @param role        Rol a verificar
     * @return true si el usuario tiene el rol, false en caso contrario
     */
    public boolean hasRole(
            UUID companyUuid,
            RoleCompany role) {

        UUID userUuid = SecurityUtils.getCurrentUserUuid();

        Worker worker = workerRepository
                .findByUserUuidAndCompanyUuid(
                        userUuid,
                        companyUuid)
                .orElse(null);

        return worker != null &&
                worker.getRoles().contains(role);
    }

    /**
     * Verifica si el usuario actual tiene al menos un rol específico en una
     * empresa.
     * 
     * @param companyUuid UUID de la empresa
     * @param roles       Roles a verificar
     * @return true si el usuario tiene al menos un rol, false en caso contrario
     */
    public boolean hasAnyRole(
            UUID companyUuid,
            RoleCompany... roles) {

        UUID userUuid = SecurityUtils.getCurrentUserUuid();

        Worker worker = workerRepository
                .findByUserUuidAndCompanyUuid(
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
     * Verifica si el usuario actual es vendedor en una empresa.
     * 
     * @param companyUuid UUID de la empresa
     * @return true si el usuario es vendedor, false en caso contrario
     */
    public boolean isSeller(UUID companyUuid) {
        return hasRole(companyUuid, RoleCompany.SELLER);

    }

    /**
     * Verifica si el usuario actual es conductor en una empresa.
     * 
     * @param companyUuid UUID de la empresa
     * @return true si el usuario es conductor, false en caso contrario
     */
    public boolean isDriver(UUID companyUuid) {
        return hasRole(companyUuid, RoleCompany.DRIVER);
    }

    /**
     * Verifica si el usuario actual es administrador de empresa en una empresa.
     * 
     * @param companyUuid UUID de la empresa
     * @return true si el usuario es administrador de empresa, false en caso
     *         contrario
     */
    public boolean isAdmin(UUID companyUuid) {
        return hasRole(companyUuid, RoleCompany.ADMIN);
    }

    /**
     * Verifica si el usuario actual es responsable de recursos humanos en una
     * empresa.
     * 
     * @param companyUuid UUID de la empresa
     * @return true si el usuario es responsable de recursos humanos, false en caso
     *         contrario
     */
    public boolean isHrManager(UUID companyUuid) {
        return hasRole(companyUuid, RoleCompany.HR_MANAGER);
    }
}