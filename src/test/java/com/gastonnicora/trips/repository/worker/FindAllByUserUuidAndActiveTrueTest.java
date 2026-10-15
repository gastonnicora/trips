package com.gastonnicora.trips.repository.worker;

import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.entities.Worker;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.repositories.WorkerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FindAllByUserUuidAndActiveTrueTest {

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void shouldReturnActiveWorkersWhenUserExists() {
        User user = new User("username", "latName", "email@example.com", "password");
        user.setEnabled(true);
        user = userRepository.save(user);

        Company company = new Company("Company Name", "Company Description", -34.6037, -58.3816,
                "test@company.com", "1234567890");
        company = companyRepository.save(company);

        Worker worker = workerRepository.save(
                new Worker(user, company, Set.of(RoleCompany.ADMIN)));

        List<Worker> found = workerRepository.findAllByUserUuidAndActiveTrue(user.getUuid());

        assertFalse(found.isEmpty());
        assertTrue(found.stream().anyMatch(item -> item.getUuid().equals(worker.getUuid())));
        assertTrue(found.stream().allMatch(Worker::isActive));
    }

    @Test
    void shouldReturnEmptyWhenAllWorkersAreInactive() {
        User user = new User("username", "latName", "email@example.com", "password");
        user.setEnabled(true);
        user = userRepository.save(user);

        Company company = new Company("Company Name", "Company Description", -34.6037, -58.3816,
                "test@company.com", "1234567890");
        company = companyRepository.save(company);

        Worker worker = new Worker(user, company, Set.of(RoleCompany.ADMIN));
        worker.setActive(false);
        workerRepository.save(worker);

        List<Worker> found = workerRepository.findAllByUserUuidAndActiveTrue(user.getUuid());

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenUserHasNoActiveWorkers() {
        User user = new User("username", "latName", "email@example.com", "password");
        user.setEnabled(true);
        user = userRepository.save(user);

        List<Worker> found = workerRepository.findAllByUserUuidAndActiveTrue(user.getUuid());

        assertTrue(found.isEmpty());
    }
}
