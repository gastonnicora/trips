package com.gastonnicora.trips.unit.sectionPrice;

import com.gastonnicora.trips.dtos.entities.SectionPriceDTO;
import com.gastonnicora.trips.dtos.request.sectionPrice.SectionPriceUpdate;
import com.gastonnicora.trips.entities.RoutePoint;
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
class UpdateSectionPriceTest {

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
    private RoutePoint from;
    private RoutePoint to;

    private void givenIdentifiers() {
        companyUuid = UUID.randomUUID();
        routeUuid = UUID.randomUUID();
        sectionUuid = UUID.randomUUID();
    }

    private RoutePoint givenPoint(String name) {
        RoutePoint point = new RoutePoint(name, -34.92, -57.95);
        point.setUuid(UUID.randomUUID());
        return point;
    }

    private SectionPrice givenSectionPrice(BigDecimal price) {
        from = givenPoint("A");
        to = givenPoint("B");
        SectionPrice sectionPrice = new SectionPrice(null, from, to, price);
        sectionPrice.setUuid(sectionUuid);
        sectionPrice.setActive(true);
        return sectionPrice;
    }

    private SectionPriceUpdate request(BigDecimal price) {
        return new SectionPriceUpdate(price);
    }

    private void givenSectionFound(SectionPrice sectionPrice) {
        // El repositorio recibe (uuid, companyUuid, routeUuid): el orden debe coincidir
        when(sectionPriceRepository
                     .findByUuidAndActiveTrueAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrue(
                             sectionUuid, companyUuid, routeUuid))
                .thenReturn(Optional.of(sectionPrice));
    }

    @Test
    void shouldUpdatePriceAndReturnSectionPrice() {

        givenIdentifiers();
        SectionPrice sectionPrice = givenSectionPrice(BigDecimal.valueOf(1000));
        givenSectionFound(sectionPrice);

        SectionPriceDTO expectedDTO = new SectionPriceDTO();
        when(sectionPriceRepository.save(any(SectionPrice.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(sectionPriceMapper.toDTO(sectionPrice)).thenReturn(expectedDTO);

        SectionPriceDTO result = sectionPriceService.updateSectionPrice(
                companyUuid, routeUuid, sectionUuid, request(BigDecimal.valueOf(1500)));

        assertSame(expectedDTO, result);
        assertEquals(BigDecimal.valueOf(1500), sectionPrice.getPrice());
        verify(sectionPriceRepository).save(sectionPrice);
    }

    @Test
    void shouldKeepPointsUnchangedWhenUpdatingPrice() {

        givenIdentifiers();
        SectionPrice sectionPrice = givenSectionPrice(BigDecimal.valueOf(1000));
        givenSectionFound(sectionPrice);

        when(sectionPriceRepository.save(any(SectionPrice.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(sectionPriceMapper.toDTO(any(SectionPrice.class))).thenReturn(new SectionPriceDTO());

        sectionPriceService.updateSectionPrice(
                companyUuid, routeUuid, sectionUuid, request(BigDecimal.valueOf(1500)));

        assertSame(from, sectionPrice.getFromPoint());
        assertSame(to, sectionPrice.getToPoint());
    }

    @Test
    void shouldAcceptSamePriceWithoutChangingIt() {

        givenIdentifiers();
        SectionPrice sectionPrice = givenSectionPrice(BigDecimal.valueOf(1000));
        givenSectionFound(sectionPrice);

        when(sectionPriceRepository.save(any(SectionPrice.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(sectionPriceMapper.toDTO(any(SectionPrice.class))).thenReturn(new SectionPriceDTO());

        assertDoesNotThrow(() -> sectionPriceService.updateSectionPrice(
                companyUuid, routeUuid, sectionUuid, request(BigDecimal.valueOf(1000))));

        assertEquals(BigDecimal.valueOf(1000), sectionPrice.getPrice());
    }

    @Test
    void shouldThrowNotFoundAndNotSaveWhenSectionDoesNotExist() {

        givenIdentifiers();

        when(sectionPriceRepository
                     .findByUuidAndActiveTrueAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrue(
                             sectionUuid, companyUuid, routeUuid))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> sectionPriceService.updateSectionPrice(
                        companyUuid, routeUuid, sectionUuid, request(BigDecimal.valueOf(1500)))
                                                  );

        assertEquals("Tramo no encontrado", exception.getMessage());
        verify(sectionPriceRepository, never()).save(any(SectionPrice.class));
        verify(sectionPriceMapper, never()).toDTO(any(SectionPrice.class));
    }
}