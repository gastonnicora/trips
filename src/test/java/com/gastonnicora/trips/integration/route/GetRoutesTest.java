package com.gastonnicora.trips.integration.route;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.response.auth.LoginResponse;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.helpers.CompanyTestFactory;
import com.gastonnicora.trips.helpers.RouteApiTestClient;
import com.gastonnicora.trips.helpers.RouteTestFactory;
import com.gastonnicora.trips.helpers.UserTestFactory;
import com.gastonnicora.trips.helpers.WorkerApiTestClient;
import com.gastonnicora.trips.services.GeocodingService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GetRoutesTest {

    private static final String PASSWORD = "goodPassword";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GeocodingService geocodingService;

    private CompanyDTO company;
    private String ownerToken;
    private RouteApiTestClient routeApi;

    @BeforeEach
    void setup() throws Exception {

        UserDTO owner = UserTestFactory.registerUser(mockMvc, "owner", PASSWORD);
        ownerToken = UserTestFactory.login(mockMvc, owner.getEmail(), PASSWORD).getToken();

        company = new CompanyTestFactory(
                mockMvc, objectMapper, ownerToken, geocodingService
        ).createCompany();

        new RouteTestFactory(mockMvc, objectMapper, ownerToken)
                .createRoute(company.getUuid(), "25 de Mayo - La Plata", PricingType.PER_KM);

        routeApi = new RouteApiTestClient(mockMvc, objectMapper).withToken(ownerToken);
    }

    @Test
    void shouldReturnRoutesSuccessfully() throws Exception {

        new RouteTestFactory(mockMvc, objectMapper, ownerToken)
                .createRoute(company.getUuid(), "La Plata - Berisso", PricingType.PER_SECTION);

        routeApi
                .getRoutes(company.getUuid())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].name").value(
                        org.hamcrest.Matchers.containsInAnyOrder("25 de Mayo - La Plata", "La Plata - Berisso")));
    }

    @Test
    void shouldReturnNotFoundWhenCompanyHasNoRoutes() throws Exception {

        CompanyDTO company2 = new CompanyTestFactory(
                mockMvc, objectMapper, ownerToken, geocodingService
        ).createCompany();

        routeApi
                .getRoutes(company2.getUuid())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {

        new RouteApiTestClient(mockMvc, objectMapper)
                .getRoutes(company.getUuid())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnForbiddenWhenUserHasNoAllowedRole() throws Exception {

        UserDTO seller = UserTestFactory.registerUser(mockMvc, "seller", PASSWORD);

        new WorkerApiTestClient(mockMvc, objectMapper)
                .withToken(ownerToken)
                .createWorker(company.getUuid(), seller.getUuid(), Set.of(RoleCompany.SELLER))
                .andExpect(status().isOk());

        LoginResponse login = UserTestFactory.login(mockMvc, seller.getEmail(), PASSWORD);

        new RouteApiTestClient(mockMvc, objectMapper)
                .withToken(login.getToken())
                .getRoutes(company.getUuid())
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotWorkerOfTheCompany() throws Exception {

        UserDTO stranger = UserTestFactory.registerUser(mockMvc, "stranger", PASSWORD);
        LoginResponse login = UserTestFactory.login(mockMvc, stranger.getEmail(), PASSWORD);

        new RouteApiTestClient(mockMvc, objectMapper)
                .withToken(login.getToken())
                .getRoutes(company.getUuid())
                .andExpect(status().isForbidden());
    }
}