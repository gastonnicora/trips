package com.gastonnicora.trips.utils;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.gastonnicora.trips.security.UserDetailsImpl;

/**
 * Utilidades de seguridad para acceder a la información del usuario autenticado.
 * <p>
 * Proporciona métodos estáticos para obtener datos del usuario actualmente
 * autenticado en el contexto de Spring Security.
 * </p>
 * 
 * Autor: Gastón
 * Versión: 1.0
 * Desde: 2026-05-04
 */
public class SecurityUtils {

    /**
     * Obtiene el UUID del usuario actualmente autenticado.
     * 
     * @return UUID del usuario si está autenticado, {@code null} si no hay usuario
     *         en el contexto de seguridad.
     */
    public static UUID getCurrentUserUuid() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl user) {
            return user.getUuid();
        }
        return null;
    }

    /**
     * Obtiene el email del usuario actualmente autenticado.
     * 
     * @return Email del usuario si está autenticado, o el nombre del principal
     *         según Spring Security.
     */
    public static String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : null;
    }

}