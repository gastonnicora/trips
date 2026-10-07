package com.gastonnicora.trips.unit.user;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.UserMapper;
import com.gastonnicora.trips.repositories.RefreshTokenRepository;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.services.RefreshTokenService;
import com.gastonnicora.trips.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class GetUserTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Test
    void shouldReturnUserSuccessfully() {
        UUID uuid = UUID.randomUUID();
        User user = new User();
        user.setUuid(uuid);

        when(userRepository.findByUuid(uuid)).thenReturn(Optional.of(user));

        User result = userService.getUser(uuid);

        assertEquals(user, result);
        verify(userRepository).findByUuid(uuid);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserDoesNotExist() {
        UUID uuid = UUID.randomUUID();

        when(userRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUser(uuid));

        assertEquals(404, exception.getStatus());
        assertEquals("El usuario solicitado no existe", exception.getMessage());
        verify(userRepository).findByUuid(uuid);
    }
}
