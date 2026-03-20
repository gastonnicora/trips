package com.gastonnicora.trips.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.gastonnicora.trips.entitys.User;
import com.gastonnicora.trips.enums.Role;
import com.gastonnicora.trips.repositories.UserRepository;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindByUsername() {

        User user = new User(null,"test","test","test","test",Set.of(Role.USER),true,null,null);
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmailAndEnabled("test",true);

        assertTrue(found.isPresent());
    }
}
