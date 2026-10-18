package com.gastonnicora.trips.utils;

import com.gastonnicora.trips.security.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

/**
 * Utilidades de seguridad para acceder a la información del usuario
 * autenticado.
 *
 * <p>
 * Proporciona métodos estáticos para obtener información del usuario
 * actualmente autenticado a partir del contexto de Spring Security.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-03
 */
public class SecurityUtils {

    /**
     * Obtiene el email del usuario actualmente autenticado.
     *
     * <p>
     * Recupera el nombre del principal mediante la autenticación almacenada en
     * el contexto de Spring Security.
     * </p>
     *
     * @return Email asociado al principal autenticado, o {@code null} si no
     * existe una autenticación en el contexto de seguridad.
     */
    public static String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : null;
    }

    /**
     * Obtiene el UUID del usuario actualmente autenticado.
     *
     * <p>
     * Recupera el principal del contexto de seguridad y obtiene su UUID cuando
     * corresponde a una instancia de {@link UserDetailsImpl}.
     * </p>
     *
     * @return UUID del usuario autenticado, o {@code null} si no existe una
     * autenticación válida en el contexto de seguridad.
     */
    public static UUID getCurrentUserUuid() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl user) {
            return user.getUuid();
        }
        return null;
    }

}