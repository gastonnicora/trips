package com.gastonnicora.trips.integration.trip;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.entities.RouteDTO;
import com.gastonnicora.trips.dtos.entities.TripDTO;
import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.entities.VehicleDTO;
import com.gastonnicora.trips.dtos.request.trip.TripCreate;
import com.gastonnicora.trips.dtos.response.auth.LoginResponse;
import com.gastonnicora.trips.entities.Trip;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.enums.TypeTrip;
import com.gastonnicora.trips.helpers.company.CompanyTestFactory;
import com.gastonnicora.trips.helpers.route.RouteTestFactory;
import com.gastonnicora.trips.helpers.trip.TripApiTestClient;
import com.gastonnicora.trips.helpers.user.UserTestFactory;
import com.gastonnicora.trips.helpers.vehicle.VehicleTestFactory;
import com.gastonnicora.trips.helpers.worker.WorkerApiTestClient;
import com.gastonnicora.trips.repositories.TripRepository;
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
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GetTripTest {

    private static final String PASSWORD = "goodPassword";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TripRepository tripRepository;

    @MockitoBean
    private GeocodingService geocodingService;

    private String ownerToken;
    private CompanyDTO company;
    private TripDTO createdTrip;
    private TripApiTestClient tripApi;

    @BeforeEach
    void setup() throws Exception {
        UserDTO owner = UserTestFactory.registerUser(mockMvc, "owner", PASSWORD);
        LoginResponse ownerLogin = UserTestFactory.login(mockMvc, owner.getEmail(), PASSWORD);
        ownerToken = ownerLogin.getToken();

        company = new CompanyTestFactory(mockMvc, objectMapper, ownerToken, geocodingService).createCompany();

        RouteDTO route = new RouteTestFactory(mockMvc, objectMapper, ownerToken)
                .createRoute(company.getUuid(), "Ruta de prueba", PricingType.PER_KM);

        VehicleDTO vehicle = new VehicleTestFactory(mockMvc, objectMapper, ownerToken)
                .createVehicle(company.getUuid());

        UserDTO driver = UserTestFactory.registerUser(mockMvc, "driver", PASSWORD);
        new WorkerApiTestClient(mockMvc, objectMapper).withToken(ownerToken)
                .createWorker(company.getUuid(), driver.getUuid(), Set.of(RoleCompany.DRIVER))
                .andExpect(status().isOk());

        tripApi = new TripApiTestClient(mockMvc, objectMapper).withToken(ownerToken);

        TripCreate tripCreate = new TripCreate(
                route.getUuid(), LocalDateTime.now().plusDays(10), vehicle.getUuid(),
                driver.getUuid(), TypeTrip.TOUR, null);

        String json = tripApi.createTrip(company.getUuid(), tripCreate)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        createdTrip = objectMapper.readValue(json, TripDTO.class);
    }

    @Test
    void shouldReturnActiveTrip() throws Exception {
        tripApi.getTrip(createdTrip.getUuid())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(createdTrip.getUuid().toString()))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldReturnNotFoundWhenTripDoesNotExist() throws Exception {
        tripApi.getTrip(UUID.randomUUID())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenTripIsInactive() throws Exception {
        Trip trip = tripRepository.findById(createdTrip.getUuid()).orElseThrow();
        trip.setActive(false);
        tripRepository.saveAndFlush(trip);

        tripApi.getTrip(createdTrip.getUuid())
                .andExpect(status().isNotFound());
    }

}