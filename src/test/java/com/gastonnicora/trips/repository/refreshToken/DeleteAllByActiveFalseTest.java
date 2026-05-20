package com.gastonnicora.trips.repository.refreshToken;

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

import com.gastonnicora.trips.entities.RefreshToken;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.enums.Role;
import com.gastonnicora.trips.repositories.RefreshTokenRepository;
import com.gastonnicora.trips.repositories.UserRepository;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DeleteAllByActiveFalseTest {
        @Autowired
        private UserRepository userRepository;

        @Autowired
        private RefreshTokenRepository refreshTokenRepository;

        @Test
        void shouldDeleteAllByActiveFalse() {
                User user = new User(
                                "username",
                                "latName",
                                "test@test.com",
                                "password",
                                Set.of(Role.USER));

                user.setEnabled(true);
                userRepository.save(user);

                RefreshToken rt = new RefreshToken("token", user, "127.0.0.1",
                                "Mozilla/5.0 (Windows NT 10.0; Win64; x64)", "web", 0);
                RefreshToken rt2 = new RefreshToken("token2", user, "127.0.0.1",
                                "Mozilla/5.0 (Windows NT 10.0; Win64; x64)", "web", 0);
                RefreshToken rt3 = new RefreshToken("token3", user, "127.0.0.1",
                                "Mozilla/5.0 (Windows NT 10.0; Win64; x64)", "web", 0);

                rt.setActive(false);
                rt2.setActive(false);

                refreshTokenRepository.save(rt);
                refreshTokenRepository.save(rt2);
                refreshTokenRepository.save(rt3);

                refreshTokenRepository.deleteAllByActiveFalse();

                Optional<RefreshToken> found = refreshTokenRepository.findByRefreshToken(rt.getRefreshToken());

                assertFalse(found.isPresent());

                Optional<RefreshToken> found2 = refreshTokenRepository.findByRefreshToken(rt2.getRefreshToken());

                assertFalse(found2.isPresent());

                Optional<RefreshToken> found3 = refreshTokenRepository.findByRefreshToken(rt3.getRefreshToken());

                assertTrue(found3.isPresent());
        }

        @Test
        void shouldNotDeleteAllByActiveFalseIfAllActive() {
                User user = new User(
                                "username",
                                "latName",
                                "test@test.com",
                                "password",
                                Set.of(Role.USER));

                user.setEnabled(true);
                userRepository.save(user);

                RefreshToken rt = new RefreshToken("token", user, "127.0.0.1",
                                "Mozilla/5.0 (Windows NT 10.0; Win64; x64)", "web", 0);

                refreshTokenRepository.save(rt);

                refreshTokenRepository.deleteAllByActiveFalse();

                List<RefreshToken> found = refreshTokenRepository.findAllByUser_UuidAndActiveTrue(user.getUuid());

                assertFalse(found.isEmpty());
                assertEquals(1, refreshTokenRepository.count());
        }

}
