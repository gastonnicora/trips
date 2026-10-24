package com.gastonnicora.trips.dtos.request.trip;

import com.gastonnicora.trips.utils.GeoUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO utilizado para buscar viajes que pasan entre un origen y un destino en un día.
 *
 * <p>
 * Se recibe como parámetros de consulta (query params). Contiene las coordenadas
 * de origen y destino, la distancia máxima en metros entre cada coordenada y el
 * recorrido, y el día de salida de los viajes.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-10-24
 */
@Schema(description = "Parámetros para buscar viajes que pasan entre un origen y un destino en un día")
@NoArgsConstructor
@Getter
@Setter
public class TripSearchRequest {

    /**
     * Latitud del origen, en grados decimales.
     */
    @Schema(description = "Latitud del origen, en grados decimales.", example = "-34.9214")
    @NotNull
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Double originLatitude;

    /**
     * Longitud del origen, en grados decimales.
     */
    @Schema(description = "Longitud del origen, en grados decimales.", example = "-57.9545")
    @NotNull
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Double originLongitude;

    /**
     * Latitud del destino, en grados decimales.
     */
    @Schema(description = "Latitud del destino, en grados decimales.", example = "-34.6037")
    @NotNull
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Double destinationLatitude;

    /**
     * Longitud del destino, en grados decimales.
     */
    @Schema(description = "Longitud del destino, en grados decimales.", example = "-58.3816")
    @NotNull
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Double destinationLongitude;

    /**
     * Distancia máxima, en metros, entre cada coordenada y el recorrido del viaje.
     */
    @Schema(
            description = "Distancia máxima, en metros, entre cada coordenada y el recorrido del viaje. Debe ser mayor a cero.",
            example = "1000"
    )
    @NotNull
    @Positive
    private Double maxDistanceMeters;

    /**
     * Día de salida de los viajes que se desean buscar.
     */
    @Schema(description = "Día de salida de los viajes.", example = "2026-11-01")
    @NotNull
    private LocalDate date;

    /**
     * Devuelve la coordenada de destino.
     *
     * <p>
     * Debe llamarse solo con el DTO ya validado, cuando la latitud y la longitud
     * de destino no son {@code null}.
     * </p>
     *
     * @return {@link GeoUtils.Coordinate} de destino.
     */
    public GeoUtils.Coordinate toDestination() {
        return new GeoUtils.Coordinate(destinationLatitude, destinationLongitude);
    }

    /**
     * Devuelve la coordenada de origen.
     *
     * <p>
     * Debe llamarse solo con el DTO ya validado, cuando la latitud y la longitud
     * de origen no son {@code null}.
     * </p>
     *
     * @return {@link GeoUtils.Coordinate} de origen.
     */
    public GeoUtils.Coordinate toOrigin() {
        return new GeoUtils.Coordinate(originLatitude, originLongitude);
    }
}