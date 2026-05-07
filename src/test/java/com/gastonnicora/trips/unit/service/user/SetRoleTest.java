package com.gastonnicora.trips.unit.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.request.User.UserChangeRole;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.enums.Role;
import com.gastonnicora.trips.exceptions.ValidationException;
import com.gastonnicora.trips.mappers.UserMapper;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.services.UserService;

@ExtendWith(MockitoExtension.class)
public class SetRoleTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldAddAdminRole() {
        UUID uuid = UUID.randomUUID();

        User user = new User("John", "Doe", "mail", "pass", new HashSet<>(Set.of(Role.USER)));
        user.setUuid(uuid);

        UserChangeRole request = new UserChangeRole();
        request.setRoles(new HashSet<>(Set.of(Role.USER, Role.ADMIN)));

        when(userRepository.findByUuid(uuid)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(new UserDTO());

        userService.setRole(uuid, request);

        // Validaciones
        assertEquals(Set.of(Role.ADMIN, Role.USER), user.getRole());

        // Verificaciones de Mockito
        verify(userRepository).findByUuid(uuid);
        verify(userRepository).save(user);
        verify(userMapper).toDTO(user);
    }

    @Test
    void shouldForceUserRoleAlwaysPresent() {
        UUID uuid = UUID.randomUUID();

        User user = new User("John", "Doe", "mail", "pass", new HashSet<>(Set.of(Role.USER)));
        user.setUuid(uuid);

        UserChangeRole request = new UserChangeRole();
        request.setRoles(new HashSet<>(Set.of(Role.ADMIN)));

        when(userRepository.findByUuid(uuid)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(new UserDTO());

        userService.setRole(uuid, request);

        // Validaciones
        assertTrue(user.getRole().contains(Role.USER));
        assertEquals(Set.of(Role.ADMIN, Role.USER), user.getRole());

        // Verificaciones de Mockito
        verify(userRepository).findByUuid(uuid);
        verify(userRepository).save(user);
        verify(userMapper).toDTO(user);
    }

    @Test
    void shouldAddRoleIfEmpty() {
        UUID uuid = UUID.randomUUID();

        User user = new User("John", "Doe", "mail", "pass", new HashSet<>(Set.of(Role.ADMIN)));
        user.setUuid(uuid);

        UserChangeRole request = new UserChangeRole();
        request.setRoles(new HashSet<>(Set.of()));

        when(userRepository.findByUuid(uuid)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(new UserDTO());

        userService.setRole(uuid, request);

        // Validaciones
        assertTrue(user.getRole().contains(Role.USER));
        assertEquals(Set.of(Role.USER), user.getRole());
        assertFalse(user.getRole().contains(Role.ADMIN));

        // Verificaciones de Mockito
        verify(userRepository).findByUuid(uuid);
        verify(userRepository).save(user);
        verify(userMapper).toDTO(user);
    }

    @Test
    void shouldRemoveSuperAdminFromRequest() {
        UUID uuid = UUID.randomUUID();

        User user = new User("John", "Doe", "mail", "pass", Set.of(Role.USER));
        user.setUuid(uuid);

        UserChangeRole request = new UserChangeRole();
        request.setRoles(new HashSet<>(Set.of(Role.ADMIN, Role.SUPER_ADMIN)));

        when(userRepository.findByUuid(uuid)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(new UserDTO());

        userService.setRole(uuid, request);

        assertFalse(user.getRole().contains(Role.SUPER_ADMIN));
        assertTrue(user.getRole().contains(Role.ADMIN));
        assertTrue(user.getRole().contains(Role.USER));
        assertFalse(user.getRole().contains(Role.SUPER_ADMIN));

        verify(userRepository).findByUuid(uuid);
        verify(userRepository).save(user);
        verify(userMapper).toDTO(user);
    }

    @Test
    void shouldThrowException_whenUserIsSuperAdmin() {
        UUID uuid = UUID.randomUUID();

        User user = new User("Super", "Admin", "mail", "pass", Set.of(Role.SUPER_ADMIN));
        user.setUuid(uuid);

        when(userRepository.findByUuid(uuid)).thenReturn(Optional.of(user));

        UserChangeRole request = new UserChangeRole();
        request.setRoles(new HashSet<>(Set.of(Role.ADMIN)));

        assertThrows(ValidationException.class, () -> userService.setRole(uuid, request));

        verify(userRepository).findByUuid(uuid);
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toDTO(any());
    }
}