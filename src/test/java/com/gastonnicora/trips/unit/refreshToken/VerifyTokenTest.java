package com.gastonnicora.trips.unit.refreshToken;

import com.gastonnicora.trips.entities.RefreshToken;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.exceptions.UnauthorizedException;
import com.gastonnicora.trips.repositories.RefreshTokenRepository;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.services.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerifyTokenTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void shouldThrowUnauthorizedExceptionWhenCurrentIpDiffers() {

        User user = new User("username", "latName", "test@test.com", "password", null);

        userRepository.save(user);

        RefreshToken refreshToken = new RefreshToken("token", user, "127.0.0.1", "user-agent", "web", 0);

        when(refreshTokenRepository.findByRefreshToken("token")).thenReturn(Optional.of(refreshToken));

        UnauthorizedException ex = assertThrows(
                UnauthorizedException.class, () -> {
                    refreshTokenService.verifyToken("token", "181.0.0.1", "user-agent");
                }
                                               );
        assertEquals(401, ex.getStatus());
        assertEquals("Token inválido o expirado", ex.getMessage());

        verify(refreshTokenRepository, times(2)).findByRefreshToken("token");
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenCurrentUserAgentDiffers() {

        User user = new User("username", "latName", "test@test.com", "password", null);

        userRepository.save(user);

        RefreshToken refreshToken = new RefreshToken("token", user, "127.0.0.1", "user-agent", "web", 0);

        when(refreshTokenRepository.findByRefreshToken("token")).thenReturn(Optional.of(refreshToken));

        UnauthorizedException ex = assertThrows(
                UnauthorizedException.class, () -> {
                    refreshTokenService.verifyToken("token", "127.0.0.1", "user-agent2");
                }
                                               );
        assertEquals(401, ex.getStatus());
        assertEquals("Token inválido o expirado", ex.getMessage());

        verify(refreshTokenRepository, times(2)).findByRefreshToken("token");
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenTokenDoesNotExist() {

        when(refreshTokenRepository.findByRefreshToken("token")).thenReturn(Optional.empty());

        UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> {
                    refreshTokenService.verifyToken("token", "127.0.0.1", "user-agent");

                }
                                               );
        assertEquals(401, ex.getStatus());
        assertEquals("Token inválido o expirado", ex.getMessage());

        verify(refreshTokenRepository).findByRefreshToken("token");

    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenTokenIsExpired() {

        User user = new User("username", "latName", "test@test.com", "password", null);

        userRepository.save(user);

        RefreshToken refreshToken = new RefreshToken("token", user, "127.0.0.1", "user-agent", "web", 0);
        refreshToken.setExpiryDate(refreshToken.getExpiryDate().minus(8, ChronoUnit.DAYS));

        when(refreshTokenRepository.findByRefreshToken("token")).thenReturn(Optional.of(refreshToken));

        UnauthorizedException ex = assertThrows(
                UnauthorizedException.class, () -> {
                    refreshTokenService.verifyToken("token", "127.0.0.1", "user-agent");
                }
                                               );
        assertEquals(401, ex.getStatus());
        assertEquals("Token inválido o expirado", ex.getMessage());

        verify(refreshTokenRepository).findByRefreshToken("token");

    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenTokenIsInactive() {

        User user = new User("username", "latName", "test@test.com", "password", null);

        userRepository.save(user);

        RefreshToken refreshToken = new RefreshToken("token", user, "127.0.0.1", "user-agent", "web", 0);
        refreshToken.setActive(false);

        when(refreshTokenRepository.findByRefreshToken(any(String.class)))
                .thenReturn(Optional.of(refreshToken));

        UnauthorizedException ex = assertThrows(
                UnauthorizedException.class, () -> {
                    refreshTokenService.verifyToken("token", "127.0.0.1", "user-agent");
                }
                                               );

        assertEquals(401, ex.getStatus());
        assertEquals("Token inválido o expirado", ex.getMessage());

        verify(refreshTokenRepository).findByRefreshToken("token");
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenTokenIsNull() {

        UnauthorizedException ex = assertThrows(
                UnauthorizedException.class, () -> {
                    refreshTokenService.verifyToken(null, "127.0.0.1", "user-agent");
                }
                                               );

        assertEquals(401, ex.getStatus());
        assertEquals("Token inválido o expirado", ex.getMessage());

        verify(refreshTokenRepository, never()).findByRefreshToken(any(String.class));

    }

    @Test
    void shouldVerifyToken() {

        User user = new User("username", "latName", "test@test.com", "password", null);

        userRepository.save(user);

        RefreshToken refreshToken = new RefreshToken("token", user, "127.0.0.1", "user-agent", "web", 0);

        when(refreshTokenRepository.findByRefreshToken(any(String.class)))
                .thenReturn(Optional.of(refreshToken));

        RefreshToken result = refreshTokenService.verifyToken("token", "127.0.0.1", "user-agent");

        verify(refreshTokenRepository).findByRefreshToken("token");

        assertNotNull(result);

        assertEquals("token", result.getToken());

        assertEquals(user, result.getUser());

        assertEquals("127.0.0.1", result.getIp());

        assertEquals("user-agent", result.getUserAgent());

        assertEquals("web", result.getDevice());

        assertEquals(0, result.getVersion());

        assertTrue(result.isActive());

        assertNotNull(result.getRefreshToken());

        assertNotNull(result.getExpiryDate());
    }
}
