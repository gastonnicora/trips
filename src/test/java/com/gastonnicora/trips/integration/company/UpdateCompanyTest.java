package com.gastonnicora.trips.integration.company;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.helpers.company.CompanyApiTestClient;
import com.gastonnicora.trips.helpers.company.CompanyTestFactory;
import com.gastonnicora.trips.helpers.user.UserTestFactory;
import com.gastonnicora.trips.services.GeocodingService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UpdateCompanyTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GeocodingService geocodingService;

    private String token;
    private CompanyApiTestClient companyApi;

    private CompanyDTO company;

    static Stream<Arguments> invalidCompanyFields() {
        return Stream.of(
                // name vacío
                Arguments.of("", "test@test.com", "123456789", 54.4, -34.5, "name"),
                Arguments.of("Goodname", "", "123456789", 54.4, -34.5, "email"),
                // phone vacío
                Arguments.of("Goodname", "test@test.com", "", 54.4, -34.5, "phone"),
                // latitude vacío
                Arguments.of("Goodname", "test@test.com", "123456789", null, -34.5, "latitude"),
                // longitude vacío
                Arguments.of("Goodname", "test@test.com", "123456789", 54.4, null, "longitude"),
                // campos en null
                Arguments.of(null, null, null, null, null, "name"),
                Arguments.of("Goodname", "test", "123456789", 54.4, -34.5, "email"));
    }

    @BeforeEach
    void setup() throws Exception {

        token = UserTestFactory.registerAndLogin(mockMvc);
        this.companyApi = new CompanyApiTestClient(mockMvc, objectMapper).withToken(token);

        this.company = new CompanyTestFactory(mockMvc, objectMapper, token, geocodingService).createCompany();

    }

    @Test
    void shouldReturnBadRequestWhenAddressIsNotFound() throws Exception {
        companyApi.updateCompany(company.getUuid(), "Good Name", "goodemail@mail.com", "+5491122334455", 100.0,
                        -100.0)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @ParameterizedTest
    @MethodSource("invalidCompanyFields")
    void shouldReturnBadRequestWhenCompanyFieldsAreInvalid(
            String name,
            String email,
            String phone,
            Double latitude,
            Double longitude,
            String expectedField) throws Exception {

        companyApi.updateCompany(company.getUuid(), name, email, phone, latitude, longitude)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.%s".formatted(expectedField)).exists())
                .andExpect(jsonPath("$.errors.%s".formatted(expectedField))
                        .value(notNullValue()))
                .andDo(result -> System.out
                        .println("Errors " + result.getResponse().getContentAsString()));
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotOwner() throws Exception {
        String token2 = UserTestFactory.registerAndLogin(mockMvc);
        companyApi.withToken(token2);
        companyApi
                .updateCompany(company.getUuid(), "Good Name", "goodemail@mail.com", "+5491122334455",
                        -34.6037,
                        -58.3816)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnOk() throws Exception {
        companyApi
                .updateCompany(company.getUuid(), "Good Name", "goodemail@mail.com", "+5491122334455",
                        -34.6037,
                        -58.3816)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Good Name"))
                .andExpect(jsonPath("$.email").value("goodemail@mail.com"))
                .andExpect(jsonPath("$.phone").value("+5491122334455"))
                .andExpect(jsonPath("$.latitude").value(-34.6037))
                .andExpect(jsonPath("$.longitude").value(-58.3816))
                .andExpect(jsonPath("$.address").value(notNullValue()));

    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        companyApi.withToken("");
        companyApi
                .updateCompany(company.getUuid(), "Good Name", "goodemail@mail.com", "+5491122334455",
                        -34.6037,
                        -58.3816)
                .andExpect(status().isUnauthorized());
    }
}
