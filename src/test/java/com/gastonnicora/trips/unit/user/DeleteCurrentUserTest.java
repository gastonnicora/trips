package com.gastonnicora.trips.unit.user;

import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.enums.Role;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.security.UserDetailsImpl;
import com.gastonnicora.trips.services.RefreshTokenService;
import com.gastonnicora.trips.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteCurrentUserTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private UserService userService;

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    private void context(UUID uuid) {
        // 🔧 Mock SecurityContext
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when(userDetails.getUuid()).thenReturn(uuid);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(context);
    }

    @Test
    void shouldDisableCurrentUserSuccessfully() {
        UUID uuid = UUID.randomUUID();

        User user = new User("John", "Doe", "mail", "pass", new HashSet<>(Set.of(Role.USER)));
        user.setUuid(uuid);
        context(uuid);

        when(userRepository.findByUuid(uuid)).thenReturn(Optional.of(user));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        userService.deleteCurrentUser();
        verify(userRepository).findByUuid(uuid);

        verify(userRepository).save(captor.capture());
        verify(refreshTokenService).deactivateAllByUserUuid(uuid);

        User deleted = captor.getValue();

        assertEquals(1, deleted.getVersion());
        assertFalse(deleted.isEnabled());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserDoesNotExist() {
        UUID uuid = UUID.randomUUID();

        context(uuid);

        when(userRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class, () -> {
                    userService.deleteCurrentUser();
                }
                                           );

        assertEquals(404, ex.getStatus());

        verify(userRepository).findByUuid(uuid);
        verify(refreshTokenService, never()).deactivateAllByUserUuid(any());
        verify(userRepository, never()).save(any());
    }
}
