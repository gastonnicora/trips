package com.gastonnicora.trips.repository.company;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
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
public class FindAllByOwner_UuidTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void shouldFindAllByOwner_Uuid() {

        User user = new User(
                "username",
                "latName",
                "test@test.com",
                "password",
                Set.of(Role.USER));

        user.setEnabled(true);

        user = userRepository.save(user);

        Company company = new Company("Test", user, "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123");
        Company company2 = new Company("Test2", user, "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123");
        companyRepository.save(company);
        companyRepository.save(company2);

        List<Company> companies = companyRepository.findAllByOwner_Uuid(user.getUuid());

        assertTrue(companies.size() == 2);

        assertTrue(companies.get(0).getEmail().equals("test@mail.com"));
        assertTrue(companies.get(0).getName().equals("Test"));

        assertTrue(companies.get(1).getEmail().equals("test@mail.com"));
        assertTrue(companies.get(1).getName().equals("Test2"));
    }

    @Test
    void shouldFindAllByOwnerUuidIfNotExist() {

        List<Company> companies = companyRepository.findAllByOwner_Uuid(UUID.randomUUID());

        assertTrue(companies.isEmpty());
    }

}
