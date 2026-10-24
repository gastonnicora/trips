package com.gastonnicora.trips.unit.trip;

import com.gastonnicora.trips.dtos.entities.TripDTO;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.entities.RoutePoint;
import com.gastonnicora.trips.entities.Trip;
import com.gastonnicora.trips.enums.TypeTrip;
import com.gastonnicora.trips.exceptions.BadRequestException;
import com.gastonnicora.trips.mappers.TripMapper;
import com.gastonnicora.trips.repositories.TripRepository;
import com.gastonnicora.trips.services.TripsService;
import com.gastonnicora.trips.utils.GeoUtils.Coordinate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetTripsPassingBetweenTest {

    /**
     * Ruta de prueba: recta de oeste a este sobre la latitud -34.90, con tres puntos.
     */
    private static final double LAT = -34.90;
    private static final LocalDate DAY = LocalDate.of(2026, 11, 1);
    private static final double MAX_DISTANCE_METERS = 1000;

    @InjectMocks
    private TripsService tripsService;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private TripMapper tripMapper;

    @Test
    void shouldReturnTripWhenRoutePassesOriginThenDestination() {
        Trip trip = tripWithPoints(DAY, activePoint(-58.00), activePoint(-57.95), activePoint(-57.90));
        TripDTO tripDTO = new TripDTO();

        when(tripRepository.findAllByActiveTrueAndRouteActiveTrueAndRouteCompanyActiveTrueAndDateGreaterThanEqualAndDateLessThan(
                DAY.atStartOfDay(), DAY.plusDays(1).atStartOfDay()))
                .thenReturn(List.of(trip));
        when(tripMapper.toDTO(trip)).thenReturn(tripDTO);

        List<TripDTO> result = tripsService.getTripsPassingBetween(
                new Coordinate(LAT, -57.99), new Coordinate(LAT, -57.91), MAX_DISTANCE_METERS, DAY);

        assertEquals(List.of(tripDTO), result);

        verify(tripRepository).findAllByActiveTrueAndRouteActiveTrueAndRouteCompanyActiveTrueAndDateGreaterThanEqualAndDateLessThan(
                DAY.atStartOfDay(), DAY.plusDays(1).atStartOfDay());
        verify(tripMapper).toDTO(trip);
    }

    @Test
    void shouldExcludeTripWhenRouteIsTravelledInOppositeDirection() {
        Trip trip = tripWithPoints(DAY, activePoint(-58.00), activePoint(-57.95), activePoint(-57.90));

        when(tripRepository.findAllByActiveTrueAndRouteActiveTrueAndRouteCompanyActiveTrueAndDateGreaterThanEqualAndDateLessThan(
                any(), any()))
                .thenReturn(List.of(trip));

        List<TripDTO> result = tripsService.getTripsPassingBetween(
                new Coordinate(LAT, -57.91), new Coordinate(LAT, -57.99), MAX_DISTANCE_METERS, DAY);

        assertEquals(List.of(), result);
        verify(tripMapper, never()).toDTO(any(Trip.class));
    }

    @Test
    void shouldExcludeTripWhenOriginIsFartherThanMaxDistance() {
        Trip trip = tripWithPoints(DAY, activePoint(-58.00), activePoint(-57.95), activePoint(-57.90));

        when(tripRepository.findAllByActiveTrueAndRouteActiveTrueAndRouteCompanyActiveTrueAndDateGreaterThanEqualAndDateLessThan(
                any(), any()))
                .thenReturn(List.of(trip));

        // El origen está a unos 5,5 km del recorrido, por encima de la tolerancia de 1 km.
        List<TripDTO> result = tripsService.getTripsPassingBetween(
                new Coordinate(-34.95, -57.99), new Coordinate(LAT, -57.91), MAX_DISTANCE_METERS, DAY);

        assertEquals(List.of(), result);
        verify(tripMapper, never()).toDTO(any(Trip.class));
    }

    @Test
    void shouldExcludeTripWhenDestinationIsFartherThanMaxDistance() {
        Trip trip = tripWithPoints(DAY, activePoint(-58.00), activePoint(-57.95), activePoint(-57.90));

        when(tripRepository.findAllByActiveTrueAndRouteActiveTrueAndRouteCompanyActiveTrueAndDateGreaterThanEqualAndDateLessThan(
                any(), any()))
                .thenReturn(List.of(trip));

        List<TripDTO> result = tripsService.getTripsPassingBetween(
                new Coordinate(LAT, -57.99), new Coordinate(-34.95, -57.91), MAX_DISTANCE_METERS, DAY);

        assertEquals(List.of(), result);
        verify(tripMapper, never()).toDTO(any(Trip.class));
    }

    @Test
    void shouldExcludeTripWhenRouteHasNoActivePoints() {
        Trip trip = tripWithPoints(DAY, inactivePoint(-58.00), inactivePoint(-57.95), inactivePoint(-57.90));

        when(tripRepository.findAllByActiveTrueAndRouteActiveTrueAndRouteCompanyActiveTrueAndDateGreaterThanEqualAndDateLessThan(
                any(), any()))
                .thenReturn(List.of(trip));

        List<TripDTO> result = tripsService.getTripsPassingBetween(
                new Coordinate(LAT, -57.99), new Coordinate(LAT, -57.91), MAX_DISTANCE_METERS, DAY);

        assertEquals(List.of(), result);
        verify(tripMapper, never()).toDTO(any(Trip.class));
    }

    @Test
    void shouldReturnOnlyMatchingTripsWhenSeveralAreFound() {
        Trip matching = tripWithPoints(DAY, activePoint(-58.00), activePoint(-57.95), activePoint(-57.90));
        Trip opposite = tripWithPoints(DAY, activePoint(-57.90), activePoint(-57.95), activePoint(-58.00));
        TripDTO matchingDTO = new TripDTO();

        when(tripRepository.findAllByActiveTrueAndRouteActiveTrueAndRouteCompanyActiveTrueAndDateGreaterThanEqualAndDateLessThan(
                any(), any()))
                .thenReturn(List.of(matching, opposite));
        when(tripMapper.toDTO(matching)).thenReturn(matchingDTO);

        List<TripDTO> result = tripsService.getTripsPassingBetween(
                new Coordinate(LAT, -57.99), new Coordinate(LAT, -57.91), MAX_DISTANCE_METERS, DAY);

        assertEquals(List.of(matchingDTO), result);
        verify(tripMapper, never()).toDTO(opposite);
    }

    @Test
    void shouldReturnEmptyListWhenNoTripsOnThatDay() {
        when(tripRepository.findAllByActiveTrueAndRouteActiveTrueAndRouteCompanyActiveTrueAndDateGreaterThanEqualAndDateLessThan(
                any(), any()))
                .thenReturn(List.of());

        List<TripDTO> result = tripsService.getTripsPassingBetween(
                new Coordinate(LAT, -57.99), new Coordinate(LAT, -57.91), MAX_DISTANCE_METERS, DAY);

        assertEquals(List.of(), result);
        verify(tripMapper, never()).toDTO(any(Trip.class));
    }

    @Test
    void shouldThrowBadRequestWhenMaxDistanceIsNotPositive() {
        BadRequestException zero = assertThrows(BadRequestException.class,
                                                () -> tripsService.getTripsPassingBetween(
                                                        new Coordinate(LAT, -57.99), new Coordinate(LAT, -57.91),
                                                        0, DAY));
        BadRequestException negative = assertThrows(BadRequestException.class,
                                                    () -> tripsService.getTripsPassingBetween(
                                                            new Coordinate(LAT, -57.99), new Coordinate(LAT, -57.91),
                                                            -5, DAY));

        assertEquals("La distancia debe ser mayor a cero", zero.getMessage());
        assertEquals("La distancia debe ser mayor a cero", negative.getMessage());

        verify(tripRepository, never()).findAllByActiveTrueAndRouteActiveTrueAndRouteCompanyActiveTrueAndDateGreaterThanEqualAndDateLessThan(
                any(), any());
    }

    private static RoutePoint activePoint(double longitude) {
        return new RoutePoint("punto", LAT, longitude);
    }

    private static RoutePoint inactivePoint(double longitude) {
        RoutePoint point = new RoutePoint("punto", LAT, longitude);
        point.setActive(false);
        return point;
    }

    private static Trip tripWithPoints(LocalDate day, RoutePoint... points) {
        Route route = new Route(null, "Ruta de prueba", null);
        for (RoutePoint point : points) {
            route.addPoint(point);
        }
        return new Trip(route, LocalDateTime.of(day, java.time.LocalTime.of(8, 0)),
                        null, null, TypeTrip.TOUR, null);
    }
}