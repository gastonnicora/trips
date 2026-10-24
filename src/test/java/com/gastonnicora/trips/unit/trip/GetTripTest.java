package com.gastonnicora.trips.unit.trip;

import com.gastonnicora.trips.dtos.entities.TripDTO;
import com.gastonnicora.trips.entities.Trip;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.TripMapper;
import com.gastonnicora.trips.repositories.TripRepository;
import com.gastonnicora.trips.services.TripsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetTripTest {

    @InjectMocks
    private TripsService tripsService;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private TripMapper tripMapper;

    @Test
    void shouldReturnTripDTOWhenTripIsActive() {
        UUID tripUuid = UUID.randomUUID();
        Trip trip = new Trip();
        TripDTO tripDTO = new TripDTO();

        when(tripRepository.findByUuidAndActiveTrue(tripUuid)).thenReturn(Optional.of(trip));
        when(tripMapper.toDTO(trip)).thenReturn(tripDTO);

        TripDTO result = tripsService.getTrip(tripUuid);

        assertSame(tripDTO, result);

        verify(tripRepository).findByUuidAndActiveTrue(tripUuid);
        verify(tripMapper).toDTO(trip);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenTripDoesNotExistOrIsInactive() {
        UUID tripUuid = UUID.randomUUID();

        when(tripRepository.findByUuidAndActiveTrue(tripUuid)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                                            () -> tripsService.getTrip(tripUuid));

        assertEquals("El viaje no existe", ex.getMessage());

        verify(tripRepository).findByUuidAndActiveTrue(tripUuid);
        verify(tripMapper, never()).toDTO(any(Trip.class));
    }
}