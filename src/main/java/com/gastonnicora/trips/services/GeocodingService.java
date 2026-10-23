package com.gastonnicora.trips.services;

import com.gastonnicora.trips.dtos.response.company.AddressResponse;
import com.gastonnicora.trips.exceptions.InternalErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Servicio encargado de obtener direcciones a partir de coordenadas
 * geográficas.
 *
 * <p>
 * Utiliza un cliente HTTP para consultar el servicio de geocodificación
 * mediante coordenadas de latitud y longitud.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-05-21
 */
@Service
public class GeocodingService {

    /**
     * Cliente HTTP utilizado para consultar el servicio Nominatim de
     * OpenStreetMap.
     */
    private final RestClient restClient;

    /**
     * Crea una instancia del servicio de geocodificación e inicializa el cliente
     * HTTP utilizado para realizar las consultas.
     *
     * <p>
     * El cliente se configura con un tiempo máximo de 3 segundos para conectar y
     * de 5 segundos para recibir la respuesta, de modo que un servicio externo
     * lento no deje bloqueada la petición.
     * </p>
     *
     * @param contactEmail Correo de contacto, tomado de la propiedad
     *                     {@code geocoding.contact-email}, que se informa en el
     *                     encabezado {@code User-Agent} de las consultas.
     *
     * @throws InternalErrorException Si ocurre un error al inicializar el
     *                                servicio de geocodificación.
     */
    public GeocodingService(@Value("${geocoding.contact-email}") String contactEmail) {

        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(3))
                    .build();
            JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
            requestFactory.setReadTimeout(Duration.ofSeconds(5));

            this.restClient = RestClient.builder()
                    .requestFactory(requestFactory)
                    .baseUrl("https://nominatim.openstreetmap.org")
                    .defaultHeader("User-Agent", "Trips/1.0 (" + contactEmail + ")")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalErrorException(
                    "Error al inicializar el servicio de geocodificación",
                    e.getMessage() + " Error al intentar inicializar el servicio de geocodificación"
            );
        }

    }

    /**
     * Obtiene una dirección a partir de sus coordenadas geográficas.
     *
     * <p>
     * Realiza una consulta de geocodificación inversa utilizando la latitud y
     * longitud proporcionadas y devuelve la información obtenida.
     * </p>
     *
     * @param latitud  Latitud de la ubicación.
     * @param longitud Longitud de la ubicación.
     *
     * @return {@link AddressResponse} con la información de la dirección
     * correspondiente.
     *
     * @throws InternalErrorException Si ocurre un error al obtener la dirección.
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
            ex.printStackTrace();
            throw new InternalErrorException(
                    "Error al obtener la dirección",
                    ex.getMessage() + " Error al intentar obtener la dirección"
            );
        }

    }
}