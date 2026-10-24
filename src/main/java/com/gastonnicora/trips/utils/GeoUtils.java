package com.gastonnicora.trips.utils;

import java.util.List;

/**
 * Utilidades geográficas para trabajar con coordenadas y rutas.
 *
 * <p>
 * Permite calcular la distancia entre dos coordenadas y el punto más cercano
 * de una polilínea (la ruta formada por sus puntos ordenados), indicando a lo
 * largo de qué parte de la ruta se encuentra ese punto.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-10-24
 */
public final class GeoUtils {

    private static final double EARTH_RADIUS_METERS = 6_371_000.0;

    private GeoUtils() {
    }

    /**
     * Coordenada geográfica expresada en grados decimales.
     *
     * @param latitude  Latitud en grados.
     * @param longitude Longitud en grados.
     */
    public record Coordinate(double latitude, double longitude) {
    }

    /**
     * Resultado de buscar el punto más cercano de una ruta a una coordenada.
     *
     * @param progress       Posición del punto más cercano a lo largo de la ruta:
     *                       la parte entera es el índice del punto de inicio del
     *                       tramo y la parte decimal es la fracción recorrida de
     *                       ese tramo. Permite comparar el orden de dos puntos
     *                       sobre la misma ruta.
     * @param distanceMeters Distancia en metros entre la coordenada y el punto
     *                       más cercano de la ruta.
     */
    public record RouteMatch(double progress, double distanceMeters) {
    }

    /**
     * Calcula la distancia en metros entre dos coordenadas usando la fórmula de
     * Haversine.
     *
     * @param a Primera coordenada.
     * @param b Segunda coordenada.
     *
     * @return distancia en metros.
     */
    public static double distanceMeters(Coordinate a, Coordinate b) {
        double lat1 = Math.toRadians(a.latitude());
        double lat2 = Math.toRadians(b.latitude());
        double dLat = lat2 - lat1;
        double dLon = Math.toRadians(b.longitude() - a.longitude());

        double h = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * EARTH_RADIUS_METERS * Math.asin(Math.min(1.0, Math.sqrt(h)));
    }

    /**
     * Busca el punto de la polilínea más cercano a la coordenada indicada.
     *
     * <p>
     * Evalúa cada tramo (segmento entre dos puntos consecutivos) proyectando la
     * coordenada sobre él, sin salirse de sus extremos, y se queda con el tramo
     * que quede más cerca.
     * </p>
     *
     * @param target   Coordenada a buscar sobre la ruta.
     * @param polyline Puntos de la ruta, en orden.
     *
     * @return {@link RouteMatch} con la posición y la distancia del punto más cercano.
     *
     * @throws IllegalArgumentException si la ruta no tiene puntos.
     */
    public static RouteMatch closestMatch(Coordinate target, List<Coordinate> polyline) {
        if (polyline.isEmpty()) {
            throw new IllegalArgumentException("La ruta no tiene puntos");
        }
        if (polyline.size() == 1) {
            return new RouteMatch(0.0, distanceMeters(target, polyline.get(0)));
        }

        RouteMatch best = null;
        for (int i = 0; i < polyline.size() - 1; i++) {
            Coordinate start = polyline.get(i);
            Coordinate end = polyline.get(i + 1);
            double t = projectionFraction(target, start, end);
            Coordinate closest = interpolate(start, end, t);
            double distance = distanceMeters(target, closest);
            if (best == null || distance < best.distanceMeters()) {
                best = new RouteMatch(i + t, distance);
            }
        }
        return best;
    }

    /**
     * Calcula la fracción (entre 0 y 1) del tramo {@code a→b} donde cae la
     * proyección ortogonal de {@code q}. Se usa una proyección plana local,
     * ajustando la longitud por el coseno de la latitud, válida para tramos
     * cortos.
     */
    private static double projectionFraction(Coordinate q, Coordinate a, Coordinate b) {
        double cosLat = Math.cos(Math.toRadians(q.latitude()));

        double abX = (b.longitude() - a.longitude()) * cosLat;
        double abY = b.latitude() - a.latitude();
        double aqX = (q.longitude() - a.longitude()) * cosLat;
        double aqY = q.latitude() - a.latitude();

        double lengthSquared = abX * abX + abY * abY;
        if (lengthSquared == 0) {
            return 0.0;
        }
        double t = (aqX * abX + aqY * abY) / lengthSquared;
        return Math.max(0.0, Math.min(1.0, t));
    }

    /**
     * Devuelve el punto que está a la fracción {@code t} del tramo {@code a→b}.
     */
    private static Coordinate interpolate(Coordinate a, Coordinate b, double t) {
        return new Coordinate(
                a.latitude() + (b.latitude() - a.latitude()) * t,
                a.longitude() + (b.longitude() - a.longitude()) * t
        );
    }
}