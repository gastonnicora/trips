package com.gastonnicora.trips.repository.company;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FindByUuidTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void shouldFindByUuid() {

        User user = new User(
                "username",
                "latName",
                "test@test.com",
                "password",
                Set.of(Role.USER));

        user.setEnabled(true);

        userRepository.save(user);

        Company company = new Company("Test", user, "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123");

        Company c = companyRepository.save(company);

        Optional<Company> found = companyRepository.findByUuid(c.getUuid());

        assertTrue(found.isPresent());
        assertTrue(found.get().getUuid().equals(company.getUuid()));
        assertTrue(found.get().getEmail().equals("test@mail.com"));
        assertTrue(found.get().getName().equals("Test"));
    }

    @Test
    void shouldFindByUuidIfNotExist() {

        Optional<Company> found = companyRepository.findByUuid(UUID.randomUUID());

        assertFalse(found.isPresent());
    }

    @Test
    void shouldFindById() {

        User user = new User(
                "username",
                "latName",
                "test@test.com",
                "password",
                Set.of(Role.USER));

        user.setEnabled(true);

        userRepository.save(user);

        Company company = new Company("Test", user, "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123");

        Company c = companyRepository.save(company);

        Optional<Company> found = companyRepository.findById(c.getUuid());

        assertTrue(found.isPresent());
        assertTrue(found.get().getUuid().equals(company.getUuid()));
        assertTrue(found.get().getEmail().equals("test@mail.com"));
        assertTrue(found.get().getName().equals("Test"));
    }
}
