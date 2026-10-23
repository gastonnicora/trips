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

import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GetSectionPricesTest {

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
    private UUID firstSectionUuid;
    private UUID secondSectionUuid;
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

    private UUID createSection(UUID companyUuid, UUID targetRouteUuid, UUID from, UUID to, BigDecimal price)
            throws Exception {
        MvcResult result = new SectionPriceApiTestClient(mockMvc, objectMapper).withToken(ownerToken)
                .createSectionPrice(companyUuid, targetRouteUuid, from, to, price)
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
        UUID a = createPoint(company.getUuid(), routeUuid, "A");
        UUID b = createPoint(company.getUuid(), routeUuid, "B");
        UUID c = createPoint(company.getUuid(), routeUuid, "C");

        firstSectionUuid = createSection(company.getUuid(), routeUuid, a, b, PRICE);
        secondSectionUuid = createSection(company.getUuid(), routeUuid, b, c, new BigDecimal("2000.00"));
    }

    @Test
    void shouldGetAllSectionPricesOfRoute() throws Exception {
        new SectionPriceApiTestClient(mockMvc, objectMapper).withToken(ownerToken)
                .getSectionPrices(company.getUuid(), routeUuid)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].uuid").value(hasItems(
                        firstSectionUuid.toString(), secondSectionUuid.toString())));
    }

    @Test
    void shouldAllowAdminToGetSectionPrices() throws Exception {
        sectionPriceApiForWorkerWithRoles("admin", Set.of(RoleCompany.ADMIN))
                .getSectionPrices(company.getUuid(), routeUuid)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldReturnNotFoundWhenRouteHasNoSectionPrices() throws Exception {
        UUID emptyRouteUuid = createRouteUuid(company.getUuid(), "Ruta vacía");

        new SectionPriceApiTestClient(mockMvc, objectMapper).withToken(ownerToken)
                .getSectionPrices(company.getUuid(), emptyRouteUuid)
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
        createSection(anotherCompany.getUuid(), anotherRouteUuid, a, b, PRICE);

        // Se usa la empresa propia en la URL con la ruta de otra empresa
        new SectionPriceApiTestClient(mockMvc, objectMapper).withToken(ownerToken)
                .getSectionPrices(company.getUuid(), anotherRouteUuid)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnForbiddenWhenUserHasNoAllowedRole() throws Exception {
        sectionPriceApiForWorkerWithRoles("driver", Set.of(RoleCompany.DRIVER))
                .getSectionPrices(company.getUuid(), routeUuid)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotWorkerOfTheCompany() throws Exception {
        String strangerToken = UserTestFactory.registerAndLogin(mockMvc);

        new SectionPriceApiTestClient(mockMvc, objectMapper)
                .withToken(strangerToken)
                .getSectionPrices(company.getUuid(), routeUuid)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        new SectionPriceApiTestClient(mockMvc, objectMapper)
                .getSectionPrices(company.getUuid(), routeUuid)
                .andExpect(status().isUnauthorized());
    }
}