package com.gastonnicora.trips.integration.sectionPrice;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.response.auth.LoginResponse;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.helpers.company.CompanyTestFactory;
import com.gastonnicora.trips.helpers.route.RouteApiTestClient;
import com.gastonnicora.trips.helpers.routePoint.RoutePointApiTestClient;
import com.gastonnicora.trips.helpers.sectionPrice.SectionPriceApiTestClient;
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
class CreateSectionPriceTest {

    private static final String PASSWORD = "goodPassword";
    private static final BigDecimal PRICE = new BigDecimal("1000.00");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GeocodingService geocodingService;

    private CompanyDTO company;
    private String ownerToken;
    private UUID routeUuid;
    private WorkerApiTestClient ownerWorkerApi;
    private SectionPriceApiTestClient sectionPriceApi;

    private SectionPriceApiTestClient sectionPriceApiForWorkerWithRoles(String name, Set<RoleCompany> roles)
            throws Exception {

        UserDTO user = UserTestFactory.registerUser(mockMvc, name, PASSWORD);

        ownerWorkerApi
                .createWorker(company.getUuid(), user.getUuid(), roles)
                .andExpect(status().isOk());

        LoginResponse login = UserTestFactory.login(mockMvc, user.getEmail(), PASSWORD);

        return new SectionPriceApiTestClient(mockMvc, objectMapper).withToken(login.getToken());
    }

    private UUID createRouteUuid(UUID companyUuid, String name) throws Exception {
        MvcResult result = new RouteApiTestClient(mockMvc, objectMapper).withToken(ownerToken)
                .createRoute(companyUuid, name, PricingType.PER_SECTION)
                .andExpect(status().isOk())
                .andReturn();

        return UUID.fromString(objectMapper.readTree(result.getResponse().getContentAsString())
                                       .get("uuid").asString());
    }

    private UUID createPoint(UUID companyUuid, UUID targetRouteUuid, String name) throws Exception {
        MvcResult result = new RoutePointApiTestClient(mockMvc, objectMapper).withToken(ownerToken)
                .createRoutePoint(companyUuid, targetRouteUuid, name, -34.92, -57.95)
                .andExpect(status().isOk())
                .andReturn();

        return UUID.fromString(objectMapper.readTree(result.getResponse().getContentAsString())
                                       .get("uuid").asString());
    }

    @BeforeEach
    void setup() throws Exception {
        UserDTO owner = UserTestFactory.registerUser(mockMvc, "owner", PASSWORD);

        LoginResponse ownerLogin = UserTestFactory.login(mockMvc, owner.getEmail(), PASSWORD);
        ownerToken = ownerLogin.getToken();

        company = new CompanyTestFactory(mockMvc, objectMapper, ownerToken, geocodingService)
                .createCompany();

        ownerWorkerApi = new WorkerApiTestClient(mockMvc, objectMapper).withToken(ownerToken);
        sectionPriceApi = new SectionPriceApiTestClient(mockMvc, objectMapper).withToken(ownerToken);

        routeUuid = createRouteUuid(company.getUuid(), "25 de Mayo - La Plata");
    }

    @Test
    void shouldCreateFirstSectionSuccessfully() throws Exception {
        UUID a = createPoint(company.getUuid(), routeUuid, "A");
        UUID b = createPoint(company.getUuid(), routeUuid, "B");

        sectionPriceApi
                .createSectionPrice(company.getUuid(), routeUuid, a, b, PRICE)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").exists())
                .andExpect(jsonPath("$.fromPoint.uuid").value(a.toString()))
                .andExpect(jsonPath("$.toPoint.uuid").value(b.toString()))
                .andExpect(jsonPath("$.price").value(1000.0))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldCreateContinuousSectionAfterPrevious() throws Exception {
        UUID a = createPoint(company.getUuid(), routeUuid, "A");
        UUID b = createPoint(company.getUuid(), routeUuid, "B");
        UUID c = createPoint(company.getUuid(), routeUuid, "C");

        sectionPriceApi
                .createSectionPrice(company.getUuid(), routeUuid, a, b, PRICE)
                .andExpect(status().isOk());

        sectionPriceApi
                .createSectionPrice(company.getUuid(), routeUuid, b, c, new BigDecimal("2000.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromPoint.uuid").value(b.toString()))
                .andExpect(jsonPath("$.toPoint.uuid").value(c.toString()));
    }

    @Test
    void shouldAllowAdminToCreateSection() throws Exception {
        UUID a = createPoint(company.getUuid(), routeUuid, "A");
        UUID b = createPoint(company.getUuid(), routeUuid, "B");

        sectionPriceApiForWorkerWithRoles("admin", Set.of(RoleCompany.ADMIN))
                .createSectionPrice(company.getUuid(), routeUuid, a, b, PRICE)
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequestWhenFromPointIsNull() throws Exception {
        UUID b = createPoint(company.getUuid(), routeUuid, "B");

        sectionPriceApi
                .createSectionPrice(company.getUuid(), routeUuid, null, b, PRICE)
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenPriceIsNegative() throws Exception {
        UUID a = createPoint(company.getUuid(), routeUuid, "A");
        UUID b = createPoint(company.getUuid(), routeUuid, "B");

        sectionPriceApi
                .createSectionPrice(company.getUuid(), routeUuid, a, b, new BigDecimal("-1.00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenBodyIsMalformed() throws Exception {
        sectionPriceApi
                .createSectionPriceRaw(company.getUuid(), routeUuid, "{ esto no es json")
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenDestinationIsNotAfterOrigin() throws Exception {
        UUID a = createPoint(company.getUuid(), routeUuid, "A");
        UUID b = createPoint(company.getUuid(), routeUuid, "B");

        sectionPriceApi
                .createSectionPrice(company.getUuid(), routeUuid, b, a, PRICE)
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenSectionIsNotContinuous() throws Exception {
        UUID a = createPoint(company.getUuid(), routeUuid, "A");
        UUID b = createPoint(company.getUuid(), routeUuid, "B");
        UUID c = createPoint(company.getUuid(), routeUuid, "C");
        UUID d = createPoint(company.getUuid(), routeUuid, "D");

        sectionPriceApi
                .createSectionPrice(company.getUuid(), routeUuid, a, b, PRICE)
                .andExpect(status().isOk());

        // C -> D no empieza donde terminó A -> B
        sectionPriceApi
                .createSectionPrice(company.getUuid(), routeUuid, c, d, PRICE)
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnConflictWhenSectionAlreadyExists() throws Exception {
        UUID a = createPoint(company.getUuid(), routeUuid, "A");
        UUID b = createPoint(company.getUuid(), routeUuid, "B");

        sectionPriceApi
                .createSectionPrice(company.getUuid(), routeUuid, a, b, PRICE)
                .andExpect(status().isOk());

        sectionPriceApi
                .createSectionPrice(company.getUuid(), routeUuid, a, b, PRICE)
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnConflictWhenSectionOverlapsExistingOne() throws Exception {
        UUID a = createPoint(company.getUuid(), routeUuid, "A");
        UUID b = createPoint(company.getUuid(), routeUuid, "B");
        UUID c = createPoint(company.getUuid(), routeUuid, "C");

        sectionPriceApi
                .createSectionPrice(company.getUuid(), routeUuid, a, b, PRICE)
                .andExpect(status().isOk());

        // A -> C abarca A -> B
        sectionPriceApi
                .createSectionPrice(company.getUuid(), routeUuid, a, c, PRICE)
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnConflictWhenRouteHasNoPoints() throws Exception {
        sectionPriceApi
                .createSectionPrice(company.getUuid(), routeUuid, UUID.randomUUID(), UUID.randomUUID(), PRICE)
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnNotFoundWhenPointBelongsToAnotherRoute() throws Exception {
        UUID a = createPoint(company.getUuid(), routeUuid, "A");
        UUID otherRouteUuid = createRouteUuid(company.getUuid(), "Otra ruta");
        UUID foreignPoint = createPoint(company.getUuid(), otherRouteUuid, "X");

        sectionPriceApi
                .createSectionPrice(company.getUuid(), routeUuid, a, foreignPoint, PRICE)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenRouteBelongsToAnotherCompany() throws Exception {
        CompanyDTO anotherCompany = new CompanyTestFactory(
                mockMvc, objectMapper, ownerToken, geocodingService
        ).createCompany();

        UUID anotherRouteUuid = createRouteUuid(anotherCompany.getUuid(), "Ruta ajena");
        UUID a = createPoint(anotherCompany.getUuid(), anotherRouteUuid, "A");
        UUID b = createPoint(anotherCompany.getUuid(), anotherRouteUuid, "B");

        // Se usa la empresa propia en la URL con la ruta de otra empresa
        sectionPriceApi
                .createSectionPrice(company.getUuid(), anotherRouteUuid, a, b, PRICE)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenRouteDoesNotExist() throws Exception {
        sectionPriceApi
                .createSectionPrice(company.getUuid(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), PRICE)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnForbiddenWhenUserHasNoAllowedRole() throws Exception {
        UUID a = createPoint(company.getUuid(), routeUuid, "A");
        UUID b = createPoint(company.getUuid(), routeUuid, "B");

        sectionPriceApiForWorkerWithRoles("driver", Set.of(RoleCompany.DRIVER))
                .createSectionPrice(company.getUuid(), routeUuid, a, b, PRICE)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotWorkerOfTheCompany() throws Exception {
        UUID a = createPoint(company.getUuid(), routeUuid, "A");
        UUID b = createPoint(company.getUuid(), routeUuid, "B");

        String strangerToken = UserTestFactory.registerAndLogin(mockMvc);

        new SectionPriceApiTestClient(mockMvc, objectMapper)
                .withToken(strangerToken)
                .createSectionPrice(company.getUuid(), routeUuid, a, b, PRICE)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        UUID a = createPoint(company.getUuid(), routeUuid, "A");
        UUID b = createPoint(company.getUuid(), routeUuid, "B");

        new SectionPriceApiTestClient(mockMvc, objectMapper)
                .createSectionPrice(company.getUuid(), routeUuid, a, b, PRICE)
                .andExpect(status().isUnauthorized());
    }
}