package com.gastonnicora.trips.integration.route;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.response.auth.LoginResponse;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.helpers.company.CompanyTestFactory;
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
import tools.jackson.databind.ObjectMapper;

import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CreateRouteTest {

    private static final String PASSWORD = "goodPassword";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GeocodingService geocodingService;

    private UserDTO owner;
    private CompanyDTO company;
    private RouteApiTestClient routeApi;
    private WorkerApiTestClient ownerWorkerApi;

    @BeforeEach
    void setup() throws Exception {

        owner = UserTestFactory.registerUser(mockMvc, "owner", PASSWORD);

        LoginResponse ownerLogin = UserTestFactory.login(mockMvc, owner.getEmail(), PASSWORD);

        company = new CompanyTestFactory(
                mockMvc, objectMapper, ownerLogin.getToken(), geocodingService
        ).createCompany();

        routeApi = new RouteApiTestClient(mockMvc, objectMapper).withToken(ownerLogin.getToken());
        ownerWorkerApi = new WorkerApiTestClient(mockMvc, objectMapper).withToken(ownerLogin.getToken());
    }

    /**
     * Crea un usuario, lo agrega a la empresa con los roles indicados y
     * devuelve un client de rutas autenticado con ese usuario.
     */
    private RouteApiTestClient routeApiForWorkerWithRoles(String name, Set<RoleCompany> roles) throws Exception {
        UserDTO user = UserTestFactory.registerUser(mockMvc, name, PASSWORD);

        ownerWorkerApi
                .createWorker(company.getUuid(), user.getUuid(), roles)
                .andExpect(status().isOk());

        LoginResponse login = UserTestFactory.login(mockMvc, user.getEmail(), PASSWORD);

        return new RouteApiTestClient(mockMvc, objectMapper).withToken(login.getToken());
    }


    @Test
    void shouldCreateRouteWithPerKmPricingSuccessfully() throws Exception {

        routeApi
                .createRoute(company.getUuid(), "25 de Mayo - La Plata", PricingType.PER_KM)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").exists())
                .andExpect(jsonPath("$.company.uuid").value(company.getUuid().toString()))
                .andExpect(jsonPath("$.name").value("25 de Mayo - La Plata"))
                .andExpect(jsonPath("$.pricingType").value("PER_KM"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.points").isEmpty())
                .andExpect(jsonPath("$.pricePerKilometer").doesNotExist())
                .andExpect(jsonPath("$.sectionPrices").isEmpty());
    }

    @Test
    void shouldCreateRouteWithPerSectionPricingSuccessfully() throws Exception {

        routeApi
                .createRoute(company.getUuid(), "La Plata - Buenos Aires", PricingType.PER_SECTION)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").exists())
                .andExpect(jsonPath("$.name").value("La Plata - Buenos Aires"))
                .andExpect(jsonPath("$.pricingType").value("PER_SECTION"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldAllowAdminToCreateRoute() throws Exception {

        routeApiForWorkerWithRoles("admin", Set.of(RoleCompany.ADMIN))
                .createRoute(company.getUuid(), "Ruta admin", PricingType.PER_KM)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ruta admin"));
    }

    @Test
    void shouldAllowCreatingTwoRoutesWithTheSameName() throws Exception {

        routeApi.createRoute(company.getUuid(), "Ruta repetida", PricingType.PER_KM)
                .andExpect(status().isOk());

        routeApi.createRoute(company.getUuid(), "Ruta repetida", PricingType.PER_KM)
                .andExpect(status().isOk());
    }


    @Test
    void shouldReturnBadRequestWhenNameIsBlank() throws Exception {

        routeApi
                .createRoute(company.getUuid(), "", PricingType.PER_KM)
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenNameIsNull() throws Exception {

        routeApi
                .createRoute(company.getUuid(), null, PricingType.PER_KM)
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenNameIsTooLong() throws Exception {

        routeApi
                .createRoute(company.getUuid(), "a".repeat(256), PricingType.PER_KM)
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateRouteWhenNameHasMaxLength() throws Exception {

        routeApi
                .createRoute(company.getUuid(), "a".repeat(255), PricingType.PER_KM)
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequestWhenPricingTypeIsNull() throws Exception {

        routeApi
                .createRoute(company.getUuid(), "Ruta", null)
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenPricingTypeIsInvalid() throws Exception {

        routeApi
                .createRouteRaw(company.getUuid(), """
                        {"name": "Ruta", "pricingType": "NOT_A_TYPE"}
                        """)
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenBodyIsMalformed() throws Exception {

        routeApi
                .createRouteRaw(company.getUuid(), "{ esto no es json")
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldReturnForbiddenWhenUserHasNoAllowedRole() throws Exception {

        routeApiForWorkerWithRoles("driver", Set.of(RoleCompany.DRIVER))
                .createRoute(company.getUuid(), "Ruta driver", PricingType.PER_KM)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotWorkerOfTheCompany() throws Exception {

        String strangerToken = UserTestFactory.registerAndLogin(mockMvc);

        new RouteApiTestClient(mockMvc, objectMapper)
                .withToken(strangerToken)
                .createRoute(company.getUuid(), "Ruta ajena", PricingType.PER_KM)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenCompanyDoesNotExist() throws Exception {

        routeApi
                .createRoute(UUID.randomUUID(), "Ruta", PricingType.PER_KM)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {

        new RouteApiTestClient(mockMvc, objectMapper)
                .createRoute(company.getUuid(), "Ruta", PricingType.PER_KM)
                .andExpect(status().isUnauthorized());
    }
}
