package com.gastonnicora.trips.unit.refreshToken;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.repositories.RefreshTokenRepository;
import com.gastonnicora.trips.services.TokenCleanupService;

@ExtendWith(MockitoExtension.class)
class TokenCleanupServiceTest {

    @InjectMocks
    private TokenCleanupService tokenCleanupService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void shouldCleanExpiredTokens() {
        tokenCleanupService.clean();

        ArgumentCaptor<Instant> captor = ArgumentCaptor.forClass(Instant.class);

        verify(refreshTokenRepository).deleteAllByExpiryDateBefore(captor.capture());

        assertTrue(captor.getValue().isBefore(Instant.now().plusSeconds(1)));
    }

    @Test
    void shouldCleanInactiveTokens() {
        tokenCleanupService.cleanInactive();

        verify(refreshTokenRepository).deleteAllByActiveFalse();
    }
}
