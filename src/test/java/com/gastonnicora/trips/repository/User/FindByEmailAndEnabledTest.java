package com.gastonnicora.trips.repository.user;

import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.enums.Role;
import com.gastonnicora.trips.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FindByEmailAndEnabledTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindByEmail() {

        User user = new User(
                "username",
                "latName",
                "test@test.com",
                "password",
                Set.of(Role.USER)
        );

        user.setEnabled(true);

        userRepository.save(user);

        List<User> found = userRepository.findByEmailAndEnabled("test@test.com", true);

        assertFalse(found.isEmpty());
        assertTrue(found.getFirst().isEnabled());
        assertEquals("test@test.com", found.getFirst().getEmail());
        assertEquals(1, found.size());
    }

    @Test
    void shouldFindByEmailIfNotExist() {

        List<User> found = userRepository.findByEmailAndEnabled("test@test.com", true);

        assertTrue(found.isEmpty());

    }

    @Test
    void shouldFindByEmailIsNotEnabled() {

        User user = new User(
                "username",
                "latName",
                "test@test.com",
                "password",
                Set.of(Role.USER)
        );

        user.setEnabled(false);

        userRepository.save(user);

        List<User> found = userRepository.findByEmailAndEnabled("test@test.com", false);

        assertFalse(found.isEmpty());
        assertFalse(found.getFirst().isEnabled());
        assertEquals("test@test.com", found.getFirst().getEmail());
        assertEquals(1, found.size());
    }
}
