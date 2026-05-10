package com.gastonnicora.trips.unit.service.refreshToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
public class FindByRefreshTokenTest {
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void shouldFindByRefreshToken() {
        UUID uuid = UUID.randomUUID();

        RefreshToken refreshToken = new RefreshToken("token", uuid, "127.0.0.1", "user-agent", "web", 0);

        String refresh = refreshToken.getRefreshToken();

        when(refreshTokenRepository.findByRefreshToken(any(String.class)))
                .thenReturn(Optional.of(refreshToken));
        Optional<RefreshToken> rt = refreshTokenService.findByRefreshToken(refresh);

        verify(refreshTokenRepository).findByRefreshToken(refresh);

        assertNotNull(rt);

        assertEquals(refresh, rt.get().getRefreshToken());

        assertEquals(uuid, rt.get().getUserUuid());

        assertEquals("127.0.0.1", rt.get().getIp());

        assertEquals("user-agent", rt.get().getUserAgent());

        assertEquals("web", rt.get().getDevice());

        assertEquals(0, rt.get().getVersion());

        assertTrue(rt.get().isActive());

        assertNotNull(rt.get().getRefreshToken());

        assertNotNull(rt.get().getExpiryDate());
    }

    @Test
    void shouldFindByRefreshTokenIsNotPresent() {

        String refresh = UUID.randomUUID().toString();

        when(refreshTokenRepository.findByRefreshToken(any(String.class)))
                .thenReturn(Optional.empty());
        Optional<RefreshToken> rt = refreshTokenService.findByRefreshToken(refresh);

        verify(refreshTokenRepository).findByRefreshToken(refresh);

        assertFalse(rt.isPresent());
    }

}
