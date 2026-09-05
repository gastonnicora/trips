package com.gastonnicora.trips.repository.worker;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.entities.Worker;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.repositories.WorkerRepository;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FindAllByUserUuidTest {

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void shouldFindAllByUserUuid() {
        User user = new User(
                "username",
                "latName",
                "email@example.com",
                "password");
        user.setEnabled(true);
        userRepository.save(user);
        Company company = new Company(
                "Company Name",
                "Company Description",
                -34.6037,
                -58.3816,
                "test@company.com",
                "1234567890");
        company = companyRepository.save(company);
        workerRepository.save(new com.gastonnicora.trips.entities.Worker(
                user,
                company,
                Set.of(RoleCompany.ADMIN)));

        List<Worker> found = workerRepository.findAllByUserUuid(user.getUuid());
        assertFalse(found.isEmpty());
        assertTrue(found.stream().anyMatch(worker -> worker.getUser().getUuid().equals(user.getUuid())));
    }

    @Test
    void shouldFindAllByUserUuidWhenNoWorkers() {
        User user = new User(
                "username",
                "latName",
                "email@example.com",
                "password");
        user.setEnabled(true);
        userRepository.save(user);

        List<Worker> found = workerRepository.findAllByUserUuid(user.getUuid());
        assertTrue(found.isEmpty());
    }
}
