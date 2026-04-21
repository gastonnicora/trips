package com.gastonnicora.trips.services;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.gastonnicora.trips.repositories.RefreshTokenRepository;

@Service
public class TokenCleanupService {

    @Autowired
    private RefreshTokenRepository repo;

    @Scheduled(cron = "0 0 * * * *")
    public void clean() {
        repo.deleteAllByExpiryDateBefore(Instant.now());
    }

    @Scheduled(cron = "0 0 * * * *")
    public void cleanInactive() {
        repo.deleteAllByActiveFalse();
    }
}