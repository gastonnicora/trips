package com.gastonnicora.trips.unit.passage;

import com.gastonnicora.trips.dtos.request.passage.PassageCreate;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.Passage;
import com.gastonnicora.trips.entities.PricePerKilometer;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.entities.RoutePoint;
import com.gastonnicora.trips.entities.SectionPrice;
import com.gastonnicora.trips.entities.Trip;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.enums.TypeTrip;
import com.gastonnicora.trips.exceptions.BadRequestException;
import com.gastonnicora.trips.exceptions.ConflictException;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.PassageMapper;
import com.gastonnicora.trips.repositories.PassageRepository;
import com.gastonnicora.trips.repositories.TripRepository;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.services.PassageService;
import com.gastonnicora.trips.services.RoutingService;
import com.gastonnicora.trips.utils.GeoUtils;
import com.gastonnicora.trips.utils.GeoUtils.Coordinate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CreatePassageTest {

    private static final UUID TRIP_UUID = UUID.randomUUID();
    private static final UUID USER_UUID = UUID.randomUUID();
    private static final double LAT = -34.92;

    @Mock
    private PassageMapper passageMapper;
    @Mock
    private PassageRepository passageRepository;
    @Mock
    private TripRepository tripRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoutingService routingService;

    @Mock
    private Trip trip;
    @Mock
    private Route route;
    @Mock
    private Company company;
    @Mock
    private User user;

    private PassageService service;
    private List<RoutePoint> points;
    private List<Coordinate> waypoints;

    @BeforeEach
    void setUp() {
        service = new PassageService(passageMapper, passageRepository, tripRepository, userRepository, routingService);

        // Cuatro puntos sobre la misma latitud, con unos 0,9 km entre cada uno.
        points = List.of(point(-58.00), point(-57.99), point(-57.98), point(-57.97));
        waypoints = points.stream()
                .map(p -> new Coordinate(p.getLatitude(), p.getLongitude()))
                .toList();

        when(tripRepository.findByUuidAndActiveTrue(TRIP_UUID)).thenReturn(Optional.of(trip));
        when(userRepository.findByUuidAndEnabledTrue(USER_UUID)).thenReturn(Optional.of(user));

        when(trip.getRoute()).thenReturn(route);
        when(trip.getType()).thenReturn(TypeTrip.TOUR);

        when(route.isActive()).thenReturn(true);
        when(route.getCompany()).thenReturn(company);
        when(route.getPoints()).thenReturn(points);
        when(route.getUuid()).thenReturn(UUID.randomUUID());

        when(company.isActive()).thenReturn(true);

        when(routingService.getRoadPath(any(), anyList()))
                .thenReturn(new RoutingService.RoadPath(waypoints, true));
        when(passageRepository.save(any(Passage.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    // ---------- Precio por tramo ----------

    @Test
    void perSectionChargesEverySectionThePassengerCovers() {
        givenPricingType(PricingType.PER_SECTION);
        givenSections(
                section(0, 1, "1000.00"),
                section(1, 2, "2000.00"),
                section(2, 3, "3000.00")
                     );

        // Sube a mitad del primer tramo y baja a mitad del último: cubre los tres.
        service.createPassage(USER_UUID, request(-57.995, -57.975));

        assertThat(savedPrice()).isEqualByComparingTo("6000.00");
    }

    @Test
    void perSectionChargesOnlySectionsInsideThePassengerTrip() {
        givenPricingType(PricingType.PER_SECTION);
        givenSections(
                section(0, 1, "1000.00"),
                section(1, 2, "2000.00"),
                section(2, 3, "3000.00")
                     );

        // Sube y baja dentro del segundo tramo: solo se cobra ese.
        service.createPassage(USER_UUID, request(-57.985, -57.982));

        assertThat(savedPrice()).isEqualByComparingTo("2000.00");
    }

    @Test
    void perSectionRejectsPassengerWhenASectionIsMissing() {
        givenPricingType(PricingType.PER_SECTION);
        // Falta el tramo del medio (1 → 2), así que el recorrido tiene un hueco.
        givenSections(
                section(0, 1, "1000.00"),
                section(2, 3, "3000.00")
                     );

        assertThatThrownBy(() -> service.createPassage(USER_UUID, request(-57.995, -57.975)))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void perSectionIgnoresInactiveSections() {
        givenPricingType(PricingType.PER_SECTION);
        SectionPrice inactive = section(1, 2, "2000.00");
        when(inactive.isActive()).thenReturn(false);
        givenSections(
                section(0, 1, "1000.00"),
                inactive,
                section(2, 3, "3000.00")
                     );

        assertThatThrownBy(() -> service.createPassage(USER_UUID, request(-57.995, -57.975)))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void perSectionIgnoresSectionsWithInactivePoints() {
        givenPricingType(PricingType.PER_SECTION);

        RoutePoint inactivePoint = mock(RoutePoint.class);
        when(inactivePoint.getLatitude()).thenReturn(LAT);
        when(inactivePoint.getLongitude()).thenReturn(-57.98);
        when(inactivePoint.isActive()).thenReturn(false);

        givenSections(
                section(0, 1, "1000.00"),
                sectionTo(1, inactivePoint, "2000.00")
                     );

        // El pasajero llega hasta 1,5 tramos: el segundo tramo no se cobra porque su punto está inactivo.
        assertThatThrownBy(() -> service.createPassage(USER_UUID, request(-58.00, -57.985)))
                .isInstanceOf(ConflictException.class);
    }

    // ---------- Precio por kilómetro ----------

    @Test
    void perKmChargesBasePricePlusExtraKilometers() {
        givenPricingType(PricingType.PER_KM);
        givenPricePerKilometer(pricePerKilometer(true, "1", "1000", "500"));

        // Recorre dos tramos completos desde el inicio de la ruta.
        service.createPassage(USER_UUID, request(-58.00, -57.98));

        double meters = GeoUtils.distanceMeters(waypoints.get(0), waypoints.get(1))
                + GeoUtils.distanceMeters(waypoints.get(1), waypoints.get(2));
        BigDecimal km = BigDecimal.valueOf(meters / 1000.0);
        BigDecimal expected = new BigDecimal("1000")
                .add(km.subtract(new BigDecimal("1")).multiply(new BigDecimal("500")))
                .setScale(2, RoundingMode.HALF_UP);

        assertThat(savedPrice()).isEqualByComparingTo(expected);
    }

    @Test
    void perKmChargesOnlyBasePriceWhenTripIsWithinBaseKm() {
        givenPricingType(PricingType.PER_KM);
        givenPricePerKilometer(pricePerKilometer(true, "5", "1000", "500"));

        // Tramo de unos 0,9 km, dentro de los 5 km del precio base.
        service.createPassage(USER_UUID, request(-58.00, -57.99));

        assertThat(savedPrice()).isEqualByComparingTo("1000.00");
    }

    @Test
    void perKmRejectsRouteWithoutActivePricePerKilometer() {
        givenPricingType(PricingType.PER_KM);
        givenPricePerKilometer(pricePerKilometer(false, "1", "1000", "500"));

        assertThatThrownBy(() -> service.createPassage(USER_UUID, request(-58.00, -57.98)))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void perKmRejectsRouteWithoutPricePerKilometer() {
        givenPricingType(PricingType.PER_KM);
        givenPricePerKilometer(null);

        assertThatThrownBy(() -> service.createPassage(USER_UUID, request(-58.00, -57.98)))
                .isInstanceOf(ConflictException.class);
    }

    // ---------- Validaciones de existencia y estado ----------

    @Test
    void rejectsWhenTripDoesNotExist() {
        when(tripRepository.findByUuidAndActiveTrue(TRIP_UUID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createPassage(USER_UUID, request(-58.00, -57.98)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void rejectsWhenUserDoesNotExist() {
        when(userRepository.findByUuidAndEnabledTrue(USER_UUID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createPassage(USER_UUID, request(-58.00, -57.98)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void rejectsWhenRouteIsInactive() {
        when(route.isActive()).thenReturn(false);
        givenPricingType(PricingType.PER_SECTION);
        givenSections(section(0, 1, "1000.00"));

        assertThatThrownBy(() -> service.createPassage(USER_UUID, request(-58.00, -57.99)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void rejectsWhenCompanyIsInactive() {
        when(company.isActive()).thenReturn(false);
        givenPricingType(PricingType.PER_SECTION);
        givenSections(section(0, 1, "1000.00"));

        assertThatThrownBy(() -> service.createPassage(USER_UUID, request(-58.00, -57.99)))
                .isInstanceOf(NotFoundException.class);
    }

    // ---------- Validaciones de recorrido ----------

    @Test
    void rejectsTourPassengerFarFromRoute() {
        givenPricingType(PricingType.PER_SECTION);
        givenSections(section(0, 1, "1000.00"));

        // Unos 550 m al norte de la ruta, fuera de la tolerancia de 100 m de un TOUR.
        PassageCreate request = request(-58.00, -57.99);
        request.setFromLatitude(LAT + 0.005);

        assertThatThrownBy(() -> service.createPassage(USER_UUID, request))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void rejectsDomicileTripWithoutDistanceToRoute() {
        when(trip.getType()).thenReturn(TypeTrip.DOMICILE);
        when(trip.getDistanceToRoute()).thenReturn(null);
        givenPricingType(PricingType.PER_SECTION);
        givenSections(section(0, 1, "1000.00"));

        assertThatThrownBy(() -> service.createPassage(USER_UUID, request(-58.00, -57.99)))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void acceptsDomicilePassengerWithinDistanceToRoute() {
        when(trip.getType()).thenReturn(TypeTrip.DOMICILE);
        when(trip.getDistanceToRoute()).thenReturn(600);
        givenPricingType(PricingType.PER_SECTION);
        givenSections(section(0, 1, "1000.00"));

        // Unos 550 m al norte de la ruta: fuera de un TOUR, pero dentro de 600 m de desvío.
        PassageCreate request = request(-58.00, -57.99);
        request.setFromLatitude(LAT + 0.005);

        service.createPassage(USER_UUID, request);

        assertThat(savedPrice()).isEqualByComparingTo("1000.00");
    }

    @Test
    void rejectsDropOffBeforePickUp() {
        givenPricingType(PricingType.PER_SECTION);
        givenSections(section(0, 1, "1000.00"));

        assertThatThrownBy(() -> service.createPassage(USER_UUID, request(-57.98, -57.99)))
                .isInstanceOf(BadRequestException.class);
    }

    // ---------- Persistencia ----------

    @Test
    void savesPassageLinkedToTripAndUser() {
        givenPricingType(PricingType.PER_SECTION);
        givenSections(section(0, 1, "1000.00"));

        service.createPassage(USER_UUID, request(-58.00, -57.99));

        ArgumentCaptor<Passage> captor = ArgumentCaptor.forClass(Passage.class);
        verify(passageRepository).save(captor.capture());
        assertThat(captor.getValue().getTrip()).isSameAs(trip);
        assertThat(captor.getValue().getUser()).isSameAs(user);
        assertThat(captor.getValue().isActive()).isTrue();
    }

    // ---------- Helpers ----------

    /**
     * Los helpers reciben objetos ya construidos. Si se construyen dentro de
     * {@code thenReturn(...)}, Mockito detecta un stubbing sin cerrar.
     */
    private void givenPricingType(PricingType pricingType) {
        when(route.getPricingType()).thenReturn(pricingType);
    }

    private void givenSections(SectionPrice... sections) {
        when(route.getSectionPrices()).thenReturn(List.of(sections));
    }

    private void givenPricePerKilometer(PricePerKilometer pricePerKilometer) {
        when(route.getPricePerKilometer()).thenReturn(pricePerKilometer);
    }

    private RoutePoint point(double lon) {
        RoutePoint point = mock(RoutePoint.class);
        when(point.getLatitude()).thenReturn(LAT);
        when(point.getLongitude()).thenReturn(lon);
        when(point.isActive()).thenReturn(true);
        return point;
    }

    private SectionPrice section(int fromIndex, int toIndex, String price) {
        return sectionTo(fromIndex, points.get(toIndex), price);
    }

    private SectionPrice sectionTo(int fromIndex, RoutePoint toPoint, String price) {
        SectionPrice section = mock(SectionPrice.class);
        when(section.isActive()).thenReturn(true);
        when(section.getFromPoint()).thenReturn(points.get(fromIndex));
        when(section.getToPoint()).thenReturn(toPoint);
        when(section.getPrice()).thenReturn(new BigDecimal(price));
        return section;
    }

    private PricePerKilometer pricePerKilometer(boolean active, String baseKm, String basePrice, String pricePerKm) {
        PricePerKilometer ppk = mock(PricePerKilometer.class);
        when(ppk.isActive()).thenReturn(active);
        when(ppk.getBaseKm()).thenReturn(new BigDecimal(baseKm));
        when(ppk.getBasePrice()).thenReturn(new BigDecimal(basePrice));
        when(ppk.getPricePerKm()).thenReturn(new BigDecimal(pricePerKm));
        return ppk;
    }

    private PassageCreate request(double fromLon, double toLon) {
        PassageCreate request = new PassageCreate();
        request.setTripUuid(TRIP_UUID);
        request.setFromLatitude(LAT);
        request.setFromLongitude(fromLon);
        request.setToLatitude(LAT);
        request.setToLongitude(toLon);
        return request;
    }

    private BigDecimal savedPrice() {
        ArgumentCaptor<Passage> captor = ArgumentCaptor.forClass(Passage.class);
        verify(passageRepository).save(captor.capture());
        return captor.getValue().getPrice();
    }
}