package com.gastonnicora.trips.unit.service.refreshToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.repositories.RefreshTokenRepository;
import com.gastonnicora.trips.services.RefreshTokenService;

@ExtendWith(MockitoExtension.class)
public class ExistsByRefreshTokenTeest {
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void shouldExistsByRefreshToken() {
        UUID uuid = UUID.randomUUID();

        when(refreshTokenRepository.existsByRefreshToken(uuid.toString())).thenReturn(true);

        boolean exists = refreshTokenService.existsByRefreshToken(uuid.toString());

        verify(refreshTokenRepository).existsByRefreshToken(uuid.toString());

        assertEquals(true, exists);
    }

    @Test
    void shouldExistsByRefreshToken_whenRefreshTokenIsNull() {
        UUID uuid = UUID.randomUUID();

        when(refreshTokenRepository.existsByRefreshToken(uuid.toString())).thenReturn(false);

        boolean exists = refreshTokenService.existsByRefreshToken(uuid.toString());

        verify(refreshTokenRepository).existsByRefreshToken(uuid.toString());

        assertEquals(false, exists);
    }
}
