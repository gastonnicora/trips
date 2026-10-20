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
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GetRouteTest {

    private static final String PASSWORD = "goodPassword";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GeocodingService geocodingService;

    private CompanyDTO company;
    private UUID routeUuid;
    private String ownerToken;
    private RouteApiTestClient routeApi;
    private WorkerApiTestClient ownerWorkerApi;

    @BeforeEach
    void setup() throws Exception {

        UserDTO owner = UserTestFactory.registerUser(mockMvc, "owner", PASSWORD);

        LoginResponse ownerLogin = UserTestFactory.login(mockMvc, owner.getEmail(), PASSWORD);
        ownerToken = ownerLogin.getToken();

        company = new CompanyTestFactory(
                mockMvc, objectMapper, ownerToken, geocodingService
        ).createCompany();

        routeUuid = new RouteTestFactory(mockMvc, objectMapper, ownerToken)
                .createRoute(company.getUuid(), "25 de Mayo - La Plata", PricingType.PER_KM)
                .getUuid();

        routeApi = new RouteApiTestClient(mockMvc, objectMapper).withToken(ownerToken);
        ownerWorkerApi = new WorkerApiTestClient(mockMvc, objectMapper).withToken(ownerToken);
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
    void shouldReturnRouteSuccessfully() throws Exception {

        routeApi
                .getRoute(company.getUuid(), routeUuid)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(routeUuid.toString()))
                .andExpect(jsonPath("$.company.uuid").value(company.getUuid().toString()))
                .andExpect(jsonPath("$.name").value("25 de Mayo - La Plata"))
                .andExpect(jsonPath("$.pricingType").value("PER_KM"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.points").isEmpty())
                .andExpect(jsonPath("$.sectionPrices").isEmpty());
    }

    @Test
    void shouldAllowAdminToGetRoute() throws Exception {

        routeApiForWorkerWithRoles("admin", Set.of(RoleCompany.ADMIN))
                .getRoute(company.getUuid(), routeUuid)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(routeUuid.toString()));
    }


    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {

        new RouteApiTestClient(mockMvc, objectMapper)
                .getRoute(company.getUuid(), routeUuid)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnForbiddenWhenUserHasNoAllowedRole() throws Exception {

        routeApiForWorkerWithRoles("seller", Set.of(RoleCompany.SELLER))
                .getRoute(company.getUuid(), routeUuid)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotWorkerOfTheCompany() throws Exception {

        UserDTO stranger = UserTestFactory.registerUser(mockMvc, "stranger", PASSWORD);
        LoginResponse login = UserTestFactory.login(mockMvc, stranger.getEmail(), PASSWORD);

        new RouteApiTestClient(mockMvc, objectMapper)
                .withToken(login.getToken())
                .getRoute(company.getUuid(), routeUuid)
                .andExpect(status().isForbidden());
    }


    @Test
    void shouldReturnForbiddenWhenRouteBelongsToAnotherCompany() throws Exception {

        CompanyDTO anotherCompany = new CompanyTestFactory(
                mockMvc, objectMapper, ownerToken, geocodingService
        ).createCompany();

        routeApi
                .getRoute(anotherCompany.getUuid(), routeUuid)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnNotFoundWhenRouteDoesNotExist() throws Exception {

        routeApi
                .getRoute(company.getUuid(), UUID.randomUUID())
                .andExpect(status().isNotFound());
    }
}