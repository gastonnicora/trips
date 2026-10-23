package com.gastonnicora.trips.integration.pricePerKilometer;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.entities.PricePerKilometerDTO;
import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.response.auth.LoginResponse;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.helpers.company.CompanyTestFactory;
import com.gastonnicora.trips.helpers.pricePerKilometer.PricePerKilometerApiTestClient;
import com.gastonnicora.trips.helpers.pricePerKilometer.PricePerKilometerTestFactory;
import com.gastonnicora.trips.helpers.route.RouteApiTestClient;
import com.gastonnicora.trips.helpers.user.UserTestFactory;
import com.gastonnicora.trips.helpers.worker.WorkerApiTestClient;
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
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DeletePricePerKilometerTest {

    private static final String PASSWORD = "goodPassword";
    private static final BigDecimal BASE_KM = new BigDecimal("100.00");
    private static final BigDecimal BASE_PRICE = new BigDecimal("10000.00");
    private static final BigDecimal PRICE_PER_KM = new BigDecimal("500.00");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GeocodingService geocodingService;

    private CompanyDTO company;
    private RouteApiTestClient routeApi;
    private PricePerKilometerApiTestClient pricePerKilometerApi;
    private WorkerApiTestClient ownerWorkerApi;
    private UUID routeUuid;
    private UUID priceUuid;
    private String ownerToken;

    private PricePerKilometerApiTestClient pricePerKilometerApiForWorkerWithRoles(
            String name,
            Set<RoleCompany> roles
                                                                                 ) throws Exception {

        UserDTO user = UserTestFactory.registerUser(mockMvc, name, PASSWORD);

        ownerWorkerApi
                .createWorker(company.getUuid(), user.getUuid(), roles)
                .andExpect(status().isOk());

        LoginResponse login = UserTestFactory.login(mockMvc, user.getEmail(), PASSWORD);

        return new PricePerKilometerApiTestClient(mockMvc, objectMapper).withToken(login.getToken());
    }

    @BeforeEach
    void setup() throws Exception {
        UserDTO owner = UserTestFactory.registerUser(mockMvc, "owner", PASSWORD);

        LoginResponse ownerLogin = UserTestFactory.login(mockMvc, owner.getEmail(), PASSWORD);
        ownerToken = ownerLogin.getToken();

        company = new CompanyTestFactory(mockMvc, objectMapper, ownerToken, geocodingService)
                .createCompany();

        routeApi = new RouteApiTestClient(mockMvc, objectMapper).withToken(ownerToken);

        pricePerKilometerApi = new PricePerKilometerApiTestClient(mockMvc, objectMapper)
                .withToken(ownerToken);

        ownerWorkerApi = new WorkerApiTestClient(mockMvc, objectMapper).withToken(ownerToken);

        MvcResult routeResult = routeApi
                .createRoute(company.getUuid(), "25 de Mayo - La Plata", PricingType.PER_KM)
                .andExpect(status().isOk())
                .andReturn();

        routeUuid = UUID.fromString(
                objectMapper.readTree(routeResult.getResponse().getContentAsString())
                        .get("uuid").asString());

        PricePerKilometerDTO price = new PricePerKilometerTestFactory(mockMvc, objectMapper, ownerToken)
                .createPricePerKilometer(company.getUuid(), routeUuid, BASE_KM, BASE_PRICE, PRICE_PER_KM);

        priceUuid = price.getUuid();
    }

    @Test
    void shouldDeletePricePerKilometerSuccessfully() throws Exception {
        pricePerKilometerApi
                .deletePricePerKilometer(company.getUuid(), routeUuid, priceUuid)
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotFindPricePerKilometerAfterDeleting() throws Exception {
        pricePerKilometerApi
                .deletePricePerKilometer(company.getUuid(), routeUuid, priceUuid)
                .andExpect(status().isOk());

        pricePerKilometerApi
                .getPricePerKilometer(company.getUuid(), routeUuid, priceUuid)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingAlreadyDeletedPricePerKilometer() throws Exception {
        pricePerKilometerApi
                .deletePricePerKilometer(company.getUuid(), routeUuid, priceUuid)
                .andExpect(status().isOk());

        pricePerKilometerApi
                .deletePricePerKilometer(company.getUuid(), routeUuid, priceUuid)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAllowCreatingNewPricePerKilometerAfterDeleting() throws Exception {
        pricePerKilometerApi
                .deletePricePerKilometer(company.getUuid(), routeUuid, priceUuid)
                .andExpect(status().isOk());

        pricePerKilometerApi
                .createPricePerKilometer(company.getUuid(), routeUuid, BASE_KM, BASE_PRICE, PRICE_PER_KM)
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowAdminToDeletePricePerKilometer() throws Exception {
        pricePerKilometerApiForWorkerWithRoles("admin", Set.of(RoleCompany.ADMIN))
                .deletePricePerKilometer(company.getUuid(), routeUuid, priceUuid)
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnForbiddenWhenUserHasNoAllowedRole() throws Exception {
        pricePerKilometerApiForWorkerWithRoles("driver", Set.of(RoleCompany.DRIVER))
                .deletePricePerKilometer(company.getUuid(), routeUuid, priceUuid)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        new PricePerKilometerApiTestClient(mockMvc, objectMapper)
                .deletePricePerKilometer(company.getUuid(), routeUuid, priceUuid)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnNotFoundWhenPricePerKilometerDoesNotExist() throws Exception {
        pricePerKilometerApi
                .deletePricePerKilometer(company.getUuid(), routeUuid, UUID.randomUUID())
                .andExpect(status().isNotFound());
    }
}