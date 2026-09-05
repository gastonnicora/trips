package com.gastonnicora.trips.unit.service.bus;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.dtos.entities.BusDTO;
import com.gastonnicora.trips.dtos.request.bus.BusCreate;
import com.gastonnicora.trips.entities.Bus;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.exceptions.ConflictException;
import com.gastonnicora.trips.mappers.BusMapper;
import com.gastonnicora.trips.repositories.BusRepository;
import com.gastonnicora.trips.services.BusService;
import com.gastonnicora.trips.services.CompanyService;

@ExtendWith(MockitoExtension.class)
class CreateBusTest {

    @InjectMocks
    private BusService busService;

    @Mock
    private BusRepository busRepository;

    @Mock
    private CompanyService companyService;

    @Mock
    private BusMapper busMapper;

    @Test
    void shouldCreateBusSuccessfully() {

        UUID companyUuid = UUID.randomUUID();
        UUID busUuid = UUID.randomUUID();

        Company company = new Company(
                "Test Company",
                "Buenos Aires, Argentina",
                -34.6037,
                -58.3816,
                "company@mail.com",
                "123456789"
        );
        company.setUuid(companyUuid);

        BusCreate busCreate = new BusCreate(
                "AA123BB",
                "Mercedes Benz",
                50
        );

        Bus bus = new Bus(
                company,
                busCreate.getPlate(),
                busCreate.getModel(),
                busCreate.getCapacity()
        );
        bus.setUuid(busUuid);

        BusDTO expectedDTO = new BusDTO();
        expectedDTO.setUuid(busUuid);

        when(companyService.getCompanyEntity(companyUuid))
                .thenReturn(company);

        when(busRepository.findByCompanyUuidAndPlate(
                companyUuid,
                busCreate.getPlate()
        )).thenReturn(Optional.empty());

        when(busRepository.save(any(Bus.class)))
                .thenReturn(bus);

        when(busMapper.toDTO(bus))
                .thenReturn(expectedDTO);

        BusDTO result = busService.createBus(
                companyUuid,
                busCreate
        );

        assertNotNull(result);
        assertEquals(expectedDTO, result);

        verify(companyService)
                .getCompanyEntity(companyUuid);

        verify(busRepository)
                .findByCompanyUuidAndPlate(
                        companyUuid,
                        busCreate.getPlate()
                );

        verify(busRepository)
                .save(any(Bus.class));

        verify(busMapper)
                .toDTO(bus);
    }

    @Test
    void shouldThrowConflictWhenPlateAlreadyExists() {

        UUID companyUuid = UUID.randomUUID();

        Company company = new Company(
                "Test Company",
                "Buenos Aires, Argentina",
                -34.6037,
                -58.3816,
                "company@mail.com",
                "123456789"
        );
        company.setUuid(companyUuid);

        BusCreate busCreate = new BusCreate(
                "AA123BB",
                "Mercedes Benz",
                50
        );

        Bus existingBus = new Bus(
                company,
                busCreate.getPlate(),
                busCreate.getModel(),
                busCreate.getCapacity()
        );

        when(companyService.getCompanyEntity(companyUuid))
                .thenReturn(company);

        when(busRepository.findByCompanyUuidAndPlate(
                companyUuid,
                busCreate.getPlate()
        )).thenReturn(Optional.of(existingBus));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> busService.createBus(
                        companyUuid,
                        busCreate
                )
        );

        assertEquals(
                "Ya existe un bus con la misma placa para esta empresa",
                exception.getMessage()
        );

        verify(companyService)
                .getCompanyEntity(companyUuid);

        verify(busRepository)
                .findByCompanyUuidAndPlate(
                        companyUuid,
                        busCreate.getPlate()
                );
    }

    @Test
    void shouldSaveBusWithCorrectData() {

        UUID companyUuid = UUID.randomUUID();

        Company company = new Company(
                "Test Company",
                "Buenos Aires, Argentina",
                -34.6037,
                -58.3816,
                "company@mail.com",
                "123456789"
        );
        company.setUuid(companyUuid);

        BusCreate busCreate = new BusCreate(
                "AA123BB",
                "Mercedes Benz",
                50
        );

        Bus savedBus = new Bus(
                company,
                busCreate.getPlate(),
                busCreate.getModel(),
                busCreate.getCapacity()
        );

        when(companyService.getCompanyEntity(companyUuid))
                .thenReturn(company);

        when(busRepository.findByCompanyUuidAndPlate(
                companyUuid,
                busCreate.getPlate()
        )).thenReturn(Optional.empty());

        when(busRepository.save(any(Bus.class)))
                .thenReturn(savedBus);

        when(busMapper.toDTO(savedBus))
                .thenReturn(new BusDTO());

        busService.createBus(companyUuid, busCreate);

        ArgumentCaptor<Bus> captor =
                ArgumentCaptor.forClass(Bus.class);

        verify(busRepository).save(captor.capture());

        Bus saved = captor.getValue();

        assertEquals(company, saved.getCompany());
        assertEquals(busCreate.getPlate(), saved.getPlate());
        assertEquals(busCreate.getModel(), saved.getModel());
        assertEquals(busCreate.getCapacity(), saved.getCapacity());
        assertEquals(true, saved.isActive());
    }
}
