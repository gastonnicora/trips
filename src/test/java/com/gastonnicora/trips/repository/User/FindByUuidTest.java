package com.gastonnicora.trips.repository.user;

import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.enums.Role;
import com.gastonnicora.trips.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FindByUuidTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldReturnEmptyWhenUuidDoesNotExist() {

        Optional<User> found = userRepository.findByUuid(UUID.randomUUID());

        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnEntityWhenIdExists() {

        User user = new User(
                "username",
                "latName",
                "test@test.com",
                "password",
                Set.of(Role.USER));

        user.setEnabled(true);

        userRepository.save(user);

        Optional<User> found = userRepository.findById(user.getUuid());

        assertTrue(found.isPresent());
        assertEquals(found.get().getUuid(), user.getUuid());
        assertEquals("test@test.com", found.get().getEmail());
        assertEquals("username", found.get().getName());
    }

    @Test
    void shouldReturnEntityWhenUuidExists() {

        User user = new User(
                "username",
                "latName",
                "test@test.com",
                "password",
                Set.of(Role.USER));

        user.setEnabled(true);

        userRepository.save(user);

        Optional<User> found = userRepository.findByUuid(user.getUuid());

        assertTrue(found.isPresent());
        assertEquals(found.get().getUuid(), user.getUuid());
        assertEquals("test@test.com", found.get().getEmail());
        assertEquals("username", found.get().getName());
    }
}
