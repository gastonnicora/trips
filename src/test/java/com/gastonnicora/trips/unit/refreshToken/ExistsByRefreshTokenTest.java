package com.gastonnicora.trips.unit.refreshToken;

import com.gastonnicora.trips.repositories.RefreshTokenRepository;
import com.gastonnicora.trips.services.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExistsByRefreshTokenTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void shouldReturnFalseWhenRefreshTokenIsNull() {
        UUID uuid = UUID.randomUUID();

        when(refreshTokenRepository.existsByRefreshToken(uuid.toString())).thenReturn(false);

        boolean exists = refreshTokenService.existsByRefreshToken(uuid.toString());

        verify(refreshTokenRepository).existsByRefreshToken(uuid.toString());

        assertFalse(exists);
    }

    @Test
    void shouldReturnTrueWhenRefreshTokenExists() {
        UUID uuid = UUID.randomUUID();

        when(refreshTokenRepository.existsByRefreshToken(uuid.toString())).thenReturn(true);

        boolean exists = refreshTokenService.existsByRefreshToken(uuid.toString());

        verify(refreshTokenRepository).existsByRefreshToken(uuid.toString());

        assertTrue(exists);
    }
}
