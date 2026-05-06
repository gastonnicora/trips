package com.gastonnicora.trips.services;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gastonnicora.trips.entities.RefreshToken;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.exceptions.UnauthorizedException;
import com.gastonnicora.trips.repositories.RefreshTokenRepository;

/**
 * Servicio para la gestión de Refresh Tokens.
 * <p>
 * Permite crear, verificar, revocar y desactivar tokens de refresco.
 * Se asegura de que cada token sea válido, activo y asociado al dispositivo
 * e IP correcta.
 * </p>
 * 
 * Flujo principal de verificación:
 * <ol>
 *   <li>Se valida que el token no sea nulo.</li>
 *   <li>Se verifica que exista en la base de datos.</li>
 *   <li>Se comprueba que el token esté activo.</li>
 *   <li>Se verifica que no haya expirado.</li>
 *   <li>Se asegura que provenga del mismo dispositivo y IP que al momento de crearlo.</li>
 * </ol>
 * 
 * Además permite revocar un token específico o desactivar todos los tokens activos
 * de un usuario, incrementando su versión para invalidar tokens previos.
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository repo;

    /**
     * Crea un nuevo refresh token y lo persiste.
     * 
     * @param token     Token generado
     * @param userUuid  UUID del usuario
     * @param userAgent Información del navegador/dispositivo
     * @param ip        Dirección IP
     * @param device    Nombre del dispositivo
     * @param version   Versión del token
     * @return {@link RefreshToken} persistido
     */
    public RefreshToken createToken(String token, UUID userUuid, String userAgent, String ip, String device,
            int version) {
        RefreshToken newToken = new RefreshToken(token, userUuid, ip, userAgent, device, version);
        return repo.save(newToken);
    }

    /**
     * Verifica si un refresh token existe en la base de datos.
     * 
     * @param refreshToken Token a verificar
     * @return {@code true} si existe, {@code false} si no
     */
    public boolean existsByRefreshToken(String refreshToken) {
        return repo.existsByRefreshToken(refreshToken);
    }

    /**
     * Verifica que el refresh token sea válido, activo, no expirado y que provenga
     * del mismo dispositivo y IP.
     * <p>
     * Si falla alguna verificación, lanza {@link UnauthorizedException} con código 401.
     * </p>
     * 
     * @param refreshToken Token a verificar
     * @param currentIp    IP del dispositivo actual
     * @param currentUA    User agent del dispositivo actual
     * @return {@link RefreshToken} válido
     * @throws UnauthorizedException Si el token es inválido, expirado o deshabilitado
     */
    public RefreshToken verifyToken(String refreshToken, String currentIp, String currentUA) {

        if (refreshToken == null) {
            throw new UnauthorizedException("Token inválido o expirado");
        }

        RefreshToken rt = repo.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new UnauthorizedException("Token inválido o expirado"));

        if (!rt.isActive()) {
            throw new UnauthorizedException("Token inválido o expirado");
        }

        if (rt.getExpiryDate().isBefore(Instant.now())) {
            throw new UnauthorizedException("Token inválido o expirado");
        }

        if (!rt.getIp().equals(currentIp) || !rt.getUserAgent().equals(currentUA)) {
            this.revokeToken(refreshToken);
            throw new UnauthorizedException("Token inválido o expirado");
        }

        return rt;
    }

    /**
     * Revoca un refresh token específico, desactivándolo y aumentando su versión.
     * 
     * @param refreshToken Token a revocar
     */
    public void revokeToken(String refreshToken) {
        repo.findByRefreshToken(refreshToken).ifPresent(rt -> {
            rt.setActive(false);
            rt.addVersion();
            repo.save(rt);
        });
    }

    /**
     * Desactiva todos los refresh tokens activos de un usuario, incrementando la
     * versión de cada uno.
     * 
     * @param uuid UUID del usuario
     */
    public void deactivateAllByUserUuid(UUID uuid) {
        repo.findAllByUserUuidAndActiveTrue(uuid).forEach(rt -> {
            rt.setActive(false);
            rt.addVersion();
            repo.save(rt);
        });
    }

    /**
     * Busca un refresh token en la base de datos.
     * 
     * @param refreshToken Token a buscar
     * @return {@link Optional} con el token si existe
     */
    public Optional<RefreshToken> findByRefreshToken(String refreshToken) {
        return repo.findByRefreshToken(refreshToken);
    }
}