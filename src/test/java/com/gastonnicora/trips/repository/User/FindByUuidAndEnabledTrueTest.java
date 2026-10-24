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
class FindByUuidAndEnabledTrueTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldReturnEmptyWhenUuidDoesNotExist() {
        Optional<User> found = userRepository.findByUuidAndEnabledTrue(UUID.randomUUID());

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnUserWhenEnabled() {
        User user = saveUser(true);

        Optional<User> found = userRepository.findByUuidAndEnabledTrue(user.getUuid());

        assertTrue(found.isPresent());
        assertEquals(user.getUuid(), found.get().getUuid());
        assertEquals("test@test.com", found.get().getEmail());
    }

    @Test
    void shouldReturnEmptyWhenUserIsDisabled() {
        User user = saveUser(false);

        Optional<User> found = userRepository.findByUuidAndEnabledTrue(user.getUuid());

        assertTrue(found.isEmpty());
    }

    private User saveUser(boolean enabled) {
        User user = new User(
                "username",
                "latName",
                "test@test.com",
                "password",
                Set.of(Role.USER)
        );
        user.setEnabled(enabled);
        return userRepository.save(user);
    }
}