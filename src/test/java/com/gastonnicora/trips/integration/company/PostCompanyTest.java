package com.gastonnicora.trips.integration.company;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.notNullValue;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.helpers.CompanyApiTestClient;
import com.gastonnicora.trips.helpers.UserTestFactory;

import jakarta.transaction.Transactional;
import tools.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PostCompanyTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private CompanyApiTestClient companyApi;

    private String email;
    private final String password = "goodPassword";

    @BeforeEach
    void setup() throws Exception {
        UserDTO user = UserTestFactory.registerUser(mockMvc, "User", password);
        this.email = user.getEmail();
        token = UserTestFactory.login(mockMvc, email, password).getToken();
        this.companyApi = new CompanyApiTestClient(mockMvc, objectMapper).withToken(token);

    }

    @Test
    void shouldReturnOk() throws Exception {
        companyApi.createCompany("Good Name", "goodemail@mail.com", "+5491122334455", -34.6037, -58.3816)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Good Name"))
                .andExpect(jsonPath("$.email").value("goodemail@mail.com"))
                .andExpect(jsonPath("$.phone").value("+5491122334455"))
                .andExpect(jsonPath("$.latitude").value(-34.6037))
                .andExpect(jsonPath("$.longitude").value(-58.3816))
                .andExpect(jsonPath("$.owner.email").value(email))
                .andExpect(jsonPath("$.address").value(notNullValue()));

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

        companyApi.createCompany(name, email, phone, latitude, longitude)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.%s".formatted(expectedField)).exists())
                .andExpect(jsonPath("$.errors.%s".formatted(expectedField))
                        .value(notNullValue()))
                .andDo(result -> System.out.println("Errors "+result.getResponse().getContentAsString()));
    }

    static Stream<Arguments> invalidCompanyFields() {
        return Stream.of(
                // name vacío
                Arguments.of("", "test@test.com", "123456789", 54.4, -34.5, "name"),
                // email vacío
                Arguments.of("Goodname", "", "123456789", 54.4, -34.5, "email"),
                // phone vacío
                Arguments.of("Goodname", "test@test.com", "", 54.4, -34.5, "phone"),
                // latitude vacío
                Arguments.of("Goodname", "test@test.com", "123456789", null, -34.5, "latitude"),
                // longitude vacío
                Arguments.of("Goodname", "test@test.com", "123456789", 54.4, null, "longitude"),
                // campos en null
                Arguments.of(null, null, null, null, null, "name"),
                // email invalido
                Arguments.of("Goodname", "test", "123456789", 54.4, -34.5, "email"));
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        companyApi.withToken("");
        companyApi.createCompany("Good Name", "goodemail@mail.com", "+5491122334455", -34.6037, -58.3816)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnBadRequest_whenAddressNotFound() throws Exception {
        companyApi.createCompany("Good Name", "goodemail@mail.com", "+5491122334455", 100.0, -100.0)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

}
