package com.gastonnicora.trips.integration.passage;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.entities.RouteDTO;
import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.entities.VehicleDTO;
import com.gastonnicora.trips.dtos.request.passage.PassageCreate;
import com.gastonnicora.trips.dtos.request.trip.TripCreate;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.enums.TypeTrip;
import com.gastonnicora.trips.helpers.company.CompanyTestFactory;
import com.gastonnicora.trips.helpers.passage.PassageApiTestClient;
import com.gastonnicora.trips.helpers.pricePerKilometer.PricePerKilometerApiTestClient;
import com.gastonnicora.trips.helpers.route.RouteTestFactory;
import com.gastonnicora.trips.helpers.routePoint.RoutePointApiTestClient;
import com.gastonnicora.trips.helpers.sectionPrice.SectionPriceApiTestClient;
import com.gastonnicora.trips.helpers.trip.TripApiTestClient;
import com.gastonnicora.trips.helpers.user.UserTestFactory;
import com.gastonnicora.trips.helpers.vehicle.VehicleTestFactory;
import com.gastonnicora.trips.helpers.worker.WorkerApiTestClient;
import com.gastonnicora.trips.services.GeocodingService;
import com.gastonnicora.trips.services.RoutingService;
import com.gastonnicora.trips.services.RoutingService.RoadPath;
import com.gastonnicora.trips.utils.GeoUtils;
import com.gastonnicora.trips.utils.GeoUtils.Coordinate;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests de integración del endpoint {@code POST /api/passages}.
 *
 * <p>
 * Hay dos rutas de prueba, sobre la misma recta de latitud -34.90, de oeste a este:
 * una cobrada por tramos (cuatro puntos y tres tramos con precio fijo) y otra
 * cobrada por kilómetro (dos puntos). Ambos viajes usan el mismo vehículo, pero en
 * fechas distintas, porque la base no permite dos viajes del mismo vehículo en el
 * mismo día. OSRM se reemplaza por la línea recta entre los puntos.
 * </p>
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CreatePassageTest {

    private static final String PASSWORD = "goodPassword";
    private static final double LAT = -34.90;
    private static final BigDecimal BASE_KM = new BigDecimal("1");
    private static final BigDecimal BASE_PRICE = new BigDecimal("1000");
    private static final BigDecimal PRICE_PER_KM = new BigDecimal("500");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GeocodingService geocodingService;

    @MockitoBean
    private RoutingService routingService;

    private CompanyDTO company;
    private String ownerToken;
    private UUID sectionTripUuid;
    private UUID perKmTripUuid;
    private UserDTO passenger;
    private PassageApiTestClient passengerApi;
    private PassageApiTestClient anonymousApi;
    private RoutePointApiTestClient routePointApi;
    private SectionPriceApiTestClient sectionPriceApi;
    private TripApiTestClient tripApi;
    private UUID vehicleUuid;
    private UUID driverUuid;

    @BeforeEach
    void setup() throws Exception {
        // Sin llamadas reales a OSRM: el recorrido es la línea recta entre los puntos.
        when(routingService.getRoadPath(any(), any()))
                .thenAnswer(invocation -> new RoadPath(invocation.getArgument(1), false));

        UserDTO owner = UserTestFactory.registerUser(mockMvc, "owner", PASSWORD);
        ownerToken = UserTestFactory.login(mockMvc, owner.getEmail(), PASSWORD).getToken();

        company = new CompanyTestFactory(mockMvc, objectMapper, ownerToken, geocodingService)
                .createCompany();

        RouteTestFactory routeFactory = new RouteTestFactory(mockMvc, objectMapper, ownerToken);
        routePointApi = new RoutePointApiTestClient(mockMvc, objectMapper).withToken(ownerToken);
        sectionPriceApi = new SectionPriceApiTestClient(mockMvc, objectMapper).withToken(ownerToken);
        tripApi = new TripApiTestClient(mockMvc, objectMapper).withToken(ownerToken);

        // Ruta por tramos: A(-58.00) - B(-57.99) - C(-57.98) - D(-57.97)
        RouteDTO sectionRoute = routeFactory.createRoute(company.getUuid(), "Por tramos", PricingType.PER_SECTION);
        UUID a = createPoint(sectionRoute.getUuid(), "A", -58.00);
        UUID b = createPoint(sectionRoute.getUuid(), "B", -57.99);
        UUID c = createPoint(sectionRoute.getUuid(), "C", -57.98);
        UUID d = createPoint(sectionRoute.getUuid(), "D", -57.97);
        createSection(sectionRoute.getUuid(), a, b, "1000.00");
        createSection(sectionRoute.getUuid(), b, c, "2000.00");
        createSection(sectionRoute.getUuid(), c, d, "3000.00");

        // Ruta por kilómetro: dos puntos, A(-58.00) - D(-57.97)
        RouteDTO perKmRoute = routeFactory.createRoute(company.getUuid(), "Por kilómetro", PricingType.PER_KM);
        createPoint(perKmRoute.getUuid(), "A", -58.00);
        createPoint(perKmRoute.getUuid(), "D", -57.97);
        new PricePerKilometerApiTestClient(mockMvc, objectMapper).withToken(ownerToken)
                .createPricePerKilometer(company.getUuid(), perKmRoute.getUuid(), BASE_KM, BASE_PRICE, PRICE_PER_KM)
                .andExpect(status().isOk());

        VehicleDTO vehicle = new VehicleTestFactory(mockMvc, objectMapper, ownerToken)
                .createVehicle(company.getUuid());
        vehicleUuid = vehicle.getUuid();

        UserDTO driver = UserTestFactory.registerUser(mockMvc, "driver", PASSWORD);
        driverUuid = driver.getUuid();
        new WorkerApiTestClient(mockMvc, objectMapper).withToken(ownerToken)
                .createWorker(company.getUuid(), driverUuid, Set.of(RoleCompany.DRIVER))
                .andExpect(status().isOk());

        // Mismo vehículo, fechas distintas: evita la restricción (vehículo, fecha).
        LocalDate day = LocalDate.now().plusDays(10);
        sectionTripUuid = createTrip(sectionRoute.getUuid(), day);
        perKmTripUuid = createTrip(perKmRoute.getUuid(), day.plusDays(1));

        passenger = UserTestFactory.registerUser(mockMvc, "passenger", PASSWORD);
        String passengerToken = UserTestFactory.login(mockMvc, passenger.getEmail(), PASSWORD).getToken();
        passengerApi = new PassageApiTestClient(mockMvc, objectMapper).withToken(passengerToken);
        anonymousApi = new PassageApiTestClient(mockMvc, objectMapper);
    }

    // ---------- Cobro por tramo ----------

    @Test
    void shouldCreatePassageChargingEverySectionThePassengerCovers() throws Exception {
        // Sube a mitad del primer tramo y baja a mitad del último: cubre los tres.
        passengerApi.createPassage(request(sectionTripUuid, -57.995, -57.975))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").exists())
                .andExpect(jsonPath("$.trip.uuid").value(sectionTripUuid.toString()))
                .andExpect(jsonPath("$.user.uuid").value(passenger.getUuid().toString()))
                .andExpect(jsonPath("$.price").value(6000.0))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldChargeOnlySectionInsideThePassengerTrip() throws Exception {
        // Sube y baja dentro del segundo tramo.
        passengerApi.createPassage(request(sectionTripUuid, -57.985, -57.982))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(2000.0));
    }

    // ---------- Cobro por kilómetro ----------

    @Test
    void shouldChargeBasePricePlusExtraKilometers() throws Exception {
        double meters = GeoUtils.distanceMeters(new Coordinate(LAT, -58.00), new Coordinate(LAT, -57.97));
        BigDecimal km = BigDecimal.valueOf(meters / 1000.0);
        BigDecimal expected = BASE_PRICE
                .add(km.subtract(BASE_KM).multiply(PRICE_PER_KM))
                .setScale(2, RoundingMode.HALF_UP);

        passengerApi.createPassage(request(perKmTripUuid, -58.00, -57.97))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(expected.doubleValue()));
    }

    // ---------- Validaciones ----------

    @Test
    void shouldRejectUnauthenticatedRequest() throws Exception {
        anonymousApi.createPassage(request(sectionTripUuid, -57.995, -57.975))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnNotFoundWhenTripDoesNotExist() throws Exception {
        passengerApi.createPassage(request(UUID.randomUUID(), -57.995, -57.975))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenTripIsMissing() throws Exception {
        PassageCreate request = request(sectionTripUuid, -57.995, -57.975);
        request.setTripUuid(null);

        passengerApi.createPassage(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenLatitudeIsOutOfRange() throws Exception {
        PassageCreate request = request(sectionTripUuid, -57.995, -57.975);
        request.setFromLatitude(-95.0);

        passengerApi.createPassage(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenPassengerIsFarFromTourRoute() throws Exception {
        // Unos 550 m al norte de la ruta, fuera de la tolerancia de 100 m de un TOUR.
        PassageCreate request = request(sectionTripUuid, -57.995, -57.975);
        request.setFromLatitude(LAT + 0.005);

        passengerApi.createPassage(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenDropOffIsBeforePickUp() throws Exception {
        passengerApi.createPassage(request(sectionTripUuid, -57.975, -57.995))
                .andExpect(status().isBadRequest());
    }

    // ---------- Helpers ----------

    private PassageCreate request(UUID tripUuid, double fromLon, double toLon) {
        PassageCreate request = new PassageCreate();
        request.setTripUuid(tripUuid);
        request.setFromLatitude(LAT);
        request.setFromLongitude(fromLon);
        request.setToLatitude(LAT);
        request.setToLongitude(toLon);
        return request;
    }

    private UUID createPoint(UUID routeUuid, String name, double lon) throws Exception {
        String json = routePointApi.createRoutePoint(company.getUuid(), routeUuid, name, LAT, lon)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return UUID.fromString(objectMapper.readTree(json).get("uuid").asString());
    }

    private void createSection(UUID routeUuid, UUID from, UUID to, String price) throws Exception {
        sectionPriceApi.createSectionPrice(company.getUuid(), routeUuid, from, to, new BigDecimal(price))
                .andExpect(status().isOk());
    }

    private UUID createTrip(UUID routeUuid, LocalDate date) throws Exception {
        TripCreate tripCreate = new TripCreate(
                routeUuid, date.atTime(8, 0), vehicleUuid, driverUuid, TypeTrip.TOUR, null);

        String json = tripApi.createTrip(company.getUuid(), tripCreate)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return UUID.fromString(objectMapper.readTree(json).get("uuid").asString());
    }
}