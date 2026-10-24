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
import com.gastonnicora.trips.helpers.vehicle.VehicleTestFactory;
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
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CreateTripTest {

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
    private TripApiTestClient tripApi;

    @BeforeEach
    void setup() throws Exception {
        UserDTO owner = UserTestFactory.registerUser(mockMvc, "owner", PASSWORD);
        LoginResponse ownerLogin = UserTestFactory.login(mockMvc, owner.getEmail(), PASSWORD);
        ownerToken = ownerLogin.getToken();

        company = new CompanyTestFactory(mockMvc, objectMapper, ownerToken, geocodingService).createCompany();

        route = new RouteTestFactory(mockMvc, objectMapper, ownerToken)
                .createRoute(company.getUuid(), "Ruta de prueba", PricingType.PER_KM);

        vehicle = new VehicleTestFactory(mockMvc, objectMapper, ownerToken)
                .createVehicle(company.getUuid());

        driver = UserTestFactory.registerUser(mockMvc, "driver", PASSWORD);
        new WorkerApiTestClient(mockMvc, objectMapper).withToken(ownerToken)
                .createWorker(company.getUuid(), driver.getUuid(), Set.of(RoleCompany.DRIVER))
                .andExpect(status().isOk());

        tripApi = new TripApiTestClient(mockMvc, objectMapper).withToken(ownerToken);
    }

    @Test
    void shouldAllowOwnerToCreateTour() throws Exception {
        tripApi.createTrip(company.getUuid(), tripCreate(TypeTrip.TOUR, null, futureDate()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.route.uuid").value(route.getUuid().toString()))
                .andExpect(jsonPath("$.vehicle.uuid").value(vehicle.getUuid().toString()))
                .andExpect(jsonPath("$.driver.uuid").value(driver.getUuid().toString()))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldRejectDomicileTripWithoutDistance() throws Exception {
        tripApi.createTrip(company.getUuid(), tripCreate(TypeTrip.DOMICILE, null, futureDate()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectTourTripWithDistance() throws Exception {
        tripApi.createTrip(company.getUuid(), tripCreate(TypeTrip.TOUR, 5, futureDate()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectPastDate() throws Exception {
        tripApi.createTrip(company.getUuid(), tripCreate(TypeTrip.TOUR, null, LocalDateTime.now().minusDays(1)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectMalformedJson() throws Exception {
        tripApi.createTripRaw(company.getUuid(), "{ esto no es json")
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectWorkerWithoutDriverRole() throws Exception {
        UserDTO seller = UserTestFactory.registerUser(mockMvc, "seller", PASSWORD);
        new WorkerApiTestClient(mockMvc, objectMapper).withToken(ownerToken)
                .createWorker(company.getUuid(), seller.getUuid(), Set.of(RoleCompany.SELLER))
                .andExpect(status().isOk());

        TripCreate tripCreate = new TripCreate(
                route.getUuid(), futureDate(), vehicle.getUuid(), seller.getUuid(), TypeTrip.TOUR, null);

        tripApi.createTrip(company.getUuid(), tripCreate)
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldForbidUserWithoutOwnerOrAdminRole() throws Exception {
        UserDTO outsider = UserTestFactory.registerUser(mockMvc, "outsider", PASSWORD);
        LoginResponse outsiderLogin = UserTestFactory.login(mockMvc, outsider.getEmail(), PASSWORD);

        new TripApiTestClient(mockMvc, objectMapper).withToken(outsiderLogin.getToken())
                .createTrip(company.getUuid(), tripCreate(TypeTrip.TOUR, null, futureDate()))
                .andExpect(status().isForbidden());
    }

    // ---------- helpers ----------

    private LocalDateTime futureDate() {
        return LocalDateTime.now().plusDays(10);
    }

    private TripCreate tripCreate(TypeTrip type, Integer distanceToRoute, LocalDateTime date) {
        return new TripCreate(route.getUuid(), date, vehicle.getUuid(), driver.getUuid(), type, distanceToRoute);
    }
}