package com.gastonnicora.trips.services;

import com.gastonnicora.trips.entities.RefreshToken;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.exceptions.UnauthorizedException;
import com.gastonnicora.trips.repositories.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio encargado de gestionar los refresh tokens de los usuarios.
 *
 * <p>
 * Permite crear, verificar, revocar y desactivar tokens de refresco, validando
 * su existencia, estado de actividad, fecha de expiración y correspondencia con
 * la dirección IP y el user agent registrados al momento de su creación.
 * </p>
 *
 * <p>
 * El proceso de verificación comprueba que el token no sea nulo, exista en la
 * base de datos, se encuentre activo, no haya expirado y coincida con la
 * dirección IP y el user agent actuales. Si alguna de estas validaciones falla,
 * se lanza una {@link UnauthorizedException}.
 * </p>
 *
 * <p>
 * También permite revocar un token específico o desactivar todos los tokens
 * activos asociados a un usuario, incrementando la versión de cada token
 * afectado.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repo;

    /**
     * Crea una instancia del servicio de tokens de refresco e inicializa las
     * dependencias
     * necesarias para la gestión de tokens de refresco y
     * conversión de entidades a DTOs.
     *
     * @param repo Repositorio de tokens de refresco ({@link RefreshTokenRepository}) utilizado para
     *             acceder a
     *             la
     *             base de datos.
     */
    public RefreshTokenService(RefreshTokenRepository repo) {
        this.repo = repo;
    }

    /**
     * Crea un nuevo refresh token y lo persiste en la base de datos.
     *
     * @param token     Token de refresco que se desea registrar.
     * @param user      Usuario asociado al refresh token.
     * @param userAgent Información del navegador o dispositivo desde el que se
     *                  genera el token.
     * @param ip        Dirección IP asociada al token.
     * @param device    Nombre del dispositivo asociado al token.
     * @param version   Versión del token.
     *
     * @return {@link RefreshToken} persistido.
     */
    public RefreshToken createToken(String token, User user, String userAgent, String ip, String device,
                                    int version) {
        RefreshToken newToken = new RefreshToken(token, user, ip, userAgent, device, version);
        return repo.save(newToken);
    }

    /**
     * Desactiva todos los refresh tokens activos asociados a un usuario.
     *
     * <p>
     * Cada token encontrado es desactivado, se incrementa su versión y se
     * persiste el cambio.
     * </p>
     *
     * @param uuid UUID del usuario cuyos tokens se desean desactivar.
     *
     * @see RefreshTokenRepository#findAllByUser_UuidAndActiveTrue(UUID)
     */
    public void deactivateAllByUserUuid(UUID uuid) {
        repo.findAllByUser_UuidAndActiveTrue(uuid).forEach(rt -> {
            rt.setActive(false);
            rt.addVersion();
            repo.save(rt);
        });
    }

    /**
     * Verifica si existe un refresh token en la base de datos.
     *
     * @param refreshToken Token de refresco que se desea verificar.
     *
     * @return {@code true} si el token existe, {@code false} en caso contrario.
     *
     * @see RefreshTokenRepository#existsByRefreshToken(String)
     */
    public boolean existsByRefreshToken(String refreshToken) {
        return repo.existsByRefreshToken(refreshToken);
    }

    /**
     * Busca un refresh token por su valor.
     *
     * @param refreshToken Token de refresco que se desea buscar.
     *
     * @return {@link Optional} que contiene el refresh token encontrado, o vacío
     * si no existe.
     *
     * @see RefreshTokenRepository#findByRefreshToken(String)
     */
    public Optional<RefreshToken> findByRefreshToken(String refreshToken) {
        return repo.findByRefreshToken(refreshToken);
    }

    /**
     * Revoca un refresh token específico, desactivándolo e incrementando su
     * versión.
     *
     * <p>
     * Si el token existe, se actualiza su estado de actividad y se persiste el
     * cambio.
     * </p>
     *
     * @param refreshToken Token de refresco que se desea revocar.
     *
     * @see RefreshTokenRepository#findByRefreshToken(String)
     */
    public void revokeToken(String refreshToken) {
        repo.findByRefreshToken(refreshToken).ifPresent(rt -> {
            rt.setActive(false);
            rt.addVersion();
            repo.save(rt);
        });
    }

    /**
     * Verifica que un refresh token sea válido para su utilización.
     *
     * <p>
     * Se comprueba que el token exista, se encuentre activo, no haya expirado y
     * coincida con la dirección IP y el user agent actuales.
     * </p>
     *
     * <p>
     * Si la dirección IP o el user agent no coinciden con los datos registrados,
     * el token es revocado antes de lanzar la excepción correspondiente.
     * </p>
     *
     * @param refreshToken Token de refresco que se desea verificar.
     * @param currentIp    Dirección IP del dispositivo actual.
     * @param currentUA    User agent del dispositivo actual.
     *
     * @return {@link RefreshToken} válido.
     *
     * @throws UnauthorizedException Si el token es nulo, no existe, está
     *                               inactivo, ha expirado o no coincide con la
     *                               dirección IP o el user
     *                               agent registrados.
     * @see RefreshTokenRepository#findByRefreshToken(String)
     * @see RefreshTokenService#revokeToken(String)
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
}