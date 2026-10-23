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
class GetSectionPriceTest {

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
    private UUID sectionUuid;
    private UUID fromPointUuid;
    private UUID toPointUuid;
    private WorkerApiTestClient ownerWorkerApi;

    private SectionPriceApiTestClient sectionPriceApiForWorkerWithRoles(String name, Set<RoleCompany> roles)
            throws Exception {

        UserDTO user = UserTestFactory.registerUser(mockMvc, name, PASSWORD);

        ownerWorkerApi
                .createWorker(company.getUuid(), user.getUuid(), roles)
                .andExpect(status().isOk());

        LoginResponse login = UserTestFactory.login(mockMvc, user.getEmail(), PASSWORD);

        return new SectionPriceApiTestClient(mockMvc, objectMapper).withToken(login.getToken());
    }

    private UUID readUuid(MvcResult result) throws Exception {
        return UUID.fromString(objectMapper.readTree(result.getResponse().getContentAsString())
                                       .get("uuid").asString());
    }

    private UUID createRouteUuid(UUID companyUuid, String name) throws Exception {
        MvcResult result = new RouteApiTestClient(mockMvc, objectMapper).withToken(ownerToken)
                .createRoute(companyUuid, name, PricingType.PER_SECTION)
                .andExpect(status().isOk())
                .andReturn();
        return readUuid(result);
    }

    private UUID createPoint(UUID companyUuid, UUID targetRouteUuid, String name) throws Exception {
        MvcResult result = new RoutePointApiTestClient(mockMvc, objectMapper).withToken(ownerToken)
                .createRoutePoint(companyUuid, targetRouteUuid, name, -34.92, -57.95)
                .andExpect(status().isOk())
                .andReturn();
        return readUuid(result);
    }

    private UUID createSection(UUID companyUuid, UUID targetRouteUuid, UUID from, UUID to) throws Exception {
        MvcResult result = new SectionPriceApiTestClient(mockMvc, objectMapper).withToken(ownerToken)
                .createSectionPrice(companyUuid, targetRouteUuid, from, to, PRICE)
                .andExpect(status().isOk())
                .andReturn();
        return readUuid(result);
    }

    @BeforeEach
    void setup() throws Exception {
        UserDTO owner = UserTestFactory.registerUser(mockMvc, "owner", PASSWORD);

        LoginResponse ownerLogin = UserTestFactory.login(mockMvc, owner.getEmail(), PASSWORD);
        ownerToken = ownerLogin.getToken();

        company = new CompanyTestFactory(mockMvc, objectMapper, ownerToken, geocodingService)
                .createCompany();

        ownerWorkerApi = new WorkerApiTestClient(mockMvc, objectMapper).withToken(ownerToken);

        routeUuid = createRouteUuid(company.getUuid(), "25 de Mayo - La Plata");
        fromPointUuid = createPoint(company.getUuid(), routeUuid, "A");
        toPointUuid = createPoint(company.getUuid(), routeUuid, "B");
        sectionUuid = createSection(company.getUuid(), routeUuid, fromPointUuid, toPointUuid);
    }

    @Test
    void shouldGetSectionPriceSuccessfully() throws Exception {
        new SectionPriceApiTestClient(mockMvc, objectMapper).withToken(ownerToken)
                .getSectionPrice(company.getUuid(), routeUuid, sectionUuid)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(sectionUuid.toString()))
                .andExpect(jsonPath("$.fromPoint.uuid").value(fromPointUuid.toString()))
                .andExpect(jsonPath("$.toPoint.uuid").value(toPointUuid.toString()))
                .andExpect(jsonPath("$.price").value(1000.0))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldAllowAdminToGetSectionPrice() throws Exception {
        sectionPriceApiForWorkerWithRoles("admin", Set.of(RoleCompany.ADMIN))
                .getSectionPrice(company.getUuid(), routeUuid, sectionUuid)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(sectionUuid.toString()));
    }

    @Test
    void shouldReturnForbiddenWhenUserHasNoAllowedRole() throws Exception {
        sectionPriceApiForWorkerWithRoles("driver", Set.of(RoleCompany.DRIVER))
                .getSectionPrice(company.getUuid(), routeUuid, sectionUuid)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotWorkerOfTheCompany() throws Exception {
        String strangerToken = UserTestFactory.registerAndLogin(mockMvc);

        new SectionPriceApiTestClient(mockMvc, objectMapper)
                .withToken(strangerToken)
                .getSectionPrice(company.getUuid(), routeUuid, sectionUuid)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        new SectionPriceApiTestClient(mockMvc, objectMapper)
                .getSectionPrice(company.getUuid(), routeUuid, sectionUuid)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnNotFoundWhenSectionPriceDoesNotExist() throws Exception {
        new SectionPriceApiTestClient(mockMvc, objectMapper).withToken(ownerToken)
                .getSectionPrice(company.getUuid(), routeUuid, UUID.randomUUID())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenSectionPriceBelongsToAnotherRoute() throws Exception {
        UUID otherRouteUuid = createRouteUuid(company.getUuid(), "Otra ruta");

        new SectionPriceApiTestClient(mockMvc, objectMapper).withToken(ownerToken)
                .getSectionPrice(company.getUuid(), otherRouteUuid, sectionUuid)
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
        UUID anotherSectionUuid = createSection(anotherCompany.getUuid(), anotherRouteUuid, a, b);

        // Se usa la empresa propia en la URL con la ruta y el tramo de otra empresa
        new SectionPriceApiTestClient(mockMvc, objectMapper).withToken(ownerToken)
                .getSectionPrice(company.getUuid(), anotherRouteUuid, anotherSectionUuid)
                .andExpect(status().isNotFound());
    }
}