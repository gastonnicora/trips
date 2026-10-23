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
class DeleteSectionPriceTest {

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
    private UUID pointBUuid;
    private UUID pointCUuid;
    private UUID firstSectionUuid;
    private UUID lastSectionUuid;
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
        pointBUuid = createPoint(company.getUuid(), routeUuid, "B");
        pointCUuid = createPoint(company.getUuid(), routeUuid, "C");

        firstSectionUuid = createSection(company.getUuid(), routeUuid, a, pointBUuid, PRICE);
        lastSectionUuid = createSection(company.getUuid(), routeUuid, pointBUuid, pointCUuid, PRICE);
    }

    @Test
    void shouldDeleteLastSectionPriceSuccessfully() throws Exception {
        SectionPriceApiTestClient api = new SectionPriceApiTestClient(mockMvc, objectMapper).withToken(ownerToken);

        api.deleteSectionPrice(company.getUuid(), routeUuid, lastSectionUuid)
                .andExpect(status().isOk());

        api.getSectionPrice(company.getUuid(), routeUuid, lastSectionUuid)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotListDeletedSectionPrice() throws Exception {
        SectionPriceApiTestClient api = new SectionPriceApiTestClient(mockMvc, objectMapper).withToken(ownerToken);

        api.deleteSectionPrice(company.getUuid(), routeUuid, lastSectionUuid)
                .andExpect(status().isOk());

        api.getSectionPrices(company.getUuid(), routeUuid)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].uuid").value(firstSectionUuid.toString()));
    }

    @Test
    void shouldAllowDeletingOnlySectionOfRoute() throws Exception {
        UUID otherRouteUuid = createRouteUuid(company.getUuid(), "Ruta de un tramo");
        UUID x = createPoint(company.getUuid(), otherRouteUuid, "X");
        UUID y = createPoint(company.getUuid(), otherRouteUuid, "Y");
        UUID onlySectionUuid = createSection(company.getUuid(), otherRouteUuid, x, y, PRICE);

        new SectionPriceApiTestClient(mockMvc, objectMapper).withToken(ownerToken)
                .deleteSectionPrice(company.getUuid(), otherRouteUuid, onlySectionUuid)
                .andExpect(status().isOk());
    }

    @Test
    void shouldReuseSameUuidWhenRecreatingDeletedLastSection() throws Exception {
        SectionPriceApiTestClient api = new SectionPriceApiTestClient(mockMvc, objectMapper).withToken(ownerToken);

        api.deleteSectionPrice(company.getUuid(), routeUuid, lastSectionUuid)
                .andExpect(status().isOk());

        // B -> C vuelve a ser válido porque el último tramo activo termina en B
        api.createSectionPrice(company.getUuid(), routeUuid, pointBUuid, pointCUuid, new BigDecimal("3000.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(lastSectionUuid.toString()))
                .andExpect(jsonPath("$.price").value(3000.0))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldAllowAdminToDeleteSectionPrice() throws Exception {
        sectionPriceApiForWorkerWithRoles("admin", Set.of(RoleCompany.ADMIN))
                .deleteSectionPrice(company.getUuid(), routeUuid, lastSectionUuid)
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnConflictWhenSectionIsNotLastActiveOne() throws Exception {
        new SectionPriceApiTestClient(mockMvc, objectMapper).withToken(ownerToken)
                .deleteSectionPrice(company.getUuid(), routeUuid, firstSectionUuid)
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnNotFoundWhenSectionPriceIsAlreadyDeleted() throws Exception {
        SectionPriceApiTestClient api = new SectionPriceApiTestClient(mockMvc, objectMapper).withToken(ownerToken);

        api.deleteSectionPrice(company.getUuid(), routeUuid, lastSectionUuid)
                .andExpect(status().isOk());

        api.deleteSectionPrice(company.getUuid(), routeUuid, lastSectionUuid)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenSectionPriceDoesNotExist() throws Exception {
        new SectionPriceApiTestClient(mockMvc, objectMapper).withToken(ownerToken)
                .deleteSectionPrice(company.getUuid(), routeUuid, UUID.randomUUID())
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
        UUID anotherSectionUuid = createSection(anotherCompany.getUuid(), anotherRouteUuid, a, b, PRICE);

        // Se usa la empresa propia en la URL con la ruta y el tramo de otra empresa
        new SectionPriceApiTestClient(mockMvc, objectMapper).withToken(ownerToken)
                .deleteSectionPrice(company.getUuid(), anotherRouteUuid, anotherSectionUuid)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnForbiddenWhenUserHasNoAllowedRole() throws Exception {
        sectionPriceApiForWorkerWithRoles("driver", Set.of(RoleCompany.DRIVER))
                .deleteSectionPrice(company.getUuid(), routeUuid, lastSectionUuid)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotWorkerOfTheCompany() throws Exception {
        String strangerToken = UserTestFactory.registerAndLogin(mockMvc);

        new SectionPriceApiTestClient(mockMvc, objectMapper)
                .withToken(strangerToken)
                .deleteSectionPrice(company.getUuid(), routeUuid, lastSectionUuid)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        new SectionPriceApiTestClient(mockMvc, objectMapper)
                .deleteSectionPrice(company.getUuid(), routeUuid, lastSectionUuid)
                .andExpect(status().isUnauthorized());
    }
}