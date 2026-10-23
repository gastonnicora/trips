package com.gastonnicora.trips.services;

import com.gastonnicora.trips.dtos.entities.SectionPriceDTO;
import com.gastonnicora.trips.dtos.request.sectionPrice.SectionPriceCreate;
import com.gastonnicora.trips.dtos.request.sectionPrice.SectionPriceUpdate;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.entities.RoutePoint;
import com.gastonnicora.trips.entities.SectionPrice;
import com.gastonnicora.trips.exceptions.BadRequestException;
import com.gastonnicora.trips.exceptions.ConflictException;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.SectionPriceMapper;
import com.gastonnicora.trips.repositories.RouteRepository;
import com.gastonnicora.trips.repositories.SectionPriceRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SectionPriceService {

    private final SectionPriceRepository sectionPriceRepository;
    private final SectionPriceMapper sectionPriceMapper;
    private final RouteRepository routeRepository;

    public SectionPriceService(
            SectionPriceRepository sectionPriceRepository, SectionPriceMapper sectionPriceMapper,
            RouteRepository routeRepository
                              ) {
        this.sectionPriceRepository = sectionPriceRepository;
        this.sectionPriceMapper = sectionPriceMapper;
        this.routeRepository = routeRepository;
    }

    /**
     * Crea el precio de un tramo entre dos puntos de la ruta.
     *
     * <p>
     * Los tramos forman una cadena continua desde el inicio de la ruta: el primer
     * tramo debe comenzar en el primer punto y cada tramo siguiente debe comenzar
     * en el punto donde terminó el anterior. No se permiten tramos que se superpongan
     * con los existentes ni tramos duplicados.
     * </p>
     *
     * <p>
     * Si ya existe un tramo inactivo entre los mismos puntos, se reutiliza y se
     * reactiva, ya que la base de datos no permite duplicar el par de puntos.
     * </p>
     *
     * @param companyUuid        Identificador único de la empresa a la que pertenece la ruta.
     * @param routeUuid          Identificador único de la ruta.
     * @param sectionPriceCreate Datos del tramo que se desea crear.
     *
     * @return {@link SectionPriceDTO} correspondiente al tramo creado o reactivado.
     *
     * @throws NotFoundException   Si la ruta no existe o alguno de los puntos no pertenece a ella.
     * @throws ConflictException   Si la ruta no tiene puntos, el tramo ya está activo o se superpone con otro.
     * @throws BadRequestException Si el destino no está después del origen o el tramo no es continuo.
     */
    @Transactional
    public SectionPriceDTO createSectionPrice(UUID companyUuid, UUID routeUuid, SectionPriceCreate sectionPriceCreate) {
        Route route = routeRepository.findByUuidAndCompanyUuidAndCompanyActiveTrueAndActiveTrue(routeUuid, companyUuid)
                .orElseThrow(() -> new NotFoundException("Ruta no encontrada"));

        List<RoutePoint> points = route.getPoints();
        List<RoutePoint> activePoints = points.stream()
                .filter(RoutePoint::isActive)
                .toList();
        if (activePoints.isEmpty()) {
            throw new ConflictException("La ruta no tiene puntos");
        }

        RoutePoint fromPoint = findActivePoint(activePoints, sectionPriceCreate.getFromPoint());
        RoutePoint toPoint = findActivePoint(activePoints, sectionPriceCreate.getToPoint());

        int fromIndex = indexOfPoint(points, fromPoint);
        int toIndex = indexOfPoint(points, toPoint);
        if (toIndex <= fromIndex) {
            throw new BadRequestException("El punto de destino debe estar después del punto de origen");
        }

        List<SectionPrice> sectionPrices = route.getSectionPrices();

        SectionPrice existing = sectionPrices.stream()
                .filter(section -> section.getFromPoint().getUuid().equals(fromPoint.getUuid())
                        && section.getToPoint().getUuid().equals(toPoint.getUuid()))
                .findFirst()
                .orElse(null);
        if (existing != null && existing.isActive()) {
            throw new ConflictException("El tramo ya existe en el sistema");
        }

        List<SectionPrice> activeSections = sectionPrices.stream()
                .filter(SectionPrice::isActive)
                .toList();

        for (SectionPrice section : activeSections) {
            int sectionFromIndex = indexOfPoint(points, section.getFromPoint());
            int sectionToIndex = indexOfPoint(points, section.getToPoint());
            if (fromIndex < sectionToIndex && sectionFromIndex < toIndex) {
                throw new ConflictException("El tramo se superpone con un tramo existente");
            }
        }

        RoutePoint expectedStart = activeSections.isEmpty()
                ? activePoints.get(0)
                : activeSections.get(activeSections.size() - 1).getToPoint();
        if (!fromPoint.getUuid().equals(expectedStart.getUuid())) {
            throw new BadRequestException(
                    "El tramo no es continuo: debe comenzar en el punto donde termina el tramo anterior");
        }

        SectionPrice sectionPrice;
        if (existing != null) {
            sectionPrice = existing;
            sectionPrice.setPrice(sectionPriceCreate.getPrice());
            sectionPrice.setActive(true);
        } else {
            sectionPrice = new SectionPrice(route, fromPoint, toPoint, sectionPriceCreate.getPrice());
            route.addSectionPrice(sectionPrice);
        }

        sectionPrice = sectionPriceRepository.save(sectionPrice);
        routeRepository.save(route);
        return sectionPriceMapper.toDTO(sectionPrice);
    }

    /**
     * Busca un punto activo de la ruta por su identificador.
     *
     * @param activePoints Puntos activos de la ruta.
     * @param pointUuid    Identificador del punto buscado.
     *
     * @return punto encontrado.
     *
     * @throws NotFoundException Si el punto no está entre los puntos activos de la ruta.
     */
    private RoutePoint findActivePoint(List<RoutePoint> activePoints, UUID pointUuid) {
        return activePoints.stream()
                .filter(point -> point.getUuid().equals(pointUuid))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("El punto no pertenece a la ruta"));
    }


    /**
     * Obtiene el precio de un tramo activo de una ruta activa de una empresa activa.
     *
     * <p>
     * La búsqueda delega en
     * {@link SectionPriceRepository#findByUuidAndActiveTrueAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrue(UUID, UUID, UUID)}.
     * El tramo solo se devuelve si el tramo, su ruta y la empresa a la que pertenece
     * la ruta están activos, y si la ruta pertenece a la empresa indicada.
     * </p>
     *
     * @param companyUuid Identificador único de la empresa a la que pertenece la ruta.
     * @param routeUuid   Identificador único de la ruta a la que pertenece el tramo.
     * @param sectionUuid Identificador único del precio por tramo.
     *
     * @return {@link SectionPriceDTO} correspondiente al tramo encontrado.
     *
     * @throws NotFoundException Si el tramo no existe, está inactivo, o su ruta o
     *                           empresa no cumplen las condiciones indicadas.
     * @see SectionPriceMapper#toDTO(SectionPrice)
     */
    @Transactional
    public SectionPriceDTO getSectionPrice(UUID companyUuid, UUID routeUuid, UUID sectionUuid) {
        SectionPrice sectionPrice = sectionPriceRepository
                .findByUuidAndActiveTrueAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrue(
                        sectionUuid, companyUuid, routeUuid)
                .orElseThrow(() -> new NotFoundException("Tramo no encontrado"));
        return sectionPriceMapper.toDTO(sectionPrice);

    }


    /**
     * Obtiene los precios por tramo activos de una ruta activa de una empresa activa.
     *
     * <p>
     * La búsqueda delega en
     * {@link SectionPriceRepository#findByActiveTrueAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrue(UUID, UUID)}.
     * Solo se devuelven los tramos activos cuya ruta esté activa, pertenezca a la
     * empresa indicada y cuya empresa también esté activa.
     * </p>
     *
     * @param companyUuid Identificador único de la empresa a la que pertenece la ruta.
     * @param routeUuid   Identificador único de la ruta cuyos tramos se desean obtener.
     *
     * @return lista de {@link SectionPriceDTO} con los tramos activos de la ruta.
     *
     * @throws NotFoundException Si la ruta no tiene tramos activos, o si la ruta no
     *                           existe o no pertenece a la empresa indicada.
     * @see SectionPriceMapper#toDTOList(List)
     */
    @Transactional
    public List<SectionPriceDTO> getSectionPrices(UUID companyUuid, UUID routeUuid) {
        List<SectionPrice> sectionPrices = sectionPriceRepository.findByActiveTrueAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrue(companyUuid, routeUuid);
        if (sectionPrices.isEmpty()) {
            throw new NotFoundException("Tramo no encontrado");
        }
        return sectionPriceMapper.toDTOList(sectionPrices);
    }

    /**
     * Obtiene la posición de un punto dentro del orden de la ruta.
     *
     * @param points Puntos de la ruta, ordenados.
     * @param point  Punto cuya posición se desea conocer.
     *
     * @return índice del punto dentro de la lista.
     */
    private int indexOfPoint(List<RoutePoint> points, RoutePoint point) {
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).getUuid().equals(point.getUuid())) {
                return i;
            }
        }
        throw new IllegalStateException("El punto del tramo no pertenece a la ruta");
    }

    /**
     * Actualiza el precio de un tramo activo de una ruta activa de una empresa activa.
     *
     * <p>
     * El tramo se busca con
     * {@link SectionPriceRepository#findByUuidAndActiveTrueAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrue(UUID, UUID, UUID)},
     * por lo que solo se actualizan tramos activos, cuya ruta esté activa, que pertenezca
     * a la empresa indicada y cuya empresa también esté activa.
     * </p>
     *
     * <p>
     * Solo se modifica el precio. Los puntos de origen y destino no cambian, porque
     * definen la identidad del tramo dentro de la cadena de la ruta. Actualizar con el
     * mismo precio es válido y no produce cambios.
     * </p>
     *
     * @param companyUuid        Identificador único de la empresa a la que pertenece la ruta.
     * @param routeUuid          Identificador único de la ruta a la que pertenece el tramo.
     * @param sectionUuid        Identificador único del precio por tramo que se desea actualizar.
     * @param sectionPriceUpdate Nuevo precio del tramo ({@link SectionPriceUpdate}).
     *
     * @return {@link SectionPriceDTO} correspondiente al tramo actualizado.
     *
     * @throws NotFoundException Si el tramo no existe, está inactivo, o su ruta o empresa
     *                           no cumplen las condiciones indicadas.
     * @see SectionPriceMapper#toDTO(SectionPrice)
     */
    @Transactional
    public SectionPriceDTO updateSectionPrice(UUID companyUuid, UUID routeUuid, UUID sectionUuid,
                                              SectionPriceUpdate sectionPriceUpdate) {
        SectionPrice sectionPrice = sectionPriceRepository
                .findByUuidAndActiveTrueAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrue(
                        sectionUuid, companyUuid, routeUuid)
                .orElseThrow(() -> new NotFoundException("Tramo no encontrado"));

        sectionPrice.setPrice(sectionPriceUpdate.getPrice());
        return sectionPriceMapper.toDTO(sectionPriceRepository.save(sectionPrice));
    }

    /**
     * Da de baja el precio de un tramo de una ruta.
     *
     * <p>
     * La baja es lógica: el tramo no se elimina de la base de datos, sino que se
     * marca como inactivo. Solo puede darse de baja el último tramo activo de la
     * ruta, porque los tramos forman una cadena continua y dar de baja uno
     * intermedio la dejaría cortada. Los tramos inactivos que estén después del
     * último activo no se tienen en cuenta.
     * </p>
     *
     * <p>
     * El tramo se busca con
     * {@link SectionPriceRepository#findByUuidAndActiveTrueAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrue(UUID, UUID, UUID)},
     * por lo que solo se pueden dar de baja tramos activos, cuya ruta esté activa,
     * que pertenezca a la empresa indicada y cuya empresa también esté activa.
     * </p>
     *
     * @param companyUuid Identificador único de la empresa a la que pertenece la ruta.
     * @param routeUuid   Identificador único de la ruta a la que pertenece el tramo.
     * @param sectionUuid Identificador único del precio por tramo que se desea dar de baja.
     *
     * @throws NotFoundException Si el tramo no existe, está inactivo, o su ruta o
     *                           empresa no cumplen las condiciones indicadas.
     * @throws ConflictException Si el tramo no es el último tramo activo de la ruta.
     */
    @Transactional
    public void deleteSectionPrice(UUID companyUuid, UUID routeUuid, UUID sectionUuid) {
        SectionPrice sectionPrice = sectionPriceRepository
                .findByUuidAndActiveTrueAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrue(
                        sectionUuid, companyUuid, routeUuid)
                .orElseThrow(() -> new NotFoundException("Tramo no encontrado"));

        List<SectionPrice> activeSections = sectionPrice.getRoute().getSectionPrices().stream()
                .filter(SectionPrice::isActive)
                .toList();
        SectionPrice last = activeSections.get(activeSections.size() - 1);

        if (!last.getUuid().equals(sectionUuid)) {
            throw new ConflictException("Solo se puede dar de baja el último tramo de la ruta");
        }

        sectionPrice.setActive(false);
        sectionPriceRepository.save(sectionPrice);
    }

}