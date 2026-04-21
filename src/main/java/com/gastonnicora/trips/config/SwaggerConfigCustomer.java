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

@Configuration
public class SwaggerConfigCustomer {

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
