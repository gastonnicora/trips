package com.gastonnicora.trips.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * Configuración de Swagger/OpenAPI para la documentación de la API REST.
 * <p>
 * Esta clase define la información completa de la API, incluyendo título,
 * versión, descripción, contacto, licencia y el esquema de seguridad para JWT.
 * </p>
 * 
 * <p>
 * El esquema de seguridad configurado permite la autenticación mediante tokens
 * Bearer JWT en los endpoints protegidos.
 * </p>
 * 
 * @author Gastón
 * @version 1.1
 * @since 2023-05-04
 */
@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {

    /**
     * Bean de OpenAPI que proporciona la información general de la API para
     * Swagger UI.
     * <p>
     * Incluye título, versión, descripción, contacto y licencia.
     * </p>
     * 
     * @return {@link OpenAPI} objeto configurado con la información completa de
     *         la API.
     */
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Viajes")
                        .version("1.0")
                        .description("API REST de viajes para la plataforma de gestión de viajes, reservas y usuarios.")
                        .contact(new Contact()
                                .name("Gastón Nicora")
                                .email("gastonmatias.21@gmail.com")
                                .url("https://tu-website.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")) 
                );
    }
}