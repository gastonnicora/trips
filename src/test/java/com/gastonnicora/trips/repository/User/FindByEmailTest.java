package com.gastonnicora.trips.repository.user;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
public class FindByEmailTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldExistsByEmail() {
        User user = new User(
                "username",
                "latName",
                "test@test.com",
                "password",
                Set.of(Role.USER));

        user.setEnabled(true);

        userRepository.save(user);

        User user2 = new User(
                "username2",
                "latName2",
                "test@test.com",
                "password2",
                Set.of(Role.USER));

        user2.setEnabled(true);

        userRepository.save(user2);

        List<User> found = userRepository.findByEmail("test@test.com");

        assertEquals(2, found.size());
        assertFalse(found.isEmpty());
        assertTrue(found.get(0).getEmail().equals("test@test.com"));
        assertTrue(found.get(1).getEmail().equals("test@test.com"));
        assertTrue(found.contains(user));
        assertTrue(found.contains(user2));
    }

    @Test
    void shouldNotExistsByEmail() {

        List<User> found = userRepository.findByEmail("test@test.com");

        assertTrue(found.isEmpty());
        assertTrue(found.isEmpty());
    }
}
