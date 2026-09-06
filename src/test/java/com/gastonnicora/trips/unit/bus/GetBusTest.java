package com.gastonnicora.trips.unit.bus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.dtos.entities.BusDTO;
import com.gastonnicora.trips.entities.Bus;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.BusMapper;
import com.gastonnicora.trips.repositories.BusRepository;
import com.gastonnicora.trips.services.BusService;

@ExtendWith(MockitoExtension.class)
public class GetBusTest {

    @InjectMocks
    private BusService busService;

    @Mock
    private BusRepository busRepository;

    @Mock
    private BusMapper busMapper;

    @Test
    void shouldGetBusSuccessfully() {
        Company company = new Company();
        company.setUuid(java.util.UUID.randomUUID());
        Bus bus = new Bus();
        bus.setActive(true);
        bus.setCompany(company);
        when(busRepository.findByUuid(bus.getUuid())).thenReturn(Optional.of(bus));
        BusDTO expectedBusDTO = new BusDTO();
        when(busMapper.toDTO(bus)).thenReturn(expectedBusDTO);
        BusDTO result = busService.getBus(bus.getUuid());
        verify(busRepository).findByUuid(bus.getUuid());
        assertEquals(expectedBusDTO, result);
        verify(busMapper).toDTO(bus);
    }

    @Test
    void shouldNotGetBusIfNotFound() {
        Company company = new Company();
        company.setUuid(java.util.UUID.randomUUID());
        Bus bus = new Bus();
        bus.setCompany(company);
        when(busRepository.findByUuid(bus.getUuid())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> busService.getBus(bus.getUuid()));
        verify(busRepository).findByUuid(bus.getUuid());
        verify(busMapper, never()).toDTO(any(Bus.class));
    }
}
