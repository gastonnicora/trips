package com.gastonnicora.trips.unit.service.refreshToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.entities.RefreshToken;
import com.gastonnicora.trips.exceptions.UnauthorizedException;
import com.gastonnicora.trips.repositories.RefreshTokenRepository;
import com.gastonnicora.trips.services.RefreshTokenService;

@ExtendWith(MockitoExtension.class)
public class VerifyTokenTest {
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void shouldVerifyToken() {

        UUID uuid = UUID.randomUUID();

        RefreshToken refreshToken = new RefreshToken("token", uuid, "127.0.0.1", "user-agent", "web", 0);

        when(refreshTokenRepository.findByRefreshToken(any(String.class)))
                .thenReturn(Optional.of(refreshToken));

        RefreshToken result = refreshTokenService.verifyToken("token", "127.0.0.1", "user-agent");

        verify(refreshTokenRepository).findByRefreshToken("token");

        assertNotNull(result);

        assertEquals("token", result.getToken());

        assertEquals(uuid, result.getUserUuid());

        assertEquals("127.0.0.1", result.getIp());

        assertEquals("user-agent", result.getUserAgent());

        assertEquals("web", result.getDevice());

        assertEquals(0, result.getVersion());

        assertTrue(result.isActive());

        assertNotNull(result.getRefreshToken());

        assertNotNull(result.getExpiryDate());
    }

    @Test
    void shouldThrowsUnauthorizedException_whenTokenIsNull() {

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> {
            refreshTokenService.verifyToken(null, "127.0.0.1", "user-agent");
        });

        assertEquals(401, ex.getStatus());
        assertEquals("Token inválido o expirado", ex.getMessage());

        verify(refreshTokenRepository, never()).findByRefreshToken(any(String.class));

    }

    @Test
    void shouldThrowsUnauthorizedException_whenTokenDoesNotExist() {

        when(refreshTokenRepository.findByRefreshToken("token")).thenReturn(Optional.empty());

        UnauthorizedException ex = assertThrows(UnauthorizedException.class,
                () -> {
                    refreshTokenService.verifyToken("token", "127.0.0.1", "user-agent");

                });
        assertEquals(401, ex.getStatus());
        assertEquals("Token inválido o expirado", ex.getMessage());

        verify(refreshTokenRepository).findByRefreshToken("token");

    }

    @Test
    void shouldThrowsUnauthorizedException_whenTokenIsInactive() {
        UUID uuid = UUID.randomUUID();
        RefreshToken refreshToken = new RefreshToken("token", uuid, "127.0.0.1", "user-agent", "web", 0);
        refreshToken.setActive(false);

        when(refreshTokenRepository.findByRefreshToken(any(String.class)))
                .thenReturn(Optional.of(refreshToken));

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> {
            refreshTokenService.verifyToken("token", "127.0.0.1", "user-agent");
        });

        assertEquals(401, ex.getStatus());
        assertEquals("Token inválido o expirado", ex.getMessage());

        verify(refreshTokenRepository).findByRefreshToken("token");
    }

    @Test
    void shouldThrowsUnauthorizedException_whenTokenIsExpired() {
        UUID uuid = UUID.randomUUID();

        RefreshToken refreshToken = new RefreshToken("token", uuid, "127.0.0.1", "user-agent", "web", 0);
        refreshToken.setExpiryDate(refreshToken.getExpiryDate().minus(8, ChronoUnit.DAYS));

        when(refreshTokenRepository.findByRefreshToken("token")).thenReturn(Optional.of(refreshToken));

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> {
            refreshTokenService.verifyToken("token", "127.0.0.1", "user-agent");
        });
        assertEquals(401, ex.getStatus());
        assertEquals("Token inválido o expirado", ex.getMessage());

        verify(refreshTokenRepository).findByRefreshToken("token");

    }

    @Test
    void shouldThrowsUnauthorizedException_whenCurrentIpIsDifferent() {
        UUID uuid = UUID.randomUUID();

        RefreshToken refreshToken = new RefreshToken("token", uuid, "127.0.0.1", "user-agent", "web", 0);

        when(refreshTokenRepository.findByRefreshToken("token")).thenReturn(Optional.of(refreshToken));

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> {
            refreshTokenService.verifyToken("token", "181.0.0.1", "user-agent");
        });
        assertEquals(401, ex.getStatus());
        assertEquals("Token inválido o expirado", ex.getMessage());

        verify(refreshTokenRepository, times(2)).findByRefreshToken("token");
    } 

    @Test
    void shouldThrowsUnauthorizedException_whenCurrentUserAgentIsDifferent() {
        UUID uuid = UUID.randomUUID();

        RefreshToken refreshToken = new RefreshToken("token", uuid, "127.0.0.1", "user-agent", "web", 0);

        when(refreshTokenRepository.findByRefreshToken("token")).thenReturn(Optional.of(refreshToken));

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> {
            refreshTokenService.verifyToken("token", "127.0.0.1", "user-agent2");
        });
        assertEquals(401, ex.getStatus());
        assertEquals("Token inválido o expirado", ex.getMessage());

        verify(refreshTokenRepository, times(2)).findByRefreshToken("token");
    } 
}
