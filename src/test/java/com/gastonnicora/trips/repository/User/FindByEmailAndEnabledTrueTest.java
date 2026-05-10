package com.gastonnicora.trips.repository.user;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.Set;

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
public class FindByEmailAndEnabledTrueTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindByEmail() {

        User user = new User(
                "username",
                "latName",
                "test@test.com",
                "password",
                Set.of(Role.USER));

        user.setEnabled(true);

        userRepository.save(user);

        Optional<User> found = userRepository.findByEmailAndEnabledTrue("test@test.com");

        assertTrue(found.isPresent());
    }

    @Test
    void shouldFindByEmailIfNotExist() {

        Optional<User> found = userRepository.findByEmailAndEnabledTrue("test@test.com");

        assertFalse(found.isPresent());
    }

    @Test
    void shouldFindByEmailIsNotEnabled() {

        User user = new User(
                "username",
                "latName",
                "test@test.com",
                "password",
                Set.of(Role.USER));

        user.setEnabled(false);

        userRepository.save(user);

        Optional<User> found = userRepository.findByEmailAndEnabledTrue("test@test.com");

        assertFalse(found.isPresent());
    }
}
