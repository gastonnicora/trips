package com.gastonnicora.trips.services;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gastonnicora.trips.entitys.RefreshToken;
import com.gastonnicora.trips.exeptions.ErrorException;
import com.gastonnicora.trips.repositories.RefreshTokenRepository;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository repo;

    public RefreshToken createToken(String token, UUID userUuid, String userAgent, String ip, String device,
            int version) {
        RefreshToken newToken = new RefreshToken(token, userUuid, ip, userAgent, device, version);
        return repo.save(newToken);
    }

    public boolean existsByRefreshToken(String refreshToken) {
        return repo.existsByRefreshToken(refreshToken);
    }

    public RefreshToken verifyToken(String refreshToken, String currentIp, String currentUA) {

        if (refreshToken == null) {
            throw new ErrorException("Token inválido o expirado", 401);
        }

        RefreshToken rt = repo.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ErrorException("Token inválido o expirado", 401));

        // ver si el refreshToken esta activo
        if (!rt.isActive()) {

            repo.deleteByRefreshToken(rt.getToken());
            throw new ErrorException("Token inválido o expirado", 401);

        }

        // ver si el refreshToken esta expirado
        if (rt.getExpiryDate().isBefore(Instant.now())) {
            throw new ErrorException("Token inválido o expirado", 401);
        }

        // verificar que el refreshToken provenga del mismo dispositivo y ip
        if (!rt.getIp().equals(currentIp) ||
                !rt.getUserAgent().equals(currentUA)) {

            repo.deleteByRefreshToken(rt.getToken());
            throw new ErrorException("Token inválido o expirado", 401);
        }

        return rt;
    }

    public void revokeToken(String refreshToken) {

        repo.findByRefreshToken(refreshToken).ifPresent(rt -> {
            System.err.println("revokeToken");
            rt.setActive(false);
            rt.addVersion();
            repo.save(rt);
        });
    }

    public void deactivateAllByUserUuid(UUID uuid) {
        repo.findAllByUserUuidAndActiveTrue(uuid).forEach(rt -> {
            System.err.println("deactivateAllByUserUuid");
            rt.setActive(false);
            rt.addVersion();
            repo.save(rt);
        });
    }
}