package com.gastonnicora.trips.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gastonnicora.trips.utils.GeoUtils.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Servicio encargado de calcular el recorrido por caminos reales entre los
 * puntos de una ruta.
 *
 * <p>
 * Consulta el servicio de ruteo OSRM ({@code /route/v1/driving}) para obtener la
 * geometría que siguen las calles entre cada par de puntos consecutivos. El
 * resultado se guarda en memoria por ruta, de modo que solo se consulta OSRM la
 * primera vez o cuando cambian los puntos de la ruta.
 * </p>
 *
 * <p>
 * Si OSRM no responde o devuelve un error, se usa la línea recta entre los
 * puntos como alternativa, marcada con {@link RoadPath#roadBased()} en
 * {@code false}. Esa alternativa no se guarda en caché, para que la siguiente
 * consulta vuelva a intentar obtener el recorrido real.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-10-25
 */
@Service
public class RoutingService {

    private static final Logger log = LoggerFactory.getLogger(RoutingService.class);

    /**
     * Cantidad máxima de puntos por consulta a OSRM. Si la ruta tiene más, se
     * pide en tramos que comparten el punto de cada extremo.
     */
    private static final int MAX_WAYPOINTS_PER_REQUEST = 50;

    /**
     * Cliente HTTP utilizado para consultar el servicio de ruteo.
     */
    private final RestClient restClient;

    /**
     * Recorridos ya calculados, indexados por el UUID de la ruta.
     */
    private final Map<UUID, CachedRoadPath> cache = new ConcurrentHashMap<>();

    /**
     * Crea una instancia del servicio de ruteo.
     *
     * <p>
     * El cliente se configura con 3 segundos para conectar y 10 segundos para
     * recibir la respuesta.
     * </p>
     *
     * @param baseUrl URL base del servicio OSRM, tomada de la propiedad
     *                {@code routing.osrm.base-url}.
     */
    public RoutingService(@Value("${routing.osrm.base-url}") String baseUrl) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(10));

        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(baseUrl)
                .defaultHeader("Accept-Encoding", "identity")
                .build();
    }

    /**
     * Recorrido de una ruta a lo largo de caminos.
     *
     * @param geometry   Puntos del recorrido, en orden. Su forma es la misma que
     *                   la de los puntos de una ruta, por lo que puede usarse con
     *                   {@link com.gastonnicora.trips.utils.GeoUtils}.
     * @param roadBased  {@code true} si la geometría sigue caminos reales;
     *                   {@code false} si es la línea recta entre los puntos de
     *                   la ruta (alternativa cuando OSRM no está disponible).
     */
    public record RoadPath(List<Coordinate> geometry, boolean roadBased) {

        public RoadPath {
            geometry = List.copyOf(geometry);
        }
    }

    /**
     * Obtiene el recorrido por caminos reales que une los puntos de una ruta, en
     * el orden indicado.
     *
     * <p>
     * Si el recorrido ya está en caché y los puntos no cambiaron, lo devuelve sin
     * consultar OSRM. Con menos de dos puntos no hay tramos que calcular y se
     * devuelven los puntos tal cual, sin marcarlos como caminos reales.
     * </p>
     *
     * @param routeUuid UUID de la ruta, usado como clave de caché. Si es
     *                  {@code null}, el recorrido no se guarda en caché.
     * @param waypoints Puntos de la ruta, en orden.
     *
     * @return {@link RoadPath} con la geometría del recorrido.
     */
    public RoadPath getRoadPath(UUID routeUuid, List<Coordinate> waypoints) {
        List<Coordinate> points = List.copyOf(waypoints);
        if (points.size() < 2) {
            return new RoadPath(points, false);
        }

        CachedRoadPath cached = routeUuid == null ? null : cache.get(routeUuid);
        if (cached != null && cached.waypoints().equals(points)) {
            return cached.path();
        }

        List<Coordinate> geometry = fetchGeometry(points);
        if (geometry == null) {
            return new RoadPath(points, false);
        }

        RoadPath path = new RoadPath(geometry, true);
        if (routeUuid != null) {
            cache.put(routeUuid, new CachedRoadPath(points, path));
        }
        return path;
    }

    /**
     * Pide a OSRM la geometría de todos los tramos, dividiendo la consulta si la
     * ruta tiene más puntos que {@link #MAX_WAYPOINTS_PER_REQUEST}.
     *
     * @return la geometría completa, o {@code null} si alguna consulta falló.
     */
    private List<Coordinate> fetchGeometry(List<Coordinate> waypoints) {
        List<Coordinate> geometry = new ArrayList<>();
        int start = 0;
        while (start < waypoints.size() - 1) {
            int end = Math.min(start + MAX_WAYPOINTS_PER_REQUEST - 1, waypoints.size() - 1);
            List<Coordinate> leg = requestRoute(waypoints.subList(start, end + 1));
            if (leg == null) {
                return null;
            }
            // El primer punto de cada tramo es el último del tramo anterior: no se repite.
            geometry.addAll(start == 0 ? leg : leg.subList(1, leg.size()));
            start = end;
        }
        return geometry;
    }

    /**
     * Consulta a OSRM el recorrido que une una secuencia de puntos.
     *
     * @return los puntos del recorrido, o {@code null} si OSRM no devolvió una ruta.
     */
    private List<Coordinate> requestRoute(List<Coordinate> waypoints) {
        // OSRM recibe las coordenadas como longitud, latitud separadas por '; '.
        String coordinates = waypoints.stream()
                .map(point -> point.longitude() + "," + point.latitude())
                .collect(Collectors.joining(";"));
        try {
            OsrmResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/route/v1/driving/" + coordinates)
                            .queryParam("overview", "full")
                            .queryParam("geometries", "geojson")
                            .queryParam("steps", "false")
                            .build())
                    .retrieve()
                    .body(OsrmResponse.class);

            if (response == null || !"Ok".equals(response.code())
                    || response.routes() == null || response.routes().isEmpty()) {
                log.warn("OSRM no devolvió una ruta (código: {})", response == null ? null : response.code());
                return null;
            }
            // GeoJSON entrega cada punto como [longitud, latitud].
            return response.routes().getFirst().geometry().coordinates().stream()
                    .map(pair -> new Coordinate(pair.get(1), pair.getFirst()))
                    .toList();
        } catch (RestClientException | IllegalArgumentException ex) {
            log.warn("No se pudo obtener la ruta por caminos reales desde OSRM", ex);
            return null;
        }
    }

    /**
     * Recorrido guardado en caché junto con los puntos de la ruta con los que se
     * calculó. Si la ruta cambia, los puntos no coinciden y el recorrido se
     * recalcula.
     */
    private record CachedRoadPath(List<Coordinate> waypoints, RoadPath path) {
    }

    /**
     * Respuesta del endpoint {@code /route/v1/driving} de OSRM. Solo se mapean los
     * campos que se usan.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OsrmResponse(String code, List<OsrmRoute> routes) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OsrmRoute(GeoJsonLineString geometry) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GeoJsonLineString(List<List<Double>> coordinates) {
    }
}
