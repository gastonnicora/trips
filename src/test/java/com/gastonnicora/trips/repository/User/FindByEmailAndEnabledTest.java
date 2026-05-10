package com.gastonnicora.trips.repository.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
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

public class FindByEmailAndEnabledTest {
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

        List<User> found = userRepository.findByEmailAndEnabled("test@test.com",true);

        assertFalse(found.isEmpty());
        assertTrue(found.get(0).isEnabled());
        assertTrue(found.get(0).getEmail().equals("test@test.com"));
        assertEquals(1, found.size());
    }

    @Test
    void shouldFindByEmailIfNotExist() {

       List<User> found = userRepository.findByEmailAndEnabled("test@test.com",true);

        assertTrue(found.isEmpty());

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

        List<User> found = userRepository.findByEmailAndEnabled("test@test.com",false); // FIXME 🐛: cambiar por lista 

        assertFalse(found.isEmpty());
        assertFalse(found.get(0).isEnabled());
        assertTrue(found.get(0).getEmail().equals("test@test.com"));
        assertEquals(1, found.size());
    }
}
