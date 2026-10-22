package com.gastonnicora.trips.integration.routePoint;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.response.auth.LoginResponse;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.helpers.company.CompanyTestFactory;
import com.gastonnicora.trips.helpers.route.RouteApiTestClient;
import com.gastonnicora.trips.helpers.routePoint.RoutePointApiTestClient;
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

import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DeleteRoutePointTest {

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
    private RoutePointApiTestClient routePointApi;
    private WorkerApiTestClient ownerWorkerApi;
    private UUID routeUuid;
    private UUID pointAUuid;
    private UUID pointBUuid;

    private RoutePointApiTestClient routePointApiForWorkerWithRoles(
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

        return new RoutePointApiTestClient(
                mockMvc, objectMapper
        ).withToken(login.getToken());
    }

    private UUID createRouteAndGetUuid(UUID companyUuid, String name) throws Exception {
        MvcResult result = routeApi
                .createRoute(companyUuid, name, PricingType.PER_KM)
                .andExpect(status().isOk())
                .andReturn();

        return UUID.fromString(
                objectMapper.readTree(
                        result.getResponse().getContentAsString()
                                     ).get("uuid").asString()
                              );
    }

    private UUID createRoutePointAndGetUuid(UUID companyUuid, UUID targetRouteUuid, String name) throws Exception {
        MvcResult result = routePointApi
                .createRoutePoint(
                        companyUuid,
                        targetRouteUuid,
                        name,
                        -34.9214,
                        -57.9545
                                 )
                .andExpect(status().isOk())
                .andReturn();

        return UUID.fromString(
                objectMapper.readTree(
                        result.getResponse().getContentAsString()
                                     ).get("uuid").asString()
                              );
    }

    @BeforeEach
    void setup() throws Exception {
        UserDTO owner = UserTestFactory.registerUser(
                mockMvc, "owner", PASSWORD
                                                    );

        LoginResponse ownerLogin = UserTestFactory.login(
                mockMvc, owner.getEmail(), PASSWORD
                                                        );

        ownerToken = ownerLogin.getToken();

        company = new CompanyTestFactory(
                mockMvc,
                objectMapper,
                ownerToken,
                geocodingService
        ).createCompany();

        routeApi = new RouteApiTestClient(
                mockMvc, objectMapper
        ).withToken(ownerToken);

        routePointApi = new RoutePointApiTestClient(
                mockMvc, objectMapper
        ).withToken(ownerToken);

        ownerWorkerApi = new WorkerApiTestClient(
                mockMvc, objectMapper
        ).withToken(ownerToken);

        routeUuid = createRouteAndGetUuid(company.getUuid(), "25 de Mayo - La Plata");

        pointAUuid = createRoutePointAndGetUuid(company.getUuid(), routeUuid, "Punto A");
        pointBUuid = createRoutePointAndGetUuid(company.getUuid(), routeUuid, "Punto B");
    }

    @Test
    void shouldAllowAdminToDeleteRoutePoint() throws Exception {
        routePointApiForWorkerWithRoles(
                "admin", Set.of(RoleCompany.ADMIN)
                                       )
                .deleteRoutePoint(company.getUuid(), routeUuid, pointAUuid)
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteRoutePointSuccessfully() throws Exception {
        routePointApi
                .deleteRoutePoint(company.getUuid(), routeUuid, pointAUuid)
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotReturnDeletedRoutePoint() throws Exception {
        routePointApi
                .deleteRoutePoint(company.getUuid(), routeUuid, pointAUuid)
                .andExpect(status().isOk());

        routePointApi
                .getRoutePoint(company.getUuid(), routeUuid, pointAUuid)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotListDeletedRoutePoint() throws Exception {
        routePointApi
                .deleteRoutePoint(company.getUuid(), routeUuid, pointAUuid)
                .andExpect(status().isOk());

        routePointApi
                .getRoutePoints(company.getUuid(), routeUuid)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].uuid").value(pointBUuid.toString()));
    }

    // ---------- errores de negocio ----------

    @Test
    void shouldReturnNotFoundWhenRoutePointIsAlreadyDeleted() throws Exception {
        routePointApi
                .deleteRoutePoint(company.getUuid(), routeUuid, pointAUuid)
                .andExpect(status().isOk());

        routePointApi
                .deleteRoutePoint(company.getUuid(), routeUuid, pointAUuid)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenRoutePointDoesNotExist() throws Exception {
        routePointApi
                .deleteRoutePoint(company.getUuid(), routeUuid, UUID.randomUUID())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenRoutePointBelongsToAnotherRoute() throws Exception {
        UUID otherRouteUuid = createRouteAndGetUuid(company.getUuid(), "Otra ruta");

        routePointApi
                .deleteRoutePoint(company.getUuid(), otherRouteUuid, pointAUuid)
                .andExpect(status().isNotFound());

        // el punto original no debe haberse dado de baja
        routePointApi
                .getRoutePoint(company.getUuid(), routeUuid, pointAUuid)
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenRouteBelongsToAnotherCompany() throws Exception {
        CompanyDTO anotherCompany = new CompanyTestFactory(
                mockMvc, objectMapper, ownerToken, geocodingService
        ).createCompany();

        UUID anotherRouteUuid = createRouteAndGetUuid(anotherCompany.getUuid(), "Ruta ajena");

        // se usa la empresa propia en la URL con la ruta de otra empresa
        routePointApi
                .deleteRoutePoint(company.getUuid(), anotherRouteUuid, pointAUuid)
                .andExpect(status().isNotFound());

        // el punto original no debe haberse dado de baja
        routePointApi
                .getRoutePoint(company.getUuid(), routeUuid, pointAUuid)
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenRouteIsDeleted() throws Exception {
        routeApi
                .deleteRoute(company.getUuid(), routeUuid)
                .andExpect(status().isOk());

        routePointApi
                .deleteRoutePoint(company.getUuid(), routeUuid, pointAUuid)
                .andExpect(status().isNotFound());
    }

    // ---------- permisos ----------

    @Test
    void shouldReturnForbiddenWhenCompanyDoesNotExist() throws Exception {
        routePointApi
                .deleteRoutePoint(UUID.randomUUID(), routeUuid, pointAUuid)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenUserHasNoAllowedRole() throws Exception {
        routePointApiForWorkerWithRoles(
                "driver", Set.of(RoleCompany.DRIVER)
                                       )
                .deleteRoutePoint(company.getUuid(), routeUuid, pointAUuid)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotWorkerOfTheCompany() throws Exception {
        String strangerToken = UserTestFactory.registerAndLogin(mockMvc);

        new RoutePointApiTestClient(mockMvc, objectMapper)
                .withToken(strangerToken)
                .deleteRoutePoint(company.getUuid(), routeUuid, pointAUuid)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        new RoutePointApiTestClient(mockMvc, objectMapper)
                .deleteRoutePoint(company.getUuid(), routeUuid, pointAUuid)
                .andExpect(status().isUnauthorized());
    }
}