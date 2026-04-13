package com.gastonnicora.trips.services;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gastonnicora.trips.entitys.RefreshToken;
import com.gastonnicora.trips.exeptions.ErrorException;
import com.gastonnicora.trips.repositories.RefreshTokenRepository;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository repo;

    public RefreshToken createToken(String email, String userAgent, String ip, String device) {
        RefreshToken newToken = new RefreshToken( email, ip, userAgent, device);
        return repo.save(newToken);
    }

    public RefreshToken verifyToken(String token, String currentIp, String currentUA) {

        if (token == null) {
            throw new ErrorException("Token inexistente", 401);
        }

        RefreshToken rt = repo.findByToken(token)
                .orElseThrow(() -> new ErrorException("Token no existe", 401));

        // ver si el token esta activo
        if (!rt.isActive()) {

            repo.deleteByToken(rt.getToken());
            throw new ErrorException("Token revocado", 401);

        }

        // ver si el token esta expirado
        if (rt.getExpiryDate().isBefore(Instant.now())) {
            throw new ErrorException("Token expirado", 401);
        }

        // verificar que el token provenga del mismo dispositivo y ip
        if (!rt.getIp().equals(currentIp) ||
                !rt.getUserAgent().equals(currentUA)) {

            repo.deleteByToken(rt.getToken());
            throw new ErrorException("Token comprometido por otro dispositivo", 401);
        }

        return rt;
    }

    public void revokeToken(String token) {

        repo.findByToken(token).ifPresent(rt -> {
            rt.setActive(false);
            repo.save(rt);
        });
    }
}