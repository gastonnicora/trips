package com.gastonnicora.trips.repository.company;

import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.repositories.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FindAllByActiveTrueTest {

    @Autowired
    private CompanyRepository companyRepository;

    private Company newCompany(String name) {
        return new Company(
                name, "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123"
        );
    }

    @Test
    void shouldNotReturnInactiveCompanies() {
        Company active = companyRepository.save(newCompany("Active"));

        Company inactive = newCompany("Inactive");
        inactive.setActive(false);
        inactive = companyRepository.save(inactive);

        List<UUID> found = uuidsOf(companyRepository.findAllByActiveTrue());

        assertTrue(found.contains(active.getUuid()));
        assertFalse(found.contains(inactive.getUuid()));
    }

    @Test
    void shouldReturnActiveCompanies() {
        Company company1 = companyRepository.save(newCompany("Test 1"));
        Company company2 = companyRepository.save(newCompany("Test 2"));

        List<UUID> found = uuidsOf(companyRepository.findAllByActiveTrue());

        assertTrue(found.contains(company1.getUuid()));
        assertTrue(found.contains(company2.getUuid()));
    }

    @Test
    void shouldReturnOnlyActiveCompanies() {
        Company inactive = newCompany("Inactive");
        inactive.setActive(false);
        companyRepository.save(inactive);
        companyRepository.save(newCompany("Active"));

        List<Company> found = companyRepository.findAllByActiveTrue();

        assertFalse(found.isEmpty());
        assertTrue(found.stream().allMatch(Company::isActive));
    }

    private List<UUID> uuidsOf(List<Company> companies) {
        return companies.stream().map(Company::getUuid).toList();
    }
}