package com.gastonnicora.trips.unit.user;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.enums.Role;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.UserMapper;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserByUuidTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnUserDtoWhenUserExists() {
        UUID uuid = UUID.randomUUID();

        User user = new User("John", "Doe", "john@mail.com", "pass", Set.of(Role.USER));
        user.setUuid(uuid);

        UserDTO dto = new UserDTO();

        when(userRepository.findByUuid(uuid)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(dto);

        UserDTO result = userService.getUserByUuid(uuid);

        assertNotNull(result);
        verify(userRepository).findByUuid(uuid);
        verify(userMapper).toDTO(user);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserDoesNotExist() {
        UUID uuid = UUID.randomUUID();

        when(userRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            userService.getUserByUuid(uuid);
        });

        assertEquals(404, ex.getStatus());
    }
}
