package com.gastonnicora.trips.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.gastonnicora.trips.entities.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    List<RefreshToken> findAllByUserUuidAndActiveTrue(UUID userUuid);

    @Modifying
    @Transactional
    void deleteByUserUuid(UUID userUuid);

    @Modifying
    @Transactional
    void deleteAllByExpiryDateBefore(Instant now);

    @Modifying
    @Transactional
    void deleteAllByActiveFalse();

    @Modifying
    @Transactional
    void deleteByRefreshToken(String token);

    Optional<RefreshToken> findByUuid(UUID uuid);

    Optional<RefreshToken> findByToken(String token);

    boolean existsByRefreshToken(String refreshToken);

}
