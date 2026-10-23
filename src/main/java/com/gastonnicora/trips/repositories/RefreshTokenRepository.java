package com.gastonnicora.trips.repositories;

import com.gastonnicora.trips.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio encargado de gestionar la persistencia de entidades
 * {@link RefreshToken}.
 *
 * <p>
 * Proporciona operaciones para consultar y eliminar tokens de refresco,
 * incluyendo búsquedas por usuario, UUID, valor del token, fecha de expiración
 * y estado de activación.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-04-11
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    /**
     * Elimina todos los tokens que se encuentran desactivados.
     */
    @Modifying
    @Transactional
    void deleteAllByActiveFalse();

    /**
     * Elimina todos los tokens cuya fecha de expiración sea anterior a la fecha
     * indicada.
     *
     * @param now Fecha de referencia para determinar los tokens expirados.
     */
    @Modifying
    @Transactional
    void deleteAllByExpiryDateBefore(Instant now);

    /**
     * Elimina todos los tokens asociados a un usuario.
     *
     * @param userUuid Identificador único del usuario.
     */
    @Modifying
    @Transactional
    void deleteAllByUser_Uuid(UUID userUuid);

    /**
     * Elimina un token de refresco por su valor.
     *
     * @param refreshToken Valor del token de refresco que se desea eliminar.
     */
    @Modifying
    @Transactional
    void deleteByRefreshToken(String refreshToken);

    /**
     * Verifica si existe un token de refresco con el valor indicado.
     *
     * @param refreshToken Valor del token de refresco.
     *
     * @return {@code true} si existe un token con el valor indicado; {@code false}
     * en caso contrario.
     */
    boolean existsByRefreshToken(String refreshToken);

    /**
     * Obtiene todos los tokens activos asociados a un usuario.
     *
     * @param userUuid Identificador único del usuario.
     *
     * @return Lista de tokens de refresco activos asociados al usuario.
     */
    List<RefreshToken> findAllByUser_UuidAndActiveTrue(UUID userUuid);

    /**
     * Busca un token de refresco por su valor.
     *
     * @param refreshToken Valor del token de refresco.
     *
     * @return {@link Optional} que contiene el token encontrado, o vacío si no
     * existe.
     */
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    /**
     * Busca un token por su valor.
     *
     * @param token Valor del token.
     *
     * @return {@link Optional} que contiene el token encontrado, o vacío si no
     * existe.
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Busca un token por su identificador único.
     *
     * @param uuid Identificador único del token.
     *
     * @return {@link Optional} que contiene el token encontrado, o vacío si no
     * existe.
     */
    Optional<RefreshToken> findByUuid(UUID uuid);

}