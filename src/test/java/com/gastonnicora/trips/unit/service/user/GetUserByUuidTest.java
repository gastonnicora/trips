package com.gastonnicora.trips.unit.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.enums.Role;
import com.gastonnicora.trips.exceptions.ErrorException;
import com.gastonnicora.trips.mappers.UserMapper;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.services.UserService;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
class GetUserByUuidTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper; // FIXME 🐛: Cambiar usermapper en service al constructor

    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnUserDTO_whenUserExists() {
        UUID uuid = UUID.randomUUID();

        User user = new User("John", "Doe", "john@mail.com", "pass", Set.of(Role.USER));
        user.setUuid(uuid);

        UserDTO dto = new UserDTO(uuid, "John", "Doe", "john@mail.com", Set.of(Role.USER), true, null, null);

        when(userRepository.findByUuid(uuid)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(dto);

        UserDTO result = userService.getUserByUuid(uuid);

        assertNotNull(result);
        verify(userRepository).findByUuid(uuid);
        verify(userMapper).toDTO(user);
    }

    @Test
    void shouldThrowNotFound_whenUserDoesNotExist() {
        UUID uuid = UUID.randomUUID();

        when(userRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        ErrorException ex = assertThrows(ErrorException.class, () -> {
            userService.getUserByUuid(uuid);
        });

        assertEquals(404, ex.getStatus());
        assertEquals("El usuario buscado no existe", ex.getMessage());
    }
}
