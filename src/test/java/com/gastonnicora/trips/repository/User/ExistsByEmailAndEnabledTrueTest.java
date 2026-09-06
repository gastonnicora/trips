package com.gastonnicora.trips.repository.user;

import java.util.Set;

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
public class ExistsByEmailAndEnabledTrueTest {

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

        boolean found = userRepository.existsByEmailAndEnabledTrue("test@test.com");

        assertTrue(found);
    }

    @Test
    void shouldNotExistsByEmail() {

        boolean found = userRepository.existsByEmailAndEnabledTrue("test@test.com");

        assertFalse(found);
    }

}
