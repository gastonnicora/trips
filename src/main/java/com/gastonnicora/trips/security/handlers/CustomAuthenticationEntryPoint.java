package com.gastonnicora.trips.security.handlers;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.gastonnicora.trips.exceptions.dtos.UnauthorizedApiError;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

/**
 * Entry point personalizado para manejar errores de autenticación en Spring
 * Security.
 * <p>
 * Cuando un usuario no autenticado intenta acceder a un recurso protegido, este
 * entry point devuelve un JSON con mensaje de error y código HTTP 401
 * (Unauthorized).
 * </p>
 * <p>
 * Utiliza {@link UnauthorizedApiError} para estructurar la respuesta JSON.
 * </p>
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Mapper de Jackson para convertir objetos a JSON
     */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Maneja la excepción de autenticación no válida o ausente.
     *
     * @param request Solicitud HTTP
     * @param response Respuesta HTTP
     * @param authException Excepción lanzada por Spring Security
     * @throws IOException si ocurre un error al escribir la respuesta JSON
     */
    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {

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
