package com.gastonnicora.trips.repository.refreshToken;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
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
public class DeleteByUserUuidTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void shouldDeleteByUserUuid() {
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
        RefreshToken rt2 = new RefreshToken("token", user, "127.0.0.1",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64)", "web", 0);

        refreshTokenRepository.save(rt);
        refreshTokenRepository.save(rt2);

        refreshTokenRepository.deleteAllByUser_Uuid(user.getUuid());

        List<RefreshToken> found = refreshTokenRepository.findAllByUser_UuidAndActiveTrue(user.getUuid());

        assertTrue(found.isEmpty());

    }

}
