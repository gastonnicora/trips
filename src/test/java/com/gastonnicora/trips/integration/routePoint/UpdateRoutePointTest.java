package com.gastonnicora.trips.integration.routePoint;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.response.auth.LoginResponse;
import com.gastonnicora.trips.entities.RoutePoint;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.helpers.company.CompanyTestFactory;
import com.gastonnicora.trips.helpers.route.RouteApiTestClient;
import com.gastonnicora.trips.helpers.routePoint.RoutePointApiTestClient;
import com.gastonnicora.trips.helpers.user.UserTestFactory;
import com.gastonnicora.trips.helpers.worker.WorkerApiTestClient;
import com.gastonnicora.trips.repositories.RoutePointRepository;
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
class UpdateRoutePointTest {

    private static final String PASSWORD = "goodPassword";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoutePointRepository routePointRepository;

    @MockitoBean
    private GeocodingService geocodingService;

    private CompanyDTO company;
    private String ownerToken;
    private RouteApiTestClient routeApi;
    private RoutePointApiTestClient routePointApi;
    private WorkerApiTestClient ownerWorkerApi;
    private UUID routeUuid;
    private UUID routePointUuid;

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

        routePointUuid = createRoutePointAndGetUuid(company.getUuid(), routeUuid, "Terminal Original");
    }

    @Test
    void shouldAllowAdminToUpdateRoutePoint() throws Exception {
        routePointApiForWorkerWithRoles(
                "admin", Set.of(RoleCompany.ADMIN)
                                       )
                .updateRoutePoint(
                        company.getUuid(),
                        routeUuid,
                        routePointUuid,
                        "Terminal Admin",
                        -34.9214,
                        -57.9545
                                 )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Terminal Admin"));
    }

    @Test
    void shouldPersistUpdatedRoutePoint() throws Exception {
        routePointApi
                .updateRoutePoint(
                        company.getUuid(),
                        routeUuid,
                        routePointUuid,
                        "Terminal Persistida",
                        -34.9300,
                        -57.9600
                                 )
                .andExpect(status().isOk());

        routePointApi
                .getRoutePoint(company.getUuid(), routeUuid, routePointUuid)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Terminal Persistida"))
                .andExpect(jsonPath("$.latitude").value(-34.9300))
                .andExpect(jsonPath("$.longitude").value(-57.9600));
    }

    @Test
    void shouldReturnBadRequestWhenLatitudeIsOutOfRange() throws Exception {
        routePointApi
                .updateRoutePoint(
                        company.getUuid(),
                        routeUuid,
                        routePointUuid,
                        "Terminal",
                        100.0,
                        -57.9545
                                 )
                .andExpect(status().isBadRequest());
    }

    // ---------- validaciones ----------

    @Test
    void shouldReturnBadRequestWhenLongitudeIsOutOfRange() throws Exception {
        routePointApi
                .updateRoutePoint(
                        company.getUuid(),
                        routeUuid,
                        routePointUuid,
                        "Terminal",
                        -34.9214,
                        200.0
                                 )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenNameIsBlank() throws Exception {
        routePointApi
                .updateRoutePoint(
                        company.getUuid(),
                        routeUuid,
                        routePointUuid,
                        "",
                        -34.9214,
                        -57.9545
                                 )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnForbiddenWhenCompanyDoesNotExist() throws Exception {
        routePointApi
                .updateRoutePoint(
                        UUID.randomUUID(),
                        routeUuid,
                        routePointUuid,
                        "Terminal",
                        -34.9214,
                        -57.9545
                                 )
                .andExpect(status().isForbidden());
    }

    // ---------- errores de negocio ----------

    @Test
    void shouldReturnForbiddenWhenUserHasNoAllowedRole() throws Exception {
        routePointApiForWorkerWithRoles(
                "driver", Set.of(RoleCompany.DRIVER)
                                       )
                .updateRoutePoint(
                        company.getUuid(),
                        routeUuid,
                        routePointUuid,
                        "Terminal Driver",
                        -34.9214,
                        -57.9545
                                 )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotWorkerOfTheCompany() throws Exception {
        String strangerToken = UserTestFactory.registerAndLogin(mockMvc);

        new RoutePointApiTestClient(mockMvc, objectMapper)
                .withToken(strangerToken)
                .updateRoutePoint(
                        company.getUuid(),
                        routeUuid,
                        routePointUuid,
                        "Terminal ajena",
                        -34.9214,
                        -57.9545
                                 )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnNotFoundWhenRouteBelongsToAnotherCompany() throws Exception {
        CompanyDTO anotherCompany = new CompanyTestFactory(
                mockMvc, objectMapper, ownerToken, geocodingService
        ).createCompany();

        UUID anotherRouteUuid = createRouteAndGetUuid(anotherCompany.getUuid(), "Ruta ajena");

        // se usa la empresa propia en la URL con la ruta de otra empresa
        routePointApi
                .updateRoutePoint(
                        company.getUuid(),
                        anotherRouteUuid,
                        routePointUuid,
                        "Terminal",
                        -34.9214,
                        -57.9545
                                 )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenRouteIsDeleted() throws Exception {
        routeApi
                .deleteRoute(company.getUuid(), routeUuid)
                .andExpect(status().isOk());

        routePointApi
                .updateRoutePoint(
                        company.getUuid(),
                        routeUuid,
                        routePointUuid,
                        "Terminal",
                        -34.9214,
                        -57.9545
                                 )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenRoutePointBelongsToAnotherRoute() throws Exception {
        UUID otherRouteUuid = createRouteAndGetUuid(company.getUuid(), "Otra ruta");

        routePointApi
                .updateRoutePoint(
                        company.getUuid(),
                        otherRouteUuid,
                        routePointUuid,
                        "Terminal",
                        -34.9214,
                        -57.9545
                                 )
                .andExpect(status().isNotFound());
    }

    // ---------- permisos ----------

    @Test
    void shouldReturnNotFoundWhenRoutePointDoesNotExist() throws Exception {
        routePointApi
                .updateRoutePoint(
                        company.getUuid(),
                        routeUuid,
                        UUID.randomUUID(),
                        "Terminal",
                        -34.9214,
                        -57.9545
                                 )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenRoutePointIsInactive() throws Exception {
        RoutePoint routePoint = routePointRepository
                .findByRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndUuidAndActiveTrue(
                        company.getUuid(), routeUuid, routePointUuid).orElseThrow();
        routePoint.setActive(false);
        routePointRepository.save(routePoint);

        routePointApi
                .updateRoutePoint(
                        company.getUuid(),
                        routeUuid,
                        routePointUuid,
                        "Terminal",
                        -34.9214,
                        -57.9545
                                 )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        new RoutePointApiTestClient(mockMvc, objectMapper)
                .updateRoutePoint(
                        company.getUuid(),
                        routeUuid,
                        routePointUuid,
                        "Terminal",
                        -34.9214,
                        -57.9545
                                 )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldUpdateRoutePointSuccessfully() throws Exception {
        routePointApi
                .updateRoutePoint(
                        company.getUuid(),
                        routeUuid,
                        routePointUuid,
                        "Terminal La Plata",
                        -34.9300,
                        -57.9600
                                 )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(routePointUuid.toString()))
                .andExpect(jsonPath("$.name").value("Terminal La Plata"))
                .andExpect(jsonPath("$.latitude").value(-34.9300))
                .andExpect(jsonPath("$.longitude").value(-57.9600));
    }
}