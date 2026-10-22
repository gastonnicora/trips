package com.gastonnicora.trips.repository.refreshToken;

import com.gastonnicora.trips.entities.RefreshToken;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.enums.Role;
import com.gastonnicora.trips.repositories.RefreshTokenRepository;
import com.gastonnicora.trips.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FindAllByUserUuidAndActiveTrueTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void shouldReturnEmptyWhenRefreshTokenDoesNotExist() {
        List<RefreshToken> found = refreshTokenRepository.findAllByUser_UuidAndActiveTrue(UUID.randomUUID());

        assertTrue(found.isEmpty());

    }

    @Test
    void shouldReturnTokenWhenRefreshTokenExists() {
        User user = new User(
                "username",
                "latName",
                "test@test.com",
                "password",
                Set.of(Role.USER)
        );

        user.setEnabled(true);
        userRepository.save(user);

        RefreshToken rt = new RefreshToken(
                "token", user, "127.0.0.1",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64)", "web", 0
        );

        refreshTokenRepository.save(rt);

        List<RefreshToken> found = refreshTokenRepository.findAllByUser_UuidAndActiveTrue(user.getUuid());

        assertFalse(found.isEmpty());
        assertEquals(found.getFirst(), rt);
    }
}
