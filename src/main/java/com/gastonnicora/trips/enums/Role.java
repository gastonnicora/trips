package com.gastonnicora.trips.enums;

public enum Role {
    USER,
    ADMIN,
    SUPER_ADMIN,
    PASSENGER, // Funciones: buscar viajes, reservar viajes, cancelar reservas, ver historial
               // de viajes
    SELLER, // Funciones: gestionar viajes (crear, modificar, eliminar), gestionar reservas
            // (ver, cancelar), generar reportes de ventas
    DRIVER, // Funciones: ver viajes asignados, actualizar estado de viaje (en curso,
            // completado), gestionar perfil
    COMPANY_ADMIN, // Funciones: gestionar usuarios (crear, modificar, eliminar), gestionar viajes
                   // (crear, modificar, eliminar), generar reportes de ventas y usuarios
    PLATFORM_ADMIN, // Funciones: gestionar empresas (crear, modificar, eliminar), gestionar
                    // usuarios (crear, modificar, eliminar), gestionar viajes (crear, modificar,
                    // eliminar), generar reportes de ventas y usuarios
    SUPPORT_AGENT, // Funciones: gestionar trips de soporte (ver, responder, cerrar), generar
                   // reportes de trips
    ANALYST, // Funciones: generar reportes de ventas, usuarios y viajes, analizar datos para
             // mejorar la plataforma
    MARKETING_MANAGER, // Funciones: gestionar campañas de marketing (crear, modificar, eliminar),
                       // generar reportes de campañas
    FINANCE_MANAGER, // Funciones: gestionar facturación (ver, generar, modificar), generar reportes
                     // financieros
    HR_MANAGER, // Funciones: gestionar empleados (crear, modificar, eliminar), generar reportes
                // de empleados
    CONTENT_MANAGER, // Funciones: gestionar contenido de la plataforma (crear, modificar, eliminar),
                     // generar reportes de contenido
    AUDITOR;// Funciones: generar reportes de auditoría, revisar actividades sospechosas

}
