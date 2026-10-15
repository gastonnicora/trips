package com.gastonnicora.trips.security;

import com.gastonnicora.trips.entities.RefreshToken;
import com.gastonnicora.trips.repositories.RefreshTokenRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Filtro de autenticación JWT para Spring Security.
 *
 * <p>
 * Intercepta las solicitudes HTTP que contienen una cabecera de autorización
 * con un token Bearer y valida las condiciones necesarias para establecer la
 * autenticación en el contexto de Spring Security.
 * </p>
 *
 * <p>
 * La validación incluye la comprobación del token JWT, su usuario asociado,
 * el estado activo del token de refresco y la coincidencia de su versión.
 * </p>
 *
 * <p>
 * Utiliza {@link JwtService} para validar y extraer información del token y
 * {@link UserDetailsServiceImpl} para cargar los detalles del usuario.
 * </p>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   UserDetailsServiceImpl userDetailsService,
                                   RefreshTokenRepository refreshTokenRepository) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Procesa una solicitud HTTP para validar el token JWT y establecer la
     * autenticación del usuario cuando las condiciones de seguridad son válidas.
     *
     * <p>
     * Si la solicitud no contiene una cabecera {@code Authorization} con el
     * esquema {@code Bearer}, si el token no es válido, si el token asociado no
     * está activo o si su versión no coincide, la solicitud continúa hacia el
     * siguiente filtro sin establecer autenticación.
     * </p>
     *
     * @param request     Solicitud HTTP que se está procesando.
     * @param response    Respuesta HTTP asociada a la solicitud.
     * @param filterChain Cadena de filtros que continúa el procesamiento de la solicitud.
     *
     * @throws ServletException Si ocurre un error durante el procesamiento del filtro.
     * @throws IOException      Si ocurre un error de entrada o salida durante el procesamiento
     *                          de la solicitud.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // Si no hay cabecera o no empieza con "Bearer ", pasa al siguiente filtro
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            // Valida el token
            if (!jwtService.isValid(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            String username = jwtService.extractUsername(token);
            int version = jwtService.extractVersion(token);

            // Verifica refresh token
            Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(token);

            if (refreshToken.isEmpty()
                    || !refreshToken.get().isActive()
                    || refreshToken.get().getVersion() != version) {

                filterChain.doFilter(request, response);
                return;
            }

            // Carga detalles del usuario y establece autenticación
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (JwtException | IllegalArgumentException e) {
            // El filtro ignora excepciones de JWT y permite continuar sin autenticación
            // Observación: se podría loguear aquí para auditoría
        }

        filterChain.doFilter(request, response);
    }
}