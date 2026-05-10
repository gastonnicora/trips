package com.gastonnicora.trips.repository.user;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
public class ExistsByRoleContainsTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldExistsByRole() {
        User user = new User(
                "username",
                "latName",
                "test@test.com",
                "password",
                Set.of(Role.USER));

        user.setEnabled(true);

        userRepository.save(user);

        boolean found = userRepository.existsByRoleContains(Role.USER);

        assertTrue(found);

    }

    @Test
    void shouldExistsByRoleSuperAdmin() {
        User user = new User(
                "username",
                "latName",
                "test@test.com",
                "password",
                Set.of(Role.SUPER_ADMIN));

        user.setEnabled(true);

        userRepository.save(user);

        boolean found = userRepository.existsByRoleContains(Role.SUPER_ADMIN);

        assertTrue(found);

    }

    @Test
    void shouldNotExistsByRole() {
        boolean found = userRepository.existsByRoleContains(Role.ADMIN);

        assertFalse(found);

    }
}
