package com.gastonnicora.trips.repository.user;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.enums.Role;
import com.gastonnicora.trips.repositories.UserRepository;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FindByUuidTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindByUuid() {

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
        assertTrue(found.get().getUuid().equals(user.getUuid()));
        assertTrue(found.get().getEmail().equals("test@test.com"));
        assertTrue(found.get().getName().equals("username"));
    }

    @Test
    void shouldFindByUuidIfNotExist() {

        Optional<User> found = userRepository.findByUuid(UUID.randomUUID());

        assertFalse(found.isPresent());
    }

    @Test
    void shouldFindById() {

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
        assertTrue(found.get().getUuid().equals(user.getUuid()));
        assertTrue(found.get().getEmail().equals("test@test.com"));
        assertTrue(found.get().getName().equals("username"));
    }
}
