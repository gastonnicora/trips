package com.gastonnicora.trips.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.gastonnicora.trips.dtos.entities.UserDTOs;
import com.gastonnicora.trips.dtos.request.User.UserCreate;
import com.gastonnicora.trips.enums.Role;
import com.gastonnicora.trips.mappers.UserMapper;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.services.UserService;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSaveUser() {

        UserCreate user = new UserCreate("test", "test", "test", "test", "test");
        when(userMapper.toDTO(any())).thenAnswer(invocation -> {
            UserDTOs dto = new UserDTOs(null, user.getName(), user.getLastname(), user.getEmail(), Set.of(Role.USER),
                    true, null, null);
            return dto;
        });

        UserDTOs result = userService.createUser(user);

        assertEquals("test", result.getName());
        assertEquals("test", result.getLastname());
        assertEquals("test", result.getEmail());
        assertEquals(Set.of(Role.USER), result.getRole());
        assertEquals(true, result.isEnabled());
        assertEquals(null, result.getCreatedAt());
        assertEquals(null, result.getUpdatedAt());
    }
}
