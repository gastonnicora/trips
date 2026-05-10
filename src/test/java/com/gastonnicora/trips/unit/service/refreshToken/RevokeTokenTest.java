package com.gastonnicora.trips.unit.service.refreshToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.entities.RefreshToken;
import com.gastonnicora.trips.repositories.RefreshTokenRepository;
import com.gastonnicora.trips.services.RefreshTokenService;

@ExtendWith(MockitoExtension.class)
public class RevokeTokenTest {
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void shouldRevokeTokenIsPresent() {
        UUID uuid = UUID.randomUUID();
        RefreshToken refreshToken = new RefreshToken("token", uuid, "127.0.0.1", "user-agent", "web", 0);

        when(refreshTokenRepository.findByRefreshToken(any(String.class)))
                .thenReturn(Optional.of(refreshToken));

        refreshTokenService.revokeToken("token");

        verify(refreshTokenRepository).findByRefreshToken("token");

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(captor.capture());

        RefreshToken saved = captor.getValue();
        assertEquals(1, saved.getVersion());
        assertFalse(saved.isActive());
    }

    @Test
    void shouldRevokeTokenIsNotPresent() {

        when(refreshTokenRepository.findByRefreshToken(any(String.class)))
                .thenReturn(Optional.empty());

        refreshTokenService.revokeToken("token");

        verify(refreshTokenRepository).findByRefreshToken("token");

        verify(refreshTokenRepository, never()).save(any());

    }
}
