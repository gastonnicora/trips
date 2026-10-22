package com.gastonnicora.trips.helpers.company;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.response.company.AddressResponse;
import com.gastonnicora.trips.dtos.response.company.AddressResponse.Address;
import com.gastonnicora.trips.services.GeocodingService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

public class CompanyTestFactory {

    private final CompanyApiTestClient companyApi;
    private final ObjectMapper objectMapper;
    private final GeocodingService geocodingService;

    private CompanyDTO company;

    public CompanyTestFactory(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            String token,
            GeocodingService geocodingService
                             ) {

        this.objectMapper = objectMapper;
        this.geocodingService = geocodingService;

        this.companyApi = new CompanyApiTestClient(
                mockMvc,
                objectMapper
        ).withToken(token);

        configureGeocoding();
    }

    private void configureGeocoding() {
        when(geocodingService.obtenerDireccion(anyDouble(), anyDouble()))
                .thenReturn(
                        new AddressResponse(
                                "calle falsa 123",
                                new Address(
                                        "calle falsa",
                                        "123",
                                        "barrio",
                                        "ciudad",
                                        "departamento",
                                        "estado",
                                        "pais"
                                )
                        )
                           );
    }

    public CompanyDTO createCompany() throws Exception {

        MvcResult result = companyApi
                .createCompany(
                        "Good Name",
                        "goodemail@mail.com",
                        "+549112233445",
                        -34.6037,
                        -54.3816
                              )
                .andExpect(
                        org.springframework.test.web.servlet.result.MockMvcResultMatchers
                                .status().isOk()
                          )
                .andReturn();

        String responseJson = result
                .getResponse()
                .getContentAsString();

        this.company = objectMapper.readValue(
                responseJson,
                CompanyDTO.class
                                             );

        return company;
    }

    public CompanyDTO getCompany() {
        return company;
    }
}
