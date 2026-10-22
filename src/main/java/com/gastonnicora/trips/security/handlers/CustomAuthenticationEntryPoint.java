package com.gastonnicora.trips.security.handlers;

import com.gastonnicora.trips.exceptions.dtos.UnauthorizedApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Entry point personalizado para gestionar errores de autenticación en Spring
 * Security.
 *
 * <p>
 * Gestiona los casos en los que un usuario no autenticado intenta acceder a un
 * recurso protegido.
 * </p>
 *
 * <p>
 * Genera una respuesta HTTP con estado {@code 401 Unauthorized} y un cuerpo en
 * formato JSON utilizando {@link UnauthorizedApiError}.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-04-21
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Mapper utilizado para convertir el objeto de error en formato JSON.
     */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Maneja una excepción de autenticación no válida o ausente generada por
     * Spring Security.
     *
     * <p>
     * Configura la respuesta HTTP con estado {@code 401 Unauthorized} y
     * contenido JSON, y escribe en la respuesta el error representado mediante
     * {@link UnauthorizedApiError}.
     * </p>
     *
     * @param request       Solicitud HTTP asociada al intento de acceso.
     * @param response      Respuesta HTTP en la que se informa el error.
     * @param authException Excepción de autenticación generada por Spring Security.
     *
     * @throws IOException Si ocurre un error al escribir la respuesta JSON.
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
                        ) throws IOException {

        // Configura el estado HTTP y el tipo de contenido
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        // Crea un objeto de error personalizado
        UnauthorizedApiError error = new UnauthorizedApiError(
                "No autenticado o token inválido");

        // Escribe la respuesta JSON
        mapper.writeValue(response.getOutputStream(), error);
    }
}