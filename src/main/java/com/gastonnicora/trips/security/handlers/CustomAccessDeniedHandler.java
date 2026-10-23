package com.gastonnicora.trips.security.handlers;

import com.gastonnicora.trips.exceptions.dtos.ForbiddenApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Manejador personalizado para accesos denegados en Spring Security.
 *
 * <p>
 * Gestiona los casos en los que un usuario autenticado intenta acceder a un
 * recurso para el que no dispone de los permisos necesarios.
 * </p>
 *
 * <p>
 * Genera una respuesta HTTP con estado {@code 403 Forbidden} y un cuerpo en
 * formato JSON utilizando {@link ForbiddenApiError}.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-04-21
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * Mapper utilizado para convertir el objeto de error en formato JSON.
     */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Maneja una excepción de acceso denegado generada por Spring Security.
     *
     * <p>
     * Configura la respuesta HTTP con estado {@code 403 Forbidden} y contenido
     * JSON, y escribe en la respuesta el error representado mediante
     * {@link ForbiddenApiError}.
     * </p>
     *
     * @param request               Solicitud HTTP asociada al acceso denegado.
     * @param response              Respuesta HTTP en la que se informa el error.
     * @param accessDeniedException Excepción de acceso denegado generada por Spring Security.
     *
     * @throws IOException Si ocurre un error al escribir la respuesta JSON.
     */
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
                      ) throws IOException {

        // Configura el estado HTTP y el tipo de contenido
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        // Crea un objeto de error personalizado
        ForbiddenApiError error = new ForbiddenApiError(
                "No tenes permisos para acceder a este recurso");

        // Escribe la respuesta JSON
        mapper.writeValue(response.getOutputStream(), error);
    }
}