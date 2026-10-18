package com.gastonnicora.trips.enums;

/**
 * Enumeración que define los roles disponibles para los usuarios de la
 * plataforma.
 *
 * <p>
 * Cada valor representa un rol que puede utilizarse para determinar los
 * permisos y el nivel de acceso de un usuario dentro de la aplicación.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-03-18
 */
public enum Role {

    /**
     * Rol correspondiente a un usuario estándar.
     */
    USER,

    /**
     * Rol correspondiente a un administrador.
     */
    ADMIN,

    /**
     * Rol correspondiente a un super administrador.
     */
    SUPER_ADMIN,

    /**
     * Rol correspondiente a un administrador de la plataforma.
     */
    PLATFORM_ADMIN,

    /**
     * Rol correspondiente a un agente de soporte.
     */
    SUPPORT_AGENT,

    /**
     * Rol correspondiente a un analista.
     */
    ANALYST,

    /**
     * Rol correspondiente a un responsable financiero.
     */
    FINANCE_MANAGER,

    /**
     * Rol correspondiente a un responsable de recursos humanos.
     */
    HR_MANAGER
}