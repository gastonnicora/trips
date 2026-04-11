package com.gastonnicora.trips.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.gastonnicora.trips.entitys.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByEmailAndActiveTrue(String email);

    @Modifying
    @Transactional
    void deleteByEmail(String email);

    @Modifying
    @Transactional
    void deleteAllByExpiryDateBefore(Instant now);

    Optional<RefreshToken> findByUuid(UUID uUId);

}
