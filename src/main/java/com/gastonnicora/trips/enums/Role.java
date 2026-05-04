package com.gastonnicora.trips.enums;

/**
 * Enumeración que define los roles posibles dentro de la plataforma.
 * <p>
 * Cada rol representa un conjunto de permisos y responsabilidades dentro del
 * sistema. Los roles pueden asignarse a los usuarios para controlar el acceso
 * a diferentes funcionalidades.
 * </p>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2023-05-04
 */
public enum Role {

    /**
     * Usuario estándar de la plataforma.
     */
    USER,

    /**
     * Administrador general con permisos para gestionar recursos y usuarios básicos.
     */
    ADMIN,

    /**
     * Super administrador con acceso completo a todas las funciones del sistema.
     */
    SUPER_ADMIN,

    /**
     * Pasajero que puede buscar viajes, reservarlos, cancelarlos y consultar su historial.
     */
    PASSENGER,

    /**
     * Vendedor que gestiona viajes (crear, modificar, eliminar), reservas (ver, cancelar)
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
     * Administrador de plataforma con permisos para gestionar empresas, usuarios,
     * viajes y generar reportes generales.
     */
    PLATFORM_ADMIN,

    /**
     * Agente de soporte que gestiona tickets de soporte y genera reportes relacionados.
     */
    SUPPORT_AGENT,

    /**
     * Analista que genera reportes de ventas, usuarios y viajes, y analiza datos
     * para mejorar la plataforma.
     */
    ANALYST,


    /**
     * Responsable financiero que gestiona facturación y genera reportes
     * financieros.
     */
    FINANCE_MANAGER,

    /**
     * Responsable de recursos humanos que gestiona empleados y genera reportes
     * de personal.
     */
    HR_MANAGER,

    
}