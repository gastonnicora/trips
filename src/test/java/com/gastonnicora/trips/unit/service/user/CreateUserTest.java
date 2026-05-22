package com.gastonnicora.trips.unit.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.request.user.UserCreate;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.enums.Role;
import com.gastonnicora.trips.exceptions.ConflictException;
import com.gastonnicora.trips.mappers.UserMapper;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.services.UserService;

@ExtendWith(MockitoExtension.class)
public class CreateUserTest {
        @Mock
        private UserRepository userRepository;

        @Mock
        private UserMapper userMapper;

        @Mock
        private PasswordEncoder passwordEncoder;

        @InjectMocks
        private UserService userService;

        @Test
        void shouldCreateUser() {
                UserCreate request = new UserCreate();
                request.setName("John");
                request.setLastname("Doe");
                request.setEmail("mail");
                request.setPassword("pass");

                when(userRepository.existsByEmailAndEnabledTrue(request.getEmail()))
                                .thenReturn(false);

                when(passwordEncoder.encode("pass"))
                                .thenReturn("encoded-pass");

                ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

                when(userRepository.save(any(User.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                when(userMapper.toDTO(any(User.class)))
                                .thenReturn(new UserDTO());

                userService.createUser(request);

                verify(userRepository).existsByEmailAndEnabledTrue(request.getEmail());
                verify(passwordEncoder).encode("pass");

                verify(userRepository).save(captor.capture());
                verify(userMapper).toDTO(any(User.class));

                User saved = captor.getValue();

                assertEquals("John", saved.getName());
                assertEquals("encoded-pass", saved.getPassword());
                assertEquals(Set.of(Role.USER), saved.getRole());
        }

        @Test
        void shouldThrowConflictException_whenEmailIsUsed() {
                UserCreate request = new UserCreate();
                request.setName("John");
                request.setLastname("Doe");
                request.setEmail("mail");
                request.setPassword("pass");

                when(userRepository.existsByEmailAndEnabledTrue(request.getEmail()))
                                .thenReturn(true);

                assertThrows(ConflictException.class,
                                () -> userService.createUser(request));

                verify(userRepository).existsByEmailAndEnabledTrue(request.getEmail());
                verify(passwordEncoder, never()).encode("pass");
                verify(userRepository, never()).save(any(User.class));
                verify(userMapper, never()).toDTO(any(User.class));

        }
}
