package com.gastonnicora.trips.unit.refreshToken;

import com.gastonnicora.trips.entities.RefreshToken;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.repositories.RefreshTokenRepository;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.services.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RevokeTokenTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void shouldDoNothingWhenTokenDoesNotExist() {

        when(refreshTokenRepository.findByRefreshToken(any(String.class)))
                .thenReturn(Optional.empty());

        refreshTokenService.revokeToken("token");

        verify(refreshTokenRepository).findByRefreshToken("token");

        verify(refreshTokenRepository, never()).save(any());

    }

    @Test
    void shouldRevokeTokenWhenItExists() {

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
}
