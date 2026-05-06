package com.gastonnicora.trips.config;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.method.HandlerMethod;

import io.swagger.v3.oas.models.Operation;

/**
 * Configuración de personalización de Swagger/OpenAPI.
 * <p>
 * Esta clase agrega información de roles requeridos a la documentación de
 * la API generada por SpringDoc para métodos protegidos con la anotación
 * {@link PreAuthorize}.
 * </p>
 * 
 * <p>
 * Cada operación protegida mostrará en su descripción los roles necesarios
 * para acceder, por ejemplo: "🔒 Requiere rol: ADMIN, USER".
 * </p>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
@Configuration
public class SwaggerConfigCustomer {

    /**
     * Bean que personaliza las operaciones de OpenAPI agregando la información
     * de seguridad basada en {@link PreAuthorize}.
     * <p>
     * Recorre cada método expuesto en la API y, si tiene la anotación
     * {@link PreAuthorize}, extrae los roles y los añade a la descripción
     * de la operación.
     * </p>
     * 
     * @return {@link OperationCustomizer} para agregar información de roles a las operaciones
     */
    @Bean
    public OperationCustomizer customizePreAuthorize() {
        return (Operation operation, HandlerMethod handlerMethod) -> {

            PreAuthorize preAuth = handlerMethod.getMethodAnnotation(PreAuthorize.class);

            if (preAuth != null) {
                String expression = preAuth.value();

                String roles = extractRoles(expression);

                String securityInfo = "🔒 Requiere rol: " + roles;

                String existingDescription = operation.getDescription();

                operation.setDescription(
                        (existingDescription == null ? "" : existingDescription + "\n\n")
                                + securityInfo);
            }

            return operation;
        };
    }

    /**
     * Extrae los roles de una expresión de {@link PreAuthorize}.
     * <p>
     * Esta función reconoce expresiones de tipo:
     * <ul>
     * <li>hasRole('ROL')</li>
     * <li>hasAnyRole('ROL1','ROL2')</li>
     * </ul>
     * y devuelve los roles como una cadena separada por comas.
     * Si no se encuentra ningún rol, devuelve la expresión completa.
     * </p>
     * 
     * @param expression expresión de {@link PreAuthorize} a analizar
     * @return {@link String} roles extraídos separados por coma
     */
    private String extractRoles(String expression) {
        List<String> roles = new ArrayList<>();

        // hasRole('ADMIN')
        Pattern singleRolePattern = Pattern.compile("hasRole\\('(.+?)'\\)");
        Matcher singleMatcher = singleRolePattern.matcher(expression);

        while (singleMatcher.find()) {
            roles.add(singleMatcher.group(1));
        }

        // hasAnyRole('ADMIN','USER')
        Pattern anyRolePattern = Pattern.compile("hasAnyRole\\((.*?)\\)");
        Matcher anyMatcher = anyRolePattern.matcher(expression);

        while (anyMatcher.find()) {
            String inside = anyMatcher.group(1); // 'ADMIN','USER'

            String[] parts = inside.split(",");

            for (String part : parts) {
                String role = part.replaceAll("[\\'\\s]", "");
                roles.add(role);
            }
        }

        // fallback si no matchea nada
        if (roles.isEmpty()) {
            return expression;
        }

        return String.join(", ", roles);
    }
}