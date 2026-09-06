package com.gastonnicora.trips.unit.user;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.response.ListResponse;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.enums.Role;
import com.gastonnicora.trips.mappers.UserMapper;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.services.UserService;

@ExtendWith(MockitoExtension.class)
public class GetUsersTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnListOfUsers() {
        User user1 = new User("John", "Doe", "mail1", "pass", Set.of(Role.USER));
        User user2 = new User("Jane", "Doe", "mail2", "pass", Set.of(Role.ADMIN));

        List<User> users = List.of(user1, user2);

        List<UserDTO> dtos = List.of(new UserDTO(), new UserDTO());

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDTOList(users)).thenReturn(dtos);

        ListResponse<UserDTO> result = userService.getUsers();

        assertNotNull(result);
        assertEquals(2, result.getData().size());

        verify(userRepository).findAll();
        verify(userMapper).toDTOList(users);
    }
}
