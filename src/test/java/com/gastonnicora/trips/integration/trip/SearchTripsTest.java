package com.gastonnicora.trips.integration.trip;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.entities.RouteDTO;
import com.gastonnicora.trips.dtos.entities.TripDTO;
import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.entities.VehicleDTO;
import com.gastonnicora.trips.dtos.request.trip.TripCreate;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.enums.TypeTrip;
import com.gastonnicora.trips.helpers.company.CompanyTestFactory;
import com.gastonnicora.trips.helpers.route.RouteTestFactory;
import com.gastonnicora.trips.helpers.routePoint.RoutePointApiTestClient;
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

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests de integración del endpoint {@code GET /api/trips/search}.
 *
 * <p>
 * La ruta de prueba es una recta sobre la latitud -34.90, de oeste a este, con
 * tres puntos. El viaje sale un día fijo (dentro de diez días) y se busca
 * sobre ese mismo día.
 * </p>
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SearchTripsTest {

    private static final String PASSWORD = "goodPassword";
    private static final double LAT = -34.90;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GeocodingService geocodingService;

    private TripApiTestClient tripApi;
    private LocalDate day;
    private TripDTO createdTrip;

    @BeforeEach
    void setup() throws Exception {
        UserDTO owner = UserTestFactory.registerUser(mockMvc, "owner", PASSWORD);
        String ownerToken = UserTestFactory.login(mockMvc, owner.getEmail(), PASSWORD).getToken();

        CompanyDTO company = new CompanyTestFactory(mockMvc, objectMapper, ownerToken, geocodingService)
                .createCompany();

        RouteDTO route = new RouteTestFactory(mockMvc, objectMapper, ownerToken)
                .createRoute(company.getUuid(), "Ruta de prueba", PricingType.PER_KM);

        RoutePointApiTestClient routePointApi = new RoutePointApiTestClient(mockMvc, objectMapper)
                .withToken(ownerToken);
        routePointApi.createRoutePoint(company.getUuid(), route.getUuid(), "Inicio", LAT, -58.00)
                .andExpect(status().isOk());
        routePointApi.createRoutePoint(company.getUuid(), route.getUuid(), "Medio", LAT, -57.95)
                .andExpect(status().isOk());
        routePointApi.createRoutePoint(company.getUuid(), route.getUuid(), "Fin", LAT, -57.90)
                .andExpect(status().isOk());

        VehicleDTO vehicle = new VehicleTestFactory(mockMvc, objectMapper, ownerToken)
                .createVehicle(company.getUuid());

        UserDTO driver = UserTestFactory.registerUser(mockMvc, "driver", PASSWORD);
        new WorkerApiTestClient(mockMvc, objectMapper).withToken(ownerToken)
                .createWorker(company.getUuid(), driver.getUuid(), Set.of(RoleCompany.DRIVER))
                .andExpect(status().isOk());

        tripApi = new TripApiTestClient(mockMvc, objectMapper).withToken(ownerToken);

        day = LocalDate.now().plusDays(10);
        TripCreate tripCreate = new TripCreate(
                route.getUuid(), day.atTime(8, 0), vehicle.getUuid(),
                driver.getUuid(), TypeTrip.TOUR, null);

        String json = tripApi.createTrip(company.getUuid(), tripCreate)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        createdTrip = objectMapper.readValue(json, TripDTO.class);
    }

    @Test
    void shouldReturnTripWhenRoutePassesOriginThenDestinationOnThatDay() throws Exception {
        tripApi.searchTrips(validParams())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].uuid").value(createdTrip.getUuid().toString()));
    }

    @Test
    void shouldReturnEmptyListWhenDirectionIsReversed() throws Exception {
        Map<String, String> params = validParams();
        params.put("originLongitude", "-57.91");
        params.put("destinationLongitude", "-57.99");

        tripApi.searchTrips(params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnEmptyListWhenOriginIsFartherThanMaxDistance() throws Exception {
        Map<String, String> params = validParams();
        params.put("originLatitude", "-34.95");

        tripApi.searchTrips(params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnEmptyListWhenNoTripsOnThatDay() throws Exception {
        Map<String, String> params = validParams();
        params.put("date", day.plusDays(1).toString());

        tripApi.searchTrips(params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnBadRequestWhenMaxDistanceIsNotPositive() throws Exception {
        Map<String, String> params = validParams();
        params.put("maxDistanceMeters", "0");

        tripApi.searchTrips(params)
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenOriginLatitudeIsMissing() throws Exception {
        Map<String, String> params = validParams();
        params.remove("originLatitude");

        tripApi.searchTrips(params)
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenLatitudeIsOutOfRange() throws Exception {
        Map<String, String> params = validParams();
        params.put("originLatitude", "91");

        tripApi.searchTrips(params)
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenDateIsInvalid() throws Exception {
        Map<String, String> params = validParams();
        params.put("date", "2026-13-01");

        tripApi.searchTrips(params)
                .andExpect(status().isBadRequest());
    }

    private Map<String, String> validParams() {
        Map<String, String> params = new HashMap<>();
        params.put("originLatitude", String.valueOf(LAT));
        params.put("originLongitude", "-57.99");
        params.put("destinationLatitude", String.valueOf(LAT));
        params.put("destinationLongitude", "-57.91");
        params.put("maxDistanceMeters", "1000");
        params.put("date", day.toString());
        return params;
    }
}