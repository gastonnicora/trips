package com.gastonnicora.trips.unit.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.enums.Role;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.services.UserService;

@ExtendWith(MockitoExtension.class)
public class CreateSuperAdminTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldCreateSuperAdminSuccessfully() {
        String email = "superAdmin@test.com";
        String password = "password";

        when(userRepository.existsByRoleContains(Role.SUPER_ADMIN)).thenReturn(false);
        when(userRepository.existsByEmailAndEnabledTrue(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("encoded-password");

        userService.createSuperAdminIfNotExists(email, password);
        verify(userRepository).existsByRoleContains(Role.SUPER_ADMIN);
        verify(userRepository).existsByEmailAndEnabledTrue(email);
        verify(passwordEncoder).encode(password);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        User saved = captor.getValue();

        assertEquals("superAdmin@test.com", saved.getEmail());
        assertEquals("encoded-password", saved.getPassword());
        assertEquals(Set.of(Role.SUPER_ADMIN, Role.USER), saved.getRole());

    }

    @Test
    void shouldFailCreateSuperAdminIfSuperAdminExists() {
        String email = "superAdmin@test.com";
        String password = "password";

        when(userRepository.existsByRoleContains(Role.SUPER_ADMIN)).thenReturn(true);

        userService.createSuperAdminIfNotExists(email, password);
        verify(userRepository).existsByRoleContains(Role.SUPER_ADMIN);
        verify(userRepository, never()).existsByEmailAndEnabledTrue(email);
        verify(passwordEncoder, never()).encode(password);

        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    void shouldFailCreateSuperAdminIfEmailIsNull() {
        String email = null;
        String password = "password";

        when(userRepository.existsByRoleContains(Role.SUPER_ADMIN)).thenReturn(false);

        userService.createSuperAdminIfNotExists(email, password);
        verify(userRepository).existsByRoleContains(Role.SUPER_ADMIN);
        verify(userRepository, never()).existsByEmailAndEnabledTrue(email);
        verify(passwordEncoder, never()).encode(password);

        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    void shouldFailCreateSuperAdminIfPasswordIsNull() {
        String email = "superAdmin@test.com";
        String password = null;

        when(userRepository.existsByRoleContains(Role.SUPER_ADMIN)).thenReturn(false);

        userService.createSuperAdminIfNotExists(email, password);
        verify(userRepository).existsByRoleContains(Role.SUPER_ADMIN);
        verify(userRepository, never()).existsByEmailAndEnabledTrue(email);
        verify(passwordEncoder, never()).encode(password);

        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    void shouldFailCreateSuperAdminIfEmailUsed() {
        String email = "superAdmin@test.com";
        String password = "password";

        when(userRepository.existsByRoleContains(Role.SUPER_ADMIN)).thenReturn(false);
        when(userRepository.existsByEmailAndEnabledTrue(email)).thenReturn(true);

        userService.createSuperAdminIfNotExists(email, password);
        verify(userRepository).existsByRoleContains(Role.SUPER_ADMIN);
        verify(userRepository).existsByEmailAndEnabledTrue(email);
        verify(passwordEncoder, never()).encode(password);

        verify(userRepository, never()).save(any(User.class));

    }
}
