package com.gastonnicora.trips.integration.trip;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.entities.RouteDTO;
import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.entities.VehicleDTO;
import com.gastonnicora.trips.dtos.request.trip.TripCreate;
import com.gastonnicora.trips.dtos.response.auth.LoginResponse;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.enums.TypeTrip;
import com.gastonnicora.trips.helpers.company.CompanyTestFactory;
import com.gastonnicora.trips.helpers.route.RouteTestFactory;
import com.gastonnicora.trips.helpers.trip.TripApiTestClient;
import com.gastonnicora.trips.helpers.user.UserTestFactory;
import com.gastonnicora.trips.helpers.vehicle.VehicleApiTestClient;
import com.gastonnicora.trips.helpers.vehicle.VehicleTestFactory;
import com.gastonnicora.trips.helpers.worker.WorkerApiTestClient;
import com.gastonnicora.trips.services.GeocodingService;
import com.jayway.jsonpath.JsonPath;
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

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UpdateTripTest {

    private static final String PASSWORD = "goodPassword";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GeocodingService geocodingService;

    private String ownerToken;
    private CompanyDTO company;
    private RouteDTO route;
    private VehicleDTO vehicle;
    private UserDTO driver;
    private VehicleDTO newVehicle;
    private UserDTO newDriver;
    private UUID tripUuid;
    private TripApiTestClient tripApi;

    @BeforeEach
    void setup() throws Exception {
        UserDTO owner = UserTestFactory.registerUser(mockMvc, "owner", PASSWORD);
        ownerToken = UserTestFactory.login(mockMvc, owner.getEmail(), PASSWORD).getToken();

        company = new CompanyTestFactory(mockMvc, objectMapper, ownerToken, geocodingService).createCompany();

        route = new RouteTestFactory(mockMvc, objectMapper, ownerToken)
                .createRoute(company.getUuid(), "Ruta de prueba", PricingType.PER_KM);

        vehicle = new VehicleTestFactory(mockMvc, objectMapper, ownerToken)
                .createVehicle(company.getUuid());

        newVehicle = createVehicle("BB456CC", "Iveco", 40);

        driver = createDriver("driver");
        newDriver = createDriver("newDriver");

        tripApi = new TripApiTestClient(mockMvc, objectMapper).withToken(ownerToken);
        tripUuid = createInitialTrip();
    }

    // ---------- casos felices ----------

    @Test
    void shouldAllowOwnerToUpdateTrip() throws Exception {
        tripApi.updateTrip(company.getUuid(), tripUuid, newVehicle.getUuid(), newDriver.getUuid())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(tripUuid.toString()))
                .andExpect(jsonPath("$.vehicle.uuid").value(newVehicle.getUuid().toString()))
                .andExpect(jsonPath("$.driver.uuid").value(newDriver.getUuid().toString()));
    }

    @Test
    void shouldAllowAdminToUpdateTrip() throws Exception {
        String adminToken = workerTokenWithRoles("admin", Set.of(RoleCompany.ADMIN));

        new TripApiTestClient(mockMvc, objectMapper).withToken(adminToken)
                .updateTrip(company.getUuid(), tripUuid, newVehicle.getUuid(), newDriver.getUuid())
                .andExpect(status().isOk());
    }

    @Test
    void shouldPersistUpdatedVehicleAndDriver() throws Exception {
        tripApi.updateTrip(company.getUuid(), tripUuid, newVehicle.getUuid(), newDriver.getUuid())
                .andExpect(status().isOk());

        tripApi.getTrip(tripUuid)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicle.uuid").value(newVehicle.getUuid().toString()))
                .andExpect(jsonPath("$.driver.uuid").value(newDriver.getUuid().toString()));
    }

    // ---------- validaciones del body ----------

    @Test
    void shouldReturnBadRequestWhenVehicleUuidIsNull() throws Exception {
        tripApi.updateTrip(company.getUuid(), tripUuid, null, newDriver.getUuid())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenDriverUuidIsNull() throws Exception {
        tripApi.updateTrip(company.getUuid(), tripUuid, newVehicle.getUuid(), null)
                .andExpect(status().isBadRequest());
    }

    // ---------- permisos ----------

    @Test
    void shouldForbidSellerToUpdateTrip() throws Exception {
        String sellerToken = workerTokenWithRoles("seller", Set.of(RoleCompany.SELLER));

        new TripApiTestClient(mockMvc, objectMapper).withToken(sellerToken)
                .updateTrip(company.getUuid(), tripUuid, newVehicle.getUuid(), newDriver.getUuid())
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldForbidUserWithoutCompanyRoleToUpdateTrip() throws Exception {
        UserDTO outsider = UserTestFactory.registerUser(mockMvc, "outsider", PASSWORD);
        String outsiderToken = UserTestFactory.login(mockMvc, outsider.getEmail(), PASSWORD).getToken();

        new TripApiTestClient(mockMvc, objectMapper).withToken(outsiderToken)
                .updateTrip(company.getUuid(), tripUuid, newVehicle.getUuid(), newDriver.getUuid())
                .andExpect(status().isForbidden());
    }

    // ---------- errores de negocio ----------

    @Test
    void shouldReturnNotFoundWhenTripDoesNotExist() throws Exception {
        tripApi.updateTrip(company.getUuid(), UUID.randomUUID(), newVehicle.getUuid(), newDriver.getUuid())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenVehicleDoesNotExist() throws Exception {
        tripApi.updateTrip(company.getUuid(), tripUuid, UUID.randomUUID(), newDriver.getUuid())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenDriverDoesNotExist() throws Exception {
        tripApi.updateTrip(company.getUuid(), tripUuid, newVehicle.getUuid(), UUID.randomUUID())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenWorkerIsNotDriver() throws Exception {
        UserDTO seller = UserTestFactory.registerUser(mockMvc, "sellerWorker", PASSWORD);
        new WorkerApiTestClient(mockMvc, objectMapper).withToken(ownerToken)
                .createWorker(company.getUuid(), seller.getUuid(), Set.of(RoleCompany.SELLER))
                .andExpect(status().isOk());

        tripApi.updateTrip(company.getUuid(), tripUuid, newVehicle.getUuid(), seller.getUuid())
                .andExpect(status().isBadRequest());
    }

    // ---------- helpers ----------

    private UUID createInitialTrip() throws Exception {
        TripCreate tripCreate = new TripCreate(
                route.getUuid(), LocalDateTime.now().plusDays(10),
                vehicle.getUuid(), driver.getUuid(), TypeTrip.TOUR, null);

        MvcResult result = tripApi.createTrip(company.getUuid(), tripCreate)
                .andExpect(status().isOk())
                .andReturn();

        String uuid = JsonPath.read(result.getResponse().getContentAsString(), "$.uuid");
        return UUID.fromString(uuid);
    }

    private VehicleDTO createVehicle(String plate, String model, Integer capacity) throws Exception {
        MvcResult result = new VehicleApiTestClient(mockMvc, objectMapper).withToken(ownerToken)
                .createVehicle(company.getUuid(), plate, model, capacity)
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), VehicleDTO.class);
    }

    private UserDTO createDriver(String name) throws Exception {
        UserDTO user = UserTestFactory.registerUser(mockMvc, name, PASSWORD);
        new WorkerApiTestClient(mockMvc, objectMapper).withToken(ownerToken)
                .createWorker(company.getUuid(), user.getUuid(), Set.of(RoleCompany.DRIVER))
                .andExpect(status().isOk());
        return user;
    }

    private String workerTokenWithRoles(String name, Set<RoleCompany> roles) throws Exception {
        UserDTO user = UserTestFactory.registerUser(mockMvc, name, PASSWORD);
        new WorkerApiTestClient(mockMvc, objectMapper).withToken(ownerToken)
                .createWorker(company.getUuid(), user.getUuid(), roles)
                .andExpect(status().isOk());
        LoginResponse login = UserTestFactory.login(mockMvc, user.getEmail(), PASSWORD);
        return login.getToken();
    }
}