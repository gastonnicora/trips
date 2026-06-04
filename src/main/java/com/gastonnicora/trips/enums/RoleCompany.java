package com.gastonnicora.trips.enums;

/**
 * Enumeración que define los roles posibles dentro de una empresa.
 * <p>
 * Cada rol representa un conjunto de permisos y responsabilidades dentro del
 * sistema. Los roles pueden asignarse a los usuarios para controlar el acceso
 * a diferentes funcionalidades.
 * </p>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-06-03
 */
public enum RoleCompany {

    /**
     * Dueño de la empresa que puede tiene todos los permisos sobre la misma.
     */
    OWNER,

    /**
     * Vendedor que gestiona viajes (crear, modificar, eliminar), reservas (ver,
     * cancelar)
     * y genera reportes de ventas.
     */
    SELLER,

    /**
     * Conductor que ve sus viajes asignados, actualiza el estado de los viajes
     * (en curso, completado) y gestiona su perfil.
     */
    DRIVER,

    /**
     * Administrador de empresa que gestiona usuarios, viajes y genera reportes
     * de ventas y usuarios dentro de su empresa.
     */
    COMPANY_ADMIN,
    
    /**
     * Responsable de recursos humanos que gestiona empleados y genera reportes
     * de personal.
     */
    HR_MANAGER
}
