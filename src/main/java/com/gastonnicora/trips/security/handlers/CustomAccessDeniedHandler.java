package com.gastonnicora.trips.security.handlers;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.gastonnicora.trips.exceptions.dtos.ForbiddenApiError;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

/**
 * Manejador personalizado para accesos denegados en Spring Security.
 * <p>
 * Cuando un usuario autenticado intenta acceder a un recurso para el que no
 * tiene permisos,
 * este handler devuelve un JSON con un mensaje de error y código HTTP 403
 * (Forbidden).
 * </p>
 * <p>
 * Utiliza {@link ForbiddenApiError} para formatear la respuesta JSON.
 * </p>
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /** Mapper de Jackson para convertir objetos a JSON */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Maneja la excepción de acceso denegado.
     * 
     * @param request               Solicitud HTTP
     * @param response              Respuesta HTTP
     * @param accessDeniedException Excepción lanzada por Spring Security
     * @throws IOException si ocurre un error al escribir la respuesta JSON
     */
    @Override
    public void handle(HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {

        // Configura el estado HTTP y el tipo de contenido
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        // Crea un objeto de error personalizado
        ForbiddenApiError error = new ForbiddenApiError(
                "No tenés permisos para acceder a este recurso");

        // Escribe la respuesta JSON
        mapper.writeValue(response.getOutputStream(), error);
    }
}