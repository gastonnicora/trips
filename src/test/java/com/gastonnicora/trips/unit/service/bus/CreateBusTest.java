package com.gastonnicora.trips.unit.service.bus;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gastonnicora.trips.dtos.entities.BusDTO;
import com.gastonnicora.trips.dtos.request.bus.BusCreate;
import com.gastonnicora.trips.entities.Bus;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.exceptions.ConflictException;
import com.gastonnicora.trips.mappers.BusMapper;
import com.gastonnicora.trips.repositories.BusRepository;
import com.gastonnicora.trips.services.BusService;
import com.gastonnicora.trips.services.CompanyService;

public class CreateBusTest {

    @InjectMocks
    private BusService busService;

    @Mock
    private CompanyService companyService;

    @Mock
    private BusMapper busMapper;

    @Mock
    private BusRepository busRepository;

    @Test
    void shouldCreateBusSuccessfully() {
        UUID companyId = UUID.randomUUID();
        BusCreate busCreate = new BusCreate();
        busCreate.setPlate("ABC123");
        busCreate.setModel("Model X");
        busCreate.setCapacity(50);

        Company company = new Company("Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@email.com", "123");
        company.setUuid(companyId);

        when(companyService.getCompanyEntity(companyId)).thenReturn(company);

        when(busRepository.findByCompanyUuidAndPlate(company.getUuid(), busCreate.getPlate())).thenReturn(Optional.empty());

        Bus bus = new Bus(company, busCreate.getPlate(), busCreate.getModel(), busCreate.getCapacity());
        when(busRepository.save(any(Bus.class))).thenReturn(bus);
        when(busMapper.toDTO(any())).thenReturn(new BusDTO());

        BusDTO result = busService.createBus(companyId, busCreate);

        assertEquals(result, new BusDTO());
        verify(busRepository).save(any(Bus.class));
        verify(busRepository).findByCompanyUuidAndPlate(company.getUuid(), busCreate.getPlate());
        verify(busMapper).toDTO(any(Bus.class));
        verify(companyService).getCompanyEntity(companyId);
    }

    @Test
    void shouldThrowConflictExceptionWhenBusAlreadyExists() {
        UUID companyId = UUID.randomUUID();
        BusCreate busCreate = new BusCreate();
        busCreate.setPlate("ABC123");
        busCreate.setModel("Model X");
        busCreate.setCapacity(50);

        Company company = new Company("Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@email.com", "123");
        company.setUuid(companyId);

        when(companyService.getCompanyEntity(companyId)).thenReturn(company);
        when(busRepository.findByCompanyUuidAndPlate(company.getUuid(), busCreate.getPlate()))
                .thenReturn(Optional.of(new Bus()));

        assertThrows(ConflictException.class, () -> busService.createBus(companyId, busCreate));

        verify(busRepository).findByCompanyUuidAndPlate(company.getUuid(), busCreate.getPlate());
        verify(companyService).getCompanyEntity(companyId);
        verify(busRepository, never()).save(any(Bus.class));
        verify(busMapper, never()).toDTO(any(Bus.class));

    }

}
