package com.gastonnicora.trips.unit.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.request.user.UserChangePassword;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.enums.Role;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.exceptions.ValidationException;
import com.gastonnicora.trips.mappers.UserMapper;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.security.UserDetailsImpl;
import com.gastonnicora.trips.services.RefreshTokenService;
import com.gastonnicora.trips.services.UserService;

@ExtendWith(MockitoExtension.class)
public class UpdatePasswordTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnUserDTO() {
        UUID uuid = UUID.randomUUID();

        User user = new User("John", "Doe", "mail", "pass", new HashSet<>(Set.of(Role.USER)));
        user.setUuid(uuid);
        context(uuid);

        UserChangePassword request = new UserChangePassword();
        request.setPasswordOld("pass");
        request.setPassword("newPass");
        request.setConfirmPassword("newPass");

        when(userRepository.findByUuid(uuid)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass"))
                .thenReturn("encoded-newPass");
        when(passwordEncoder.matches("pass", "pass")).thenReturn(true);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(userMapper.toDTO(user)).thenReturn(new UserDTO());

        userService.updatePassword(request);
        verify(userRepository).findByUuid(uuid);
        verify(passwordEncoder).matches("pass", "pass");
        verify(passwordEncoder).encode("newPass");

        verify(userRepository).save(captor.capture());
        verify(userMapper).toDTO(any(User.class));
        verify(refreshTokenService).deactivateAllByUserUuid(uuid);

        User updated = captor.getValue();

        assertEquals("John", updated.getName());
        assertEquals("encoded-newPass", updated.getPassword());
        assertEquals(Set.of(Role.USER), updated.getRole());
        assertEquals(1, updated.getVersion());

    }

    @Test
    void shouldThrowNotFoundException_whenUserDoesNotExist() {
        UUID uuid = UUID.randomUUID();

        context(uuid);

        UserChangePassword request = new UserChangePassword();
        request.setPasswordOld("pass");
        request.setPassword("newPass");
        request.setConfirmPassword("newPass");

        when(userRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            userService.updatePassword(request);
        });

        assertEquals(404, ex.getStatus());

        verify(userRepository).findByUuid(uuid);
        verify(passwordEncoder, never()).matches("pass", "pass");
        verify(passwordEncoder, never()).encode("newPass");

        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toDTO(any(User.class));
        verify(refreshTokenService, never()).deactivateAllByUserUuid(uuid);

    }

    @Test
    void shouldThrowValidationException_whenPasswordOldIsBad() {
        UUID uuid = UUID.randomUUID();

        User user = new User("John", "Doe", "mail", "pass", new HashSet<>(Set.of(Role.USER)));
        user.setUuid(uuid);
        context(uuid);

        UserChangePassword request = new UserChangePassword();
        request.setPasswordOld("pass");
        request.setPassword("newPass");
        request.setConfirmPassword("newPass");

        when(userRepository.findByUuid(uuid)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "pass")).thenReturn(false);

        ValidationException ex = assertThrows(ValidationException.class, () -> {
            userService.updatePassword(request);
        });

        assertEquals(400, ex.getStatus());

        verify(userRepository).findByUuid(uuid);
        verify(passwordEncoder).matches("pass", "pass");
        verify(passwordEncoder, never()).encode("newPass");

        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toDTO(any(User.class));
        verify(refreshTokenService, never()).deactivateAllByUserUuid(uuid);
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
}
