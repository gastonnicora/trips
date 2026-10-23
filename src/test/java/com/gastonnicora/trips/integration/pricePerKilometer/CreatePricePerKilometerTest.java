package com.gastonnicora.trips.integration.pricePerKilometer;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.response.auth.LoginResponse;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.helpers.company.CompanyTestFactory;
import com.gastonnicora.trips.helpers.pricePerKilometer.PricePerKilometerApiTestClient;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CreatePricePerKilometerTest {

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
    private String ownerToken;

    private PricePerKilometerApiTestClient pricePerKilometerApiForWorkerWithRoles(
            String name,
            Set<RoleCompany> roles
                                                                                 ) throws Exception {

        UserDTO user = UserTestFactory.registerUser(
                mockMvc, name, PASSWORD
                                                   );

        ownerWorkerApi
                .createWorker(company.getUuid(), user.getUuid(), roles)
                .andExpect(status().isOk());

        LoginResponse login = UserTestFactory.login(
                mockMvc, user.getEmail(), PASSWORD
                                                   );

        return new PricePerKilometerApiTestClient(
                mockMvc, objectMapper
        ).withToken(login.getToken());
    }

    @BeforeEach
    void setup() throws Exception {
        UserDTO owner = UserTestFactory.registerUser(
                mockMvc, "owner", PASSWORD
                                                    );

        LoginResponse ownerLogin = UserTestFactory.login(
                mockMvc, owner.getEmail(), PASSWORD
                                                        );

        company = new CompanyTestFactory(
                mockMvc,
                objectMapper,
                ownerLogin.getToken(),
                geocodingService
        ).createCompany();

        routeApi = new RouteApiTestClient(
                mockMvc, objectMapper
        ).withToken(ownerLogin.getToken());

        pricePerKilometerApi = new PricePerKilometerApiTestClient(
                mockMvc, objectMapper
        ).withToken(ownerLogin.getToken());

        ownerWorkerApi = new WorkerApiTestClient(
                mockMvc, objectMapper
        ).withToken(ownerLogin.getToken());

        MvcResult result = routeApi
                .createRoute(
                        company.getUuid(),
                        "25 de Mayo - La Plata",
                        PricingType.PER_KM
                            )
                .andExpect(status().isOk())
                .andReturn();

        routeUuid = UUID.fromString(
                objectMapper.readTree(
                        result.getResponse().getContentAsString()
                                     ).get("uuid").asString()
                                   );
        ownerToken = ownerLogin.getToken();
    }

    @Test
    void shouldAllowAdminToCreatePricePerKilometer() throws Exception {
        pricePerKilometerApiForWorkerWithRoles("admin", Set.of(RoleCompany.ADMIN))
                .createPricePerKilometer(company.getUuid(), routeUuid, BASE_KM, BASE_PRICE, PRICE_PER_KM)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").exists());
    }

    @Test
    void shouldCreatePricePerKilometerSuccessfully() throws Exception {
        pricePerKilometerApi
                .createPricePerKilometer(company.getUuid(), routeUuid, BASE_KM, BASE_PRICE, PRICE_PER_KM)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").exists())
                .andExpect(jsonPath("$.baseKm").value(100.0))
                .andExpect(jsonPath("$.basePrice").value(10000.0))
                .andExpect(jsonPath("$.pricePerKm").value(500.0))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldReturnBadRequestWhenBaseKmIsNull() throws Exception {
        pricePerKilometerApi
                .createPricePerKilometer(company.getUuid(), routeUuid, null, BASE_PRICE, PRICE_PER_KM)
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenBasePriceIsNegative() throws Exception {
        pricePerKilometerApi
                .createPricePerKilometer(
                        company.getUuid(), routeUuid, BASE_KM,
                        new BigDecimal("-1.00"), PRICE_PER_KM
                                        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenBodyIsMalformed() throws Exception {
        pricePerKilometerApi
                .createPricePerKilometerRaw(company.getUuid(), routeUuid, "{ esto no es json")
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenPricePerKmIsNull() throws Exception {
        pricePerKilometerApi
                .createPricePerKilometer(company.getUuid(), routeUuid, BASE_KM, BASE_PRICE, null)
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnConflictWhenRouteAlreadyHasPricePerKilometer() throws Exception {
        pricePerKilometerApi
                .createPricePerKilometer(company.getUuid(), routeUuid, BASE_KM, BASE_PRICE, PRICE_PER_KM)
                .andExpect(status().isOk());

        pricePerKilometerApi
                .createPricePerKilometer(company.getUuid(), routeUuid, BASE_KM, BASE_PRICE, PRICE_PER_KM)
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnForbiddenWhenUserHasNoAllowedRole() throws Exception {
        pricePerKilometerApiForWorkerWithRoles("driver", Set.of(RoleCompany.DRIVER))
                .createPricePerKilometer(company.getUuid(), routeUuid, BASE_KM, BASE_PRICE, PRICE_PER_KM)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotWorkerOfTheCompany() throws Exception {
        String strangerToken = UserTestFactory.registerAndLogin(mockMvc);

        new PricePerKilometerApiTestClient(mockMvc, objectMapper)
                .withToken(strangerToken)
                .createPricePerKilometer(company.getUuid(), routeUuid, BASE_KM, BASE_PRICE, PRICE_PER_KM)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnNotFoundWhenRouteBelongsToAnotherCompany() throws Exception {
        CompanyDTO anotherCompany = new CompanyTestFactory(
                mockMvc, objectMapper, ownerToken, geocodingService
        ).createCompany();

        MvcResult result = routeApi
                .createRoute(anotherCompany.getUuid(), "Ruta ajena", PricingType.PER_KM)
                .andExpect(status().isOk())
                .andReturn();

        UUID anotherRouteUuid = UUID.fromString(
                objectMapper.readTree(result.getResponse().getContentAsString())
                        .get("uuid").asString());

        // se usa la empresa propia en la URL con la ruta de otra empresa
        pricePerKilometerApi
                .createPricePerKilometer(company.getUuid(), anotherRouteUuid, BASE_KM, BASE_PRICE, PRICE_PER_KM)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenRouteDoesNotExist() throws Exception {
        pricePerKilometerApi
                .createPricePerKilometer(company.getUuid(), UUID.randomUUID(), BASE_KM, BASE_PRICE, PRICE_PER_KM)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenRouteIsDeleted() throws Exception {
        routeApi
                .deleteRoute(company.getUuid(), routeUuid)
                .andExpect(status().isOk());

        pricePerKilometerApi
                .createPricePerKilometer(company.getUuid(), routeUuid, BASE_KM, BASE_PRICE, PRICE_PER_KM)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        new PricePerKilometerApiTestClient(mockMvc, objectMapper)
                .createPricePerKilometer(company.getUuid(), routeUuid, BASE_KM, BASE_PRICE, PRICE_PER_KM)
                .andExpect(status().isUnauthorized());
    }
}