package com.gastonnicora.trips.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gastonnicora.trips.entities.RefreshToken;
import com.gastonnicora.trips.repositories.RefreshTokenRepository;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro de autenticación JWT para Spring Security.
 * <p>
 * Este filtro intercepta todas las solicitudes HTTP y realiza los siguientes
 * pasos:
 * </p>
 * <ul>
 * <li>Verifica que la cabecera "Authorization" contenga un token Bearer
 * válido.</li>
 * <li>Valida el token JWT usando {@link JwtService}.</li>
 * <li>Extrae el username y la versión del token del JWT.</li>
 * <li>Verifica que el RefreshToken asociado esté activo y que la versión
 * coincida.</li>
 * <li>Si todo es válido, establece la autenticación en el contexto de Spring
 * Security.</li>
 * <li>Si el token es inválido o no cumple las condiciones, la solicitud
 * continúa sin autenticación.</li>
 * </ul>
 * <p>
 * Utiliza {@link UserDetailsServiceImpl} para cargar los detalles del usuario.
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
