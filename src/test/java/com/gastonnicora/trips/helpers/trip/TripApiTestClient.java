package com.gastonnicora.trips.helpers.trip;

import com.gastonnicora.trips.dtos.request.trip.TripCreate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Cliente HTTP para los endpoints de viajes, pensado para tests de integración.
 *
 * <p>
 * Envía las peticiones con el token configurado mediante {@link #withToken(String)}
 * y devuelve los {@link ResultActions} para que el test valide la respuesta.
 * </p>
 */
public class TripApiTestClient {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private String token;

    /**
     * Crea un cliente de viajes.
     *
     * @param mockMvc      cliente MockMvc utilizado para ejecutar las peticiones.
     * @param objectMapper serializador JSON utilizado para el body de las peticiones.
     */
    public TripApiTestClient(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    /**
     * Envía una petición para crear un viaje en una empresa.
     *
     * @param companyUuid UUID de la empresa a la que pertenece el viaje.
     * @param tripCreate  datos del viaje que se desea crear.
     *
     * @return resultado de la petición.
     */
    public ResultActions createTrip(UUID companyUuid, TripCreate tripCreate) throws Exception {
        String json = objectMapper.writeValueAsString(tripCreate);
        return createTripRaw(companyUuid, json);
    }

    /**
     * Envía el body tal cual, útil para probar JSON mal formado.
     *
     * @param companyUuid UUID de la empresa a la que pertenece el viaje.
     * @param json        body de la petición sin procesar.
     *
     * @return resultado de la petición.
     */
    public ResultActions createTripRaw(UUID companyUuid, String json) throws Exception {
        return mockMvc.perform(post("/api/companies/" + companyUuid + "/trip")
                                       .header("Authorization", "Bearer " + token)
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .header("User-Agent", "JUnit-Test")
                                       .content(json));
    }

    /**
     * Configura el token JWT con el que se autenticarán las peticiones.
     *
     * @param token token JWT del usuario.
     *
     * @return este mismo cliente.
     */
    public TripApiTestClient withToken(String token) {
        this.token = token;
        return this;
    }

    public String getToken() {
        return token;
    }
}