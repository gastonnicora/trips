/* package com.gastonnicora.trips.repository.worker;

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
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.repositories.WorkerRepository;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FindByUserUuidAndCompanyUuidTest {
    @Autowired
    private WorkerRepository workerRepository;

    @Test
    void shouldFindByUserUuidAndCompanyUuid() {

        User user = new User(
                "username",
                "latName",
                "email@example.com",
                "password"
        );
        Company company = new Company(
                "Company Name",
                "Company Description",
                -34.6037,
                -58.3816,
                "test@example.com",
                "1234567890"
        );
        workerRepository.save(new com.gastonnicora.trips.entities.Worker(
                user,
                company,
                Set.of(RoleCompany.ADMIN)
        ));
    }
}
 */