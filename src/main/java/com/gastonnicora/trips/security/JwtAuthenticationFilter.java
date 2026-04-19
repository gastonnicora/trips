package com.gastonnicora.trips.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gastonnicora.trips.entitys.RefreshToken;
import com.gastonnicora.trips.entitys.User;
import com.gastonnicora.trips.repositories.RefreshTokenRepository;
import com.gastonnicora.trips.repositories.UserRepository;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        if (!jwtService.isValid(token)) {
            writeError(response);
            return;
        }
        String username;
        try {

            username = jwtService.extractUsername(token);
        } catch (JwtException e) {
            writeError(response);
            return;
        }
        int version;
        try {
            version = jwtService.extractVersion(token);
        } catch (JwtException e) {
            writeError(response);
            return;
        }
        Optional<RefreshToken> refreshToken=refreshTokenRepository.findByToken(token);
        if (refreshToken.isEmpty() ||!refreshToken.get().isActive()|| refreshToken.get().getVersion() != version) {
            writeError(response);
            return;
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }

    private void writeError(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        String body = """
                {
                    "status": 401,
                    "message": "Token invalido",
                    "timestamp": "%s",
                    "errors": null
                }
                """.formatted(LocalDateTime.now());

        response.getWriter().write(body);
    }
}