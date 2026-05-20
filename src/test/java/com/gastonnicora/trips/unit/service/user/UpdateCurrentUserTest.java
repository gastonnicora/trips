package com.gastonnicora.trips.unit.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.request.User.UserPut;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.enums.Role;
import com.gastonnicora.trips.exceptions.ConflictException;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.UserMapper;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.security.UserDetailsImpl;
import com.gastonnicora.trips.services.RefreshTokenService;
import com.gastonnicora.trips.services.UserService;

@ExtendWith(MockitoExtension.class)
public class UpdateCurrentUserTest {
        @Mock
        private UserRepository userRepository;

        @Mock
        private UserMapper userMapper;

        @Mock
        private RefreshTokenService refreshTokenService;

        @InjectMocks
        private UserService userService;

        @AfterEach
        void cleanup() {
                SecurityContextHolder.clearContext();
        }

        @Test
        void shouldReturnUserDTOIfNotChangeEmail() {
                UUID uuid = UUID.randomUUID();

                User user = new User("John", "Doe", "john@mail.com", "pass", Set.of(Role.USER));
                user.setUuid(uuid);
                // 🔧 Mock SecurityContext
                UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
                when(userDetails.getUuid()).thenReturn(uuid);

                Authentication auth = mock(Authentication.class);
                when(auth.getPrincipal()).thenReturn(userDetails);

                SecurityContext context = mock(SecurityContext.class);
                when(context.getAuthentication()).thenReturn(auth);

                SecurityContextHolder.setContext(context);

                UserPut request = new UserPut();
                request.setName("Juan");
                request.setLastname("Perez");
                request.setEmail("john@mail.com");

                when(userRepository.findByUuid(uuid))
                                .thenReturn(Optional.of(user));

                when(userRepository.save(any(User.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                when(userMapper.toDTO(any(User.class)))
                                .thenReturn(new UserDTO());

                ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

                userService.updateCurrentUser(request);

                verify(userRepository).findByUuid(uuid);
                verify(userRepository, never()).existsByEmailAndEnabledTrue(any());

                verify(userRepository).save(captor.capture());
                verify(userMapper).toDTO(any(User.class));

                verify(refreshTokenService, never()).deactivateAllByUserUuid(any());

                User saved = captor.getValue();

                assertEquals("Juan", saved.getName());
                assertEquals("Perez", saved.getLastname());
                assertEquals("john@mail.com", saved.getEmail());
                assertEquals(0, saved.getVersion());
        }

        @Test
        void shouldReturnUserDTOIfChangeEmailAndNotUsed() {
                UUID uuid = UUID.randomUUID();

                User user = new User("John", "Doe", "john@mail.com", "pass", Set.of(Role.USER));
                user.setUuid(uuid);

                // 🔧 Mock SecurityContext
                UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
                when(userDetails.getUuid()).thenReturn(uuid);

                Authentication auth = mock(Authentication.class);
                when(auth.getPrincipal()).thenReturn(userDetails);

                SecurityContext context = mock(SecurityContext.class);
                when(context.getAuthentication()).thenReturn(auth);

                SecurityContextHolder.setContext(context);

                UserPut request = new UserPut();
                request.setName("Juan");
                request.setLastname("Perez");
                request.setEmail("Juan@mail.com");

                when(userRepository.findByUuid(uuid))
                                .thenReturn(Optional.of(user));

                when(userRepository.existsByEmailAndEnabledTrue(request.getEmail()))
                                .thenReturn(false);

                when(userRepository.save(any(User.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                when(userMapper.toDTO(any(User.class)))
                                .thenReturn(new UserDTO());

                ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

                userService.updateCurrentUser(request);

                verify(userRepository).findByUuid(uuid);
                verify(userRepository).existsByEmailAndEnabledTrue(request.getEmail());

                verify(userRepository).save(captor.capture());
                verify(userMapper).toDTO(any(User.class));

                User saved = captor.getValue();

                assertEquals("Juan", saved.getName());
                assertEquals("Perez", saved.getLastname());
                assertEquals("juan@mail.com", saved.getEmail());
                assertEquals(1, saved.getVersion());
                verify(refreshTokenService).deactivateAllByUserUuid(uuid);
        }

        @Test
        void shouldThrowNotFoundException_whenUserDoesNotExist() {
                UUID uuid = UUID.randomUUID();
                // 🔧 Mock SecurityContext
                UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
                when(userDetails.getUuid()).thenReturn(uuid);

                Authentication auth = mock(Authentication.class);
                when(auth.getPrincipal()).thenReturn(userDetails);

                SecurityContext context = mock(SecurityContext.class);
                when(context.getAuthentication()).thenReturn(auth);

                SecurityContextHolder.setContext(context);
                UserPut request = new UserPut();
                request.setName("Juan");
                request.setLastname("Perez");
                request.setEmail("Juan@mail.com");

                when(userRepository.findByUuid(uuid))
                                .thenReturn(Optional.empty());

                NotFoundException ex = assertThrows(NotFoundException.class, () -> {
                        userService.updateCurrentUser(request);
                });

                assertEquals(404, ex.getStatus());

                verify(userRepository).findByUuid(uuid);
                verify(userRepository, never()).save(any());
                verify(userMapper, never()).toDTO(any());
                verify(refreshTokenService, never()).deactivateAllByUserUuid(any());

        }

        @Test
        void shouldThrowConflictException_whenUserChangeEmailAndUsed() {
                UUID uuid = UUID.randomUUID();

                User user = new User("John", "Doe", "john@mail.com", "pass", Set.of(Role.USER));
                user.setUuid(uuid);
                // 🔧 Mock SecurityContext
                UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
                when(userDetails.getUuid()).thenReturn(uuid);

                Authentication auth = mock(Authentication.class);
                when(auth.getPrincipal()).thenReturn(userDetails);

                SecurityContext context = mock(SecurityContext.class);
                when(context.getAuthentication()).thenReturn(auth);

                SecurityContextHolder.setContext(context);
                UserPut request = new UserPut();
                request.setName("Juan");
                request.setLastname("Perez");
                request.setEmail("Juan@mail.com");

                when(userRepository.findByUuid(uuid))
                                .thenReturn(Optional.of(user));

                when(userRepository.existsByEmailAndEnabledTrue(request.getEmail()))
                                .thenReturn(true);

                ConflictException ex = assertThrows(ConflictException.class, () -> {
                        userService.updateCurrentUser(request);
                });

                assertEquals(409, ex.getStatus());

                verify(userRepository).findByUuid(uuid);
                verify(userRepository).existsByEmailAndEnabledTrue(request.getEmail());
                verify(userRepository, never()).save(any());
                verify(userMapper, never()).toDTO(any());
                verify(refreshTokenService, never()).deactivateAllByUserUuid(any());
        }
}
