package com.gastonnicora.trips.repository.company;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.enums.Role;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.repositories.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FindByUuidTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void shouldReturnEntityWhenUuidExists() {

        User user = new User(
                "username",
                "latName",
                "test@test.com",
                "password",
                Set.of(Role.USER));

        user.setEnabled(true);

        userRepository.save(user);

        Company company = new Company("Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123");

        Company c = companyRepository.save(company);

        Optional<Company> found = companyRepository.findByUuidAndActiveTrue(c.getUuid());

        assertTrue(found.isPresent());
        assertEquals(found.get().getUuid(), company.getUuid());
        assertEquals("test@mail.com", found.get().getEmail());
        assertEquals("Test", found.get().getName());
    }

    @Test
    void shouldReturnEmptyWhenUuidDoesNotExist() {

        Optional<Company> found = companyRepository.findByUuidAndActiveTrue(UUID.randomUUID());

        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnEntityWhenIdExists() {

        User user = new User(
                "username",
                "latName",
                "test@test.com",
                "password",
                Set.of(Role.USER));

        user.setEnabled(true);

        userRepository.save(user);

        Company company = new Company("Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123");

        Company c = companyRepository.save(company);

        Optional<Company> found = companyRepository.findById(c.getUuid());

        assertTrue(found.isPresent());
        assertEquals(found.get().getUuid(), company.getUuid());
        assertEquals("test@mail.com", found.get().getEmail());
        assertEquals("Test", found.get().getName());
    }
}
