package com.gastonnicora.trips.unit.bus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.entities.Bus;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.repositories.BusRepository;
import com.gastonnicora.trips.services.BusService;

@ExtendWith(MockitoExtension.class)
public class DeleteBusTest {

    @InjectMocks
    private BusService busService;

    @Mock
    private BusRepository busRepository;

    @Test
    void shouldDeleteBusSuccessfully() {
        Company company = new Company();
        company.setUuid(java.util.UUID.randomUUID());
        Bus bus = new Bus();
        bus.setActive(true);
        bus.setCompany(company);
        when(busRepository.findByCompanyUuidAndPlate(company.getUuid(), bus.getPlate())).thenReturn(Optional.of(bus));
        busService.deleteBus(company, bus.getPlate());
        verify(busRepository).save(bus);
        assertFalse(bus.isActive());
    }

    @Test
    void shouldNotDeleteBusIfNotFound() {
        Company company = new Company();
        company.setUuid(java.util.UUID.randomUUID());
        String plate = "ABC123";
        when(busRepository.findByCompanyUuidAndPlate(company.getUuid(), plate)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> busService.deleteBus(company, plate));
        verify(busRepository).findByCompanyUuidAndPlate(company.getUuid(), plate);
        verify(busRepository, never()).save(new Bus());
    }
}
