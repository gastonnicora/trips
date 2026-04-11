package com.gastonnicora.trips.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gastonnicora.trips.entitys.RefreshToken;
import com.gastonnicora.trips.repositories.RefreshTokenRepository;
import com.gastonnicora.trips.security.JwtService;

//TODO crear exepcion para sustituir RuntimeException
@Service
public class RefreshTokenService {


    @Autowired
    private RefreshTokenRepository repo;

    private final JwtService jwtService;

    RefreshTokenService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public RefreshToken createToken(String token, String email, String userAgent, String ip, String device)
            throws Exception {

        RefreshToken newToken = new RefreshToken();
        newToken.setUuid(UUID.randomUUID());
        newToken.setEmail(email);
        newToken.setToken(token);
        newToken.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
        newToken.setUserAgent(userAgent);
        newToken.setIp(ip);
        newToken.setDevice(device);
        newToken.setActive(true);
        return repo.save(newToken);
    }

    public RefreshToken verifyToken(String token, String currentIp, String currentUA) throws Exception {
       
        
        if (token == null) {
            throw new RuntimeException("Token inexistente");
        }


         RefreshToken rt = repo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token no existe"));
        System.err.println(rt.getEmail());
        System.err.println(rt.getToken());
        // validar token
        if (!jwtService.isValid(token)) {
            // revocar TODOS los tokens del usuario
            repo.deleteByEmail(rt.getEmail());
            throw new RuntimeException("Token comprometido");
        }

        // ver si el token esta activo
        if (!rt.isActive()) {
            
            // revocar TODOS los tokens del usuario
            repo.deleteByEmail(rt.getEmail());
            throw new RuntimeException("Token revocado");
            
        }


        // ver si el token esta expirado
        if (rt.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Token expirado");
        }


        // verificar que el token provenga del mismo dispositivo y ip
        if (!rt.getIp().equals(currentIp) ||
                !rt.getUserAgent().equals(currentUA)) {

            // revocar TODOS los tokens del usuario
            repo.deleteByEmail(rt.getEmail());

            throw new RuntimeException("Token comprometido por otro dispositivo");
        }

        return rt;
    }

    //TODO refactorizar
    public void revokeToken(String token) {
        
        repo.findByToken(token).ifPresent(rt -> {
            rt.setActive(false);
            repo.save(rt);
        });
    }
}