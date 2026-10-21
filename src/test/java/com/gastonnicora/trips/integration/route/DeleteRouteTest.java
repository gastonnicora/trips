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
class DeleteRouteTest {

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

    @BeforeEach
    void setup() throws Exception {

        UserDTO owner = UserTestFactory.registerUser(mockMvc, "owner", PASSWORD);
        ownerToken = UserTestFactory.login(mockMvc, owner.getEmail(), PASSWORD).getToken();

        company = new CompanyTestFactory(
                mockMvc, objectMapper, ownerToken, geocodingService
        ).createCompany();

        routeUuid = new RouteTestFactory(mockMvc, objectMapper, ownerToken)
                .createRoute(company.getUuid(), "25 de Mayo - La Plata", PricingType.PER_KM)
                .getUuid();

        routeApi = new RouteApiTestClient(mockMvc, objectMapper).withToken(ownerToken);
    }

    private RouteApiTestClient routeApiForWorkerWithRoles(String name, Set<RoleCompany> roles) throws Exception {
        UserDTO user = UserTestFactory.registerUser(mockMvc, name, PASSWORD);

        new WorkerApiTestClient(mockMvc, objectMapper)
                .withToken(ownerToken)
                .createWorker(company.getUuid(), user.getUuid(), roles)
                .andExpect(status().isOk());

        LoginResponse login = UserTestFactory.login(mockMvc, user.getEmail(), PASSWORD);

        return new RouteApiTestClient(mockMvc, objectMapper).withToken(login.getToken());
    }

    // ---------- casos felices ----------

    @Test
    void shouldDeleteRouteSuccessfully() throws Exception {

        routeApi
                .deleteRoute(company.getUuid(), routeUuid)
                .andExpect(status().isOk());

        routeApi
                .getRoute(company.getUuid(), routeUuid)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAllowAdminToDeleteRoute() throws Exception {

        routeApiForWorkerWithRoles("admin", Set.of(RoleCompany.ADMIN))
                .deleteRoute(company.getUuid(), routeUuid)
                .andExpect(status().isOk());

        routeApi
                .getRoute(company.getUuid(), routeUuid)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotListDeletedRoute() throws Exception {

        UUID otherRouteUuid = new RouteTestFactory(mockMvc, objectMapper, ownerToken)
                .createRoute(company.getUuid(), "La Plata - Berisso", PricingType.PER_SECTION)
                .getUuid();

        routeApi
                .deleteRoute(company.getUuid(), routeUuid)
                .andExpect(status().isOk());

        routeApi
                .getRoutes(company.getUuid())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].uuid").value(otherRouteUuid.toString()));
    }

    // ---------- permisos ----------

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {

        new RouteApiTestClient(mockMvc, objectMapper)
                .deleteRoute(company.getUuid(), routeUuid)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnForbiddenWhenUserHasNoAllowedRole() throws Exception {

        routeApiForWorkerWithRoles("seller", Set.of(RoleCompany.SELLER))
                .deleteRoute(company.getUuid(), routeUuid)
                .andExpect(status().isForbidden());

        routeApi
                .getRoute(company.getUuid(), routeUuid)
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotWorkerOfTheCompany() throws Exception {

        UserDTO stranger = UserTestFactory.registerUser(mockMvc, "stranger", PASSWORD);
        LoginResponse login = UserTestFactory.login(mockMvc, stranger.getEmail(), PASSWORD);

        new RouteApiTestClient(mockMvc, objectMapper)
                .withToken(login.getToken())
                .deleteRoute(company.getUuid(), routeUuid)
                .andExpect(status().isForbidden());

        routeApi
                .getRoute(company.getUuid(), routeUuid)
                .andExpect(status().isOk());
    }

    // ---------- errores de negocio ----------

    @Test
    void shouldReturnNotFoundWhenRouteDoesNotExist() throws Exception {

        routeApi
                .deleteRoute(company.getUuid(), UUID.randomUUID())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenRouteIsAlreadyDeleted() throws Exception {

        routeApi
                .deleteRoute(company.getUuid(), routeUuid)
                .andExpect(status().isOk());

        routeApi
                .deleteRoute(company.getUuid(), routeUuid)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenRouteBelongsToAnotherCompany() throws Exception {

        CompanyDTO anotherCompany = new CompanyTestFactory(
                mockMvc, objectMapper, ownerToken, geocodingService
        ).createCompany();

        routeApi
                .deleteRoute(anotherCompany.getUuid(), routeUuid)
                .andExpect(status().isNotFound());

        // la ruta original no debe haberse eliminado
        routeApi
                .getRoute(company.getUuid(), routeUuid)
                .andExpect(status().isOk());
    }
}