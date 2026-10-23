package com.gastonnicora.trips.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI para la documentación de la API REST.
 *
 * <p>
 * Define la información general de la API, incluyendo su título, versión,
 * descripción, información de contacto y licencia.
 * </p>
 *
 * <p>
 * También configura el esquema de seguridad utilizado por Swagger para
 * autenticar solicitudes mediante tokens JWT enviados en el encabezado
 * {@code Authorization} como Bearer Token.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-03-18
 */
@Configuration
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
public class SwaggerConfig {

    /**
     * Crea y configura la definición de OpenAPI utilizada para documentar la
     * API en Swagger UI.
     *
     * <p>
     * La definición incluye el título, versión, descripción, información de
     * contacto y licencia del proyecto.
     * </p>
     *
     * @return Definición de {@link OpenAPI} con la información general de la
     * API.
     */
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                              .title("API Viajes")
                              .version("1.0")
                              .description(
                                      "API REST de viajes para la plataforma de gestión de viajes, reservas y usuarios.")
                              .contact(new Contact()
                                               .name("Gastón Nicora")
                                               .email("gastonmatias.21@gmail.com")
                                               .url("https://github.com/gastonnicora/trips"))
                              .license(new License()
                                               .name("MIT License")
                                               .url("https://opensource.org/licenses/MIT")));
    }
}