package com.gastonnicora.trips.unit.refreshToken;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.entities.RefreshToken;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.repositories.RefreshTokenRepository;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.services.RefreshTokenService;

@ExtendWith(MockitoExtension.class)
public class CreateTokenTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void shouldCreateToken() {

        User user = new User("username", "latName", "test@test.com", "password", null);

        userRepository.save(user);

        RefreshToken refreshToken = new RefreshToken("token", user, "127.0.0.1", "user-agent", "device", 0);

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);

        refreshTokenService.createToken("token", user, "user-agent", "127.0.0.1",
                "device", 0);

        verify(refreshTokenRepository).save(captor.capture());

        RefreshToken saved = captor.getValue();

        assertEquals("token", saved.getToken());

        assertEquals(user, saved.getUser());

        assertEquals("127.0.0.1", saved.getIp());

        assertEquals("user-agent", saved.getUserAgent());

        assertEquals("device", saved.getDevice());

        assertEquals(0, saved.getVersion());

        assertTrue(saved.isActive());

        assertNotNull(saved.getRefreshToken());

        assertNotNull(saved.getExpiryDate());
        assertTrue(
                saved.getExpiryDate().isAfter(
                        Instant.now().plus(6, ChronoUnit.DAYS)));

        assertTrue(
                saved.getExpiryDate().isBefore(
                        Instant.now().plus(8, ChronoUnit.DAYS)));
    }

    @Test
    void shouldCreateToken_whenUserAgentIsNull() {

        User user = new User("username", "latName", "test@test.com", "password", null);

        userRepository.save(user);

        RefreshToken refreshToken = new RefreshToken("token", user, "127.0.0.1", null, "device", 0);

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);

        refreshTokenService.createToken("token", user, null, "127.0.0.1",
                "device", 0);

        verify(refreshTokenRepository).save(captor.capture());

        RefreshToken saved = captor.getValue();

        assertEquals("token", saved.getToken());

        assertEquals(user, saved.getUser());

        assertEquals("127.0.0.1", saved.getIp());

        assertEquals("web", saved.getUserAgent());

        assertEquals("device", saved.getDevice());

        assertEquals(0, saved.getVersion());

        assertTrue(saved.isActive());

        assertNotNull(saved.getRefreshToken());

        assertNotNull(saved.getExpiryDate());
        assertTrue(
                saved.getExpiryDate().isAfter(
                        Instant.now().plus(6, ChronoUnit.DAYS)));

        assertTrue(
                saved.getExpiryDate().isBefore(
                        Instant.now().plus(8, ChronoUnit.DAYS)));
    }

    @Test
    void shouldCreateToken_whenUserAgentIsBlanK() {

        User user = new User("username", "latName", "test@test.com", "password", null);

        userRepository.save(user);

        RefreshToken refreshToken = new RefreshToken("token", user, "127.0.0.1", " ", "device", 0);

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);

        refreshTokenService.createToken("token", user, " ", "127.0.0.1",
                "device", 0);

        verify(refreshTokenRepository).save(captor.capture());

        RefreshToken saved = captor.getValue();

        assertEquals("token", saved.getToken());

        assertEquals(user, saved.getUser());

        assertEquals("127.0.0.1", saved.getIp());

        assertEquals("web", saved.getUserAgent());

        assertEquals("device", saved.getDevice());

        assertEquals(0, saved.getVersion());

        assertTrue(saved.isActive());

        assertNotNull(saved.getRefreshToken());

        assertNotNull(saved.getExpiryDate());
        assertTrue(
                saved.getExpiryDate().isAfter(
                        Instant.now().plus(6, ChronoUnit.DAYS)));

        assertTrue(
                saved.getExpiryDate().isBefore(
                        Instant.now().plus(8, ChronoUnit.DAYS)));
    }
}
