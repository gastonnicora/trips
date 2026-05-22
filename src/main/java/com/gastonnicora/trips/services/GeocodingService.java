package com.gastonnicora.trips.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.gastonnicora.trips.dtos.response.company.AddressResponse;

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
     *
     */
    public GeocodingService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://nominatim.openstreetmap.org")
                .defaultHeader("User-Agent", "Trips/1.0 (gastonmatias.21@gmail.com)")
                .build();
                // TODO 🚀:  agregar try 
    }

    /**
     * Obtiene la dirección a partir de coordenadas.
     *
     * @param latitud double con la latitud de la ubicación.
     * @param longitud double con la longitud de la ubicación.
     * @return {@link AddressResponse} con la dirección correspondiente
     */
    public AddressResponse obtenerDireccion(double latitud, double longitud) {
        return this.restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/reverse")
                        .queryParam("format", "jsonv2")
                        .queryParam("lat", latitud)
                        .queryParam("lon", longitud)
                        .build())
                .retrieve()
                .body(AddressResponse.class);
                // TODO 🚀:  agregar try 
    }
}
