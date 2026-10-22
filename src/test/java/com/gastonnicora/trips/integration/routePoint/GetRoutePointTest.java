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
class GetRoutePointTest {

    private static final String PASSWORD = "goodPassword";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GeocodingService geocodingService;

    private CompanyDTO company;
    private RouteApiTestClient routeApi;
    private RoutePointApiTestClient routePointApi;
    private WorkerApiTestClient ownerWorkerApi;
    private UUID routeUuid;
    private UUID routePointUuid;

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

    private UUID createRoutePointAndGetUuid(UUID targetRouteUuid) throws Exception {
        MvcResult result = routePointApi
                .createRoutePoint(
                        company.getUuid(),
                        targetRouteUuid,
                        "Terminal La Plata",
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

        company = new CompanyTestFactory(
                mockMvc,
                objectMapper,
                ownerLogin.getToken(),
                geocodingService
        ).createCompany();

        routeApi = new RouteApiTestClient(
                mockMvc, objectMapper
        ).withToken(ownerLogin.getToken());

        routePointApi = new RoutePointApiTestClient(
                mockMvc, objectMapper
        ).withToken(ownerLogin.getToken());

        ownerWorkerApi = new WorkerApiTestClient(
                mockMvc, objectMapper
        ).withToken(ownerLogin.getToken());

        MvcResult routeResult = routeApi
                .createRoute(
                        company.getUuid(),
                        "25 de Mayo - La Plata",
                        PricingType.PER_KM
                            )
                .andExpect(status().isOk())
                .andReturn();

        routeUuid = UUID.fromString(
                objectMapper.readTree(
                        routeResult.getResponse().getContentAsString()
                                     ).get("uuid").asString()
                                   );

        routePointUuid = createRoutePointAndGetUuid(routeUuid);
    }

    @Test
    void shouldAllowAdminToGetRoutePoint() throws Exception {
        routePointApiForWorkerWithRoles(
                "admin", Set.of(RoleCompany.ADMIN)
                                       )
                .getRoutePoint(company.getUuid(), routeUuid, routePointUuid)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(routePointUuid.toString()));
    }

    @Test
    void shouldReturnRoutePointSuccessfully() throws Exception {
        routePointApi
                .getRoutePoint(company.getUuid(), routeUuid, routePointUuid)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(routePointUuid.toString()))
                .andExpect(jsonPath("$.name").value("Terminal La Plata"))
                .andExpect(jsonPath("$.latitude").value(-34.9214))
                .andExpect(jsonPath("$.longitude").value(-57.9545));
    }


    @Test
    void shouldReturnNotFoundWhenRoutePointDoesNotExist() throws Exception {
        routePointApi
                .getRoutePoint(company.getUuid(), routeUuid, UUID.randomUUID())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenRoutePointBelongsToAnotherRoute() throws Exception {
        MvcResult otherRouteResult = routeApi
                .createRoute(
                        company.getUuid(),
                        "Otra ruta",
                        PricingType.PER_KM
                            )
                .andExpect(status().isOk())
                .andReturn();

        UUID otherRouteUuid = UUID.fromString(
                objectMapper.readTree(
                        otherRouteResult.getResponse().getContentAsString()
                                     ).get("uuid").asString()
                                             );

        routePointApi
                .getRoutePoint(company.getUuid(), otherRouteUuid, routePointUuid)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenRouteBelongsToAnotherCompany() throws Exception {
        CompanyDTO anotherCompany = new CompanyTestFactory(
                mockMvc, objectMapper, routeApi.getToken(), geocodingService
        ).createCompany();

        MvcResult anotherRouteResult = routeApi
                .createRoute(
                        anotherCompany.getUuid(),
                        "Ruta ajena",
                        PricingType.PER_KM
                            )
                .andExpect(status().isOk())
                .andReturn();

        UUID anotherRouteUuid = UUID.fromString(
                objectMapper.readTree(
                        anotherRouteResult.getResponse().getContentAsString()
                                     ).get("uuid").asString()
                                               );

        // se usa la empresa propia en la URL con la ruta de otra empresa
        routePointApi
                .getRoutePoint(company.getUuid(), anotherRouteUuid, routePointUuid)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenRouteIsDeleted() throws Exception {
        routeApi
                .deleteRoute(company.getUuid(), routeUuid)
                .andExpect(status().isOk());

        routePointApi
                .getRoutePoint(company.getUuid(), routeUuid, routePointUuid)
                .andExpect(status().isNotFound());
    }

    // ---------- permisos ----------

    @Test
    void shouldReturnForbiddenWhenCompanyDoesNotExist() throws Exception {
        routePointApi
                .getRoutePoint(UUID.randomUUID(), routeUuid, routePointUuid)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenUserHasNoAllowedRole() throws Exception {
        routePointApiForWorkerWithRoles(
                "driver", Set.of(RoleCompany.DRIVER)
                                       )
                .getRoutePoint(company.getUuid(), routeUuid, routePointUuid)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotWorkerOfTheCompany() throws Exception {
        String strangerToken = UserTestFactory.registerAndLogin(mockMvc);

        new RoutePointApiTestClient(mockMvc, objectMapper)
                .withToken(strangerToken)
                .getRoutePoint(company.getUuid(), routeUuid, routePointUuid)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        new RoutePointApiTestClient(mockMvc, objectMapper)
                .getRoutePoint(company.getUuid(), routeUuid, routePointUuid)
                .andExpect(status().isUnauthorized());
    }
}