package com.gastonnicora.trips.unit.refreshToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.entities.RefreshToken;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.repositories.RefreshTokenRepository;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.services.RefreshTokenService;

@ExtendWith(MockitoExtension.class)
public class RevokeTokenTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void shouldRevokeTokenIsPresent() {

        User user = new User("username", "latName", "test@test.com", "password", null);

        userRepository.save(user);

        RefreshToken refreshToken = new RefreshToken("token", user, "127.0.0.1", "user-agent", "web", 0);

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
