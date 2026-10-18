package com.gastonnicora.trips.config;

import com.gastonnicora.trips.services.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración encargada de inicializar datos al arrancar la aplicación.
 *
 * <p>
 * Crea un usuario con rol {@code SUPER_ADMIN} si no existe, utilizando las
 * credenciales definidas mediante las propiedades {@code superadmin.email} y
 * {@code superadmin.password}.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-04-19
 */
@Configuration
public class DataInitializer {

    /**
     * Dirección de correo electrónico del usuario {@code SUPER_ADMIN}, obtenida
     * desde la configuración de la aplicación.
     */
    @Value("${superadmin.email}")
    private String email;

    /**
     * Contraseña del usuario {@code SUPER_ADMIN}, obtenida desde la
     * configuración de la aplicación.
     */
    @Value("${superadmin.password}")
    private String password;

    /**
     * Crea un {@link CommandLineRunner} encargado de inicializar el usuario
     * {@code SUPER_ADMIN} al iniciar la aplicación.
     *
     * <p>
     * Si el usuario ya existe, no se crea uno nuevo.
     * </p>
     *
     * @param userService ({@link UserService}) servicio utilizado para gestionar usuarios.
     *
     * @return Ejecutor encargado de inicializar el usuario {@code SUPER_ADMIN}.
     *
     * @see UserService#createSuperAdminIfNotExists(String, String)
     */
    @Bean
    CommandLineRunner init(UserService userService) {
        return args -> {
            System.out.println("Iniciando la creación del usuario SUPER_ADMIN si no existe...");
            userService.createSuperAdminIfNotExists(email, password);
            System.out.println("Proceso de inicialización completado.");
        };
    }
}