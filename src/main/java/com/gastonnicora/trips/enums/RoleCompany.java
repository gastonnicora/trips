package com.gastonnicora.trips.enums;

/**
 * Enumeración que define los roles disponibles dentro de una empresa.
 *
 * <p>
 * Cada valor representa un rol que puede asignarse a un trabajador para
 * determinar su función dentro de la empresa.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-06-03
 */
public enum RoleCompany {

    /**
     * Rol correspondiente al propietario de la empresa.
     */
    OWNER,

    /**
     * Rol correspondiente a un vendedor dentro de la empresa.
     */
    SELLER,

    /**
     * Rol correspondiente a un conductor dentro de la empresa.
     */
    DRIVER,

    /**
     * Rol correspondiente a un administrador de la empresa.
     */
    ADMIN,

    /**
     * Rol correspondiente al responsable de recursos humanos dentro de la
     * empresa.
     */
    HR_MANAGER
}