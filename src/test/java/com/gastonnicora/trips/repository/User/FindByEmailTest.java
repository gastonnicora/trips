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
class FindByEmailTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {

        List<User> found = userRepository.findByEmail("test@test.com");

        assertTrue(found.isEmpty());
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
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
        assertEquals("test@test.com", found.get(0).getEmail());
        assertEquals("test@test.com", found.get(1).getEmail());
        assertTrue(found.contains(user));
        assertTrue(found.contains(user2));
    }
}
