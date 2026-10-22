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

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FindByUserUuidAndCompanyUuidAndActiveTrueAndUserEnabledTrueAndCompanyActiveTrueTest {

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void shouldReturnActiveWorkerWhenUserAndCompanyMatch() {
        User user = new User("username", "latName", "email@example.com", "password");
        user.setEnabled(true);
        user = userRepository.save(user);

        Company company = new Company("Company Name", "Company Description", -34.6037, -58.3816,
                "test@company.com", "1234567890");
        company = companyRepository.save(company);

        Worker worker = workerRepository.save(
                new Worker(user, company, Set.of(RoleCompany.ADMIN)));

        Optional<Worker> found = workerRepository.findByUserUuidAndCompanyUuidAndActiveTrueAndUserEnabledTrueAndCompanyActiveTrue(
                user.getUuid(), company.getUuid());

        assertTrue(found.isPresent());
        assertEquals(found.get().getUuid(), worker.getUuid());
        assertEquals(found.get().getUser().getUuid(), user.getUuid());
        assertEquals(found.get().getCompany().getUuid(), company.getUuid());
        assertTrue(found.get().isActive());
    }

    @Test
    void shouldReturnEmptyWhenWorkerDoesNotExist() {
        Optional<Worker> found = workerRepository.findByUserUuidAndCompanyUuidAndActiveTrueAndUserEnabledTrueAndCompanyActiveTrue(
                UUID.randomUUID(), UUID.randomUUID());

        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenWorkerIsInactive() {
        User user = new User("username", "latName", "email@example.com", "password");
        user.setEnabled(true);
        user = userRepository.save(user);

        Company company = new Company("Company Name", "Company Description", -34.6037, -58.3816,
                "test@company.com", "1234567890");
        company = companyRepository.save(company);

        Worker worker = new Worker(user, company, Set.of(RoleCompany.ADMIN));
        worker.setActive(false);
        workerRepository.save(worker);

        Optional<Worker> found = workerRepository.findByUserUuidAndCompanyUuidAndActiveTrueAndUserEnabledTrueAndCompanyActiveTrue(
                user.getUuid(), company.getUuid());

        assertFalse(found.isPresent());
    }
}
