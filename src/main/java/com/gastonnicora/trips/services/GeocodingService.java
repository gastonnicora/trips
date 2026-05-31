package com.gastonnicora.trips.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.gastonnicora.trips.dtos.response.company.AddressResponse;
import com.gastonnicora.trips.exceptions.InternalErrorException;

/**
 * Servicio para obtener direcciones a partir de coordenadas.
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-20
 */
@Service
public class GeocodingService {

    private final RestClient restClient;

    /**
     * Constructor de la clase GeocodingService.
     *@throws InternalErrorException Si ocurre un error al inicializar el servicio de geocodificación
     */
    public GeocodingService() {
        try {
            this.restClient = RestClient.builder()
                    .baseUrl("https://nominatim.openstreetmap.org")
                    .defaultHeader("User-Agent", "Trips/1.0 (gastonmatias.21@gmail.com)")
                    .build();
        } catch (Exception e) {
            throw new InternalErrorException("Error al inicializar el servicio de geocodificación",
                    e.getMessage() + " Error al intentar inicializar el servicio de geocodificación");
        }

    }

    /**
     * Obtiene la dirección a partir de coordenadas.
     *
     * @param latitud  double con la latitud de la ubicación.
     * @param longitud double con la longitud de la ubicación.
     * @return {@link AddressResponse} con la dirección correspondiente
     * @throws InternalErrorException Si ocurre un error al obtener la dirección
     */
    public AddressResponse obtenerDireccion(double latitud, double longitud) {
        try {
            return this.restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/reverse")
                            .queryParam("format", "jsonv2")
                            .queryParam("lat", latitud)
                            .queryParam("lon", longitud)
                            .build())
                    .retrieve()
                    .body(AddressResponse.class);

        } catch (Exception ex) {
            throw new InternalErrorException("Error al obtener la dirección",
                    ex.getMessage() + " Error al intentar obtener la dirección");
        }

    }
}
