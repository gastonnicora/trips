package com.gastonnicora.trips.repository.worker;

import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
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
class FindByUserUuidAndCompanyUuidAndUserEnabledTrueAndCompanyActiveTrueTest {

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WorkerRepository workerRepository;

    @Test
    void shouldReturnEmptyWhenWorkerDoesNotExist() {
        Optional<com.gastonnicora.trips.entities.Worker> found = workerRepository
                .findByUserUuidAndCompanyUuidAndUserEnabledTrueAndCompanyActiveTrue(UUID.randomUUID(), UUID.randomUUID());
        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnWorkerWhenUserAndCompanyMatch() {

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
                "test@example.com",
                "1234567890");
        companyRepository.save(company);

        workerRepository.save(new com.gastonnicora.trips.entities.Worker(
                user,
                company,
                Set.of(RoleCompany.ADMIN)));

        Optional<com.gastonnicora.trips.entities.Worker> found = workerRepository
                .findByUserUuidAndCompanyUuidAndUserEnabledTrueAndCompanyActiveTrue(user.getUuid(), company.getUuid());
        assertTrue(found.isPresent());
        assertEquals(found.get().getUser().getUuid(), user.getUuid());
        assertEquals(found.get().getCompany().getUuid(), company.getUuid());
        assertTrue(found.get().getRoles().contains(RoleCompany.ADMIN));
    }
}
