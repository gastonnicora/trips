package com.gastonnicora.trips.unit.refreshToken;

import java.util.UUID;


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.repositories.RefreshTokenRepository;
import com.gastonnicora.trips.services.RefreshTokenService;

@ExtendWith(MockitoExtension.class)
class ExistsByRefreshTokenTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void shouldReturnTrueWhenRefreshTokenExists() {
        UUID uuid = UUID.randomUUID();

        when(refreshTokenRepository.existsByRefreshToken(uuid.toString())).thenReturn(true);

        boolean exists = refreshTokenService.existsByRefreshToken(uuid.toString());

        verify(refreshTokenRepository).existsByRefreshToken(uuid.toString());

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenRefreshTokenIsNull() {
        UUID uuid = UUID.randomUUID();

        when(refreshTokenRepository.existsByRefreshToken(uuid.toString())).thenReturn(false);

        boolean exists = refreshTokenService.existsByRefreshToken(uuid.toString());

        verify(refreshTokenRepository).existsByRefreshToken(uuid.toString());

        assertFalse(exists);
    }
}
