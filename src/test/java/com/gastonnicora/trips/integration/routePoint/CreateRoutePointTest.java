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
class CreateRoutePointTest {

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
    private String ownerToken;

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
    void shouldAllowAdminToCreateRoutePoint() throws Exception {
        routePointApiForWorkerWithRoles(
                "admin", Set.of(RoleCompany.ADMIN)
                                       )
                .createRoutePoint(
                        company.getUuid(),
                        routeUuid,
                        "Terminal Admin",
                        -34.9214,
                        -57.9545
                                 )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Terminal Admin"));
    }

    @Test
    void shouldCreateRoutePointSuccessfully() throws Exception {
        routePointApi
                .createRoutePoint(
                        company.getUuid(),
                        routeUuid,
                        "Terminal La Plata",
                        -34.9214,
                        -57.9545
                                 )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").exists())
                .andExpect(jsonPath("$.name").value("Terminal La Plata"))
                .andExpect(jsonPath("$.latitude").value(-34.9214))
                .andExpect(jsonPath("$.longitude").value(-57.9545));
    }

    @Test
    void shouldReturnBadRequestWhenBodyIsMalformed() throws Exception {
        routePointApi
                .createRoutePointRaw(
                        company.getUuid(),
                        routeUuid,
                        "{ esto no es json"
                                    )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenLatitudeIsNull() throws Exception {
        routePointApi
                .createRoutePoint(
                        company.getUuid(),
                        routeUuid,
                        "Punto inválido",
                        null,
                        -57.9545
                                 )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenLatitudeIsOutOfRange() throws Exception {
        routePointApi
                .createRoutePoint(
                        company.getUuid(),
                        routeUuid,
                        "Punto inválido",
                        91.0,
                        -57.9545
                                 )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenLongitudeIsNull() throws Exception {
        routePointApi
                .createRoutePoint(
                        company.getUuid(),
                        routeUuid,
                        "Punto inválido",
                        -34.9214,
                        null
                                 )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenLongitudeIsOutOfRange() throws Exception {
        routePointApi
                .createRoutePoint(
                        company.getUuid(),
                        routeUuid,
                        "Punto inválido",
                        -34.9214,
                        181.0
                                 )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenNameIsBlank() throws Exception {
        routePointApi
                .createRoutePoint(
                        company.getUuid(),
                        routeUuid,
                        "",
                        -34.9214,
                        -57.9545
                                 )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenNameIsNull() throws Exception {
        routePointApi
                .createRoutePoint(
                        company.getUuid(),
                        routeUuid,
                        null,
                        -34.9214,
                        -57.9545
                                 )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenNameIsTooLong() throws Exception {
        routePointApi
                .createRoutePoint(
                        company.getUuid(),
                        routeUuid,
                        "a".repeat(256),
                        -34.9214,
                        -57.9545
                                 )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnForbiddenWhenCompanyDoesNotExist() throws Exception {
        routePointApi
                .createRoutePoint(
                        UUID.randomUUID(),
                        routeUuid,
                        "Terminal",
                        -34.9214,
                        -57.9545
                                 )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenUserHasNoAllowedRole() throws Exception {
        routePointApiForWorkerWithRoles(
                "driver", Set.of(RoleCompany.DRIVER)
                                       )
                .createRoutePoint(
                        company.getUuid(),
                        routeUuid,
                        "Terminal Driver",
                        -34.9214,
                        -57.9545
                                 )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotWorkerOfTheCompany()
            throws Exception {

        String strangerToken = UserTestFactory.registerAndLogin(mockMvc);

        new RoutePointApiTestClient(mockMvc, objectMapper)
                .withToken(strangerToken)
                .createRoutePoint(
                        company.getUuid(),
                        routeUuid,
                        "Terminal ajena",
                        -34.9214,
                        -57.9545
                                 )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        new RoutePointApiTestClient(mockMvc, objectMapper)
                .createRoutePoint(
                        company.getUuid(),
                        routeUuid,
                        "Terminal",
                        -34.9214,
                        -57.9545
                                 )
                .andExpect(status().isUnauthorized());
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
        routePointApi
                .createRoutePoint(company.getUuid(), anotherRouteUuid,
                                  "Terminal Ajena", -34.9214, -57.9545)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenRouteIsDeleted() throws Exception {

        routeApi
                .deleteRoute(company.getUuid(), routeUuid)
                .andExpect(status().isOk());

        routePointApi
                .createRoutePoint(company.getUuid(), routeUuid,
                                  "Terminal Borrada", -34.9214, -57.9545)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenRouteDoesNotExist() throws Exception {

        routePointApi
                .createRoutePoint(company.getUuid(), UUID.randomUUID(),
                                  "Terminal Inexistente", -34.9214, -57.9545)
                .andExpect(status().isNotFound());
    }
}
