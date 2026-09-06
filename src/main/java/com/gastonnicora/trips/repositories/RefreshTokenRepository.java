package com.gastonnicora.trips.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.gastonnicora.trips.entities.RefreshToken;

/**
 * Repositorio para gestionar {@link RefreshToken}.
 * <p>
 * Proporciona métodos para consultar, eliminar y verificar tokens de refresco.
 * Utiliza Spring Data JPA para el acceso a la base de datos.
 * </p>
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    /**
     * Busca un token de refresco por su valor.
     *
     * @param refreshToken Token de refresco
     * @return {@link Optional} con el token si existe
     */
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    /**
     * Obtiene todos los tokens activos de un usuario.
     *
     * @param userUuid UUID del usuario
     * @return Lista de tokens activos
     */
    List<RefreshToken> findAllByUser_UuidAndActiveTrue(UUID userUuid);

    /**
     * Elimina todos los tokens de un usuario.
     *
     * @param userUuid UUID del usuario
     */
    @Modifying
    @Transactional
    void deleteAllByUser_Uuid(UUID userUuid);

    /**
     * Elimina todos los tokens que hayan expirado antes de la fecha indicada.
     *
     * @param now Fecha de referencia
     */
    @Modifying
    @Transactional
    void deleteAllByExpiryDateBefore(Instant now);

    /**
     * Elimina todos los tokens que están desactivados.
     */
    @Modifying
    @Transactional
    void deleteAllByActiveFalse();

    /**
     * Elimina un token de refresco por su valor.
     *
     * @param refreshToken Token de refresco
     */
    @Modifying
    @Transactional
    void deleteByRefreshToken(String refreshToken);

    /**
     * Busca un token por su UUID.
     *
     * @param uuid UUID del token
     * @return {@link Optional} con el token si existe
     */
    Optional<RefreshToken> findByUuid(UUID uuid);

    /**
     * Busca un token de acceso por su valor.
     *
     * @param token Token JWT
     * @return {@link Optional} con el token si existe
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Verifica si existe un token de refresco con el valor indicado.
     *
     * @param refreshToken Token de refresco
     * @return true si existe, false en caso contrario
     */
    boolean existsByRefreshToken(String refreshToken);

}
