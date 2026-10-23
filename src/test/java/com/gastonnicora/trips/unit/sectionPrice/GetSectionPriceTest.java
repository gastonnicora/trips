package com.gastonnicora.trips.unit.sectionPrice;

import com.gastonnicora.trips.dtos.entities.SectionPriceDTO;
import com.gastonnicora.trips.entities.SectionPrice;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.SectionPriceMapper;
import com.gastonnicora.trips.repositories.RouteRepository;
import com.gastonnicora.trips.repositories.SectionPriceRepository;
import com.gastonnicora.trips.services.SectionPriceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetSectionPriceTest {

    @InjectMocks
    private SectionPriceService sectionPriceService;

    @Mock
    private SectionPriceRepository sectionPriceRepository;

    @Mock
    private SectionPriceMapper sectionPriceMapper;

    @Mock
    private RouteRepository routeRepository;

    private UUID companyUuid;
    private UUID routeUuid;
    private UUID sectionUuid;

    private void givenIdentifiers() {
        companyUuid = UUID.randomUUID();
        routeUuid = UUID.randomUUID();
        sectionUuid = UUID.randomUUID();
    }

    private SectionPrice givenSectionPrice() {
        SectionPrice sectionPrice = new SectionPrice(null, null, null, BigDecimal.valueOf(1000));
        sectionPrice.setUuid(sectionUuid);
        sectionPrice.setActive(true);
        return sectionPrice;
    }

    @Test
    void shouldReturnSectionPriceWhenItExists() {

        givenIdentifiers();
        SectionPrice sectionPrice = givenSectionPrice();
        SectionPriceDTO expectedDTO = new SectionPriceDTO();

        // El repositorio recibe (uuid, companyUuid, routeUuid): el orden debe coincidir
        when(sectionPriceRepository
                     .findByUuidAndActiveTrueAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrue(
                             sectionUuid, companyUuid, routeUuid))
                .thenReturn(Optional.of(sectionPrice));
        when(sectionPriceMapper.toDTO(sectionPrice)).thenReturn(expectedDTO);

        SectionPriceDTO result = sectionPriceService.getSectionPrice(companyUuid, routeUuid, sectionUuid);

        assertSame(expectedDTO, result);
        verify(sectionPriceMapper).toDTO(sectionPrice);
    }

    @Test
    void shouldThrowNotFoundWhenSectionPriceDoesNotExist() {

        givenIdentifiers();

        when(sectionPriceRepository
                     .findByUuidAndActiveTrueAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrue(
                             sectionUuid, companyUuid, routeUuid))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> sectionPriceService.getSectionPrice(companyUuid, routeUuid, sectionUuid)
                                                  );

        assertEquals("Tramo no encontrado", exception.getMessage());
        verify(sectionPriceMapper, never()).toDTO(any(SectionPrice.class));
    }
}