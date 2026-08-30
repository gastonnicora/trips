package com.gastonnicora.trips.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gastonnicora.trips.services.UserService;

/**
 * Configuración encargada de inicializar datos al arrancar la aplicación.
 * <p>
 * Crea un usuario SUPER_ADMIN por defecto si no existe, utilizando las
 * credenciales
 * definidas en las variables de entorno `superadmin.email` y
 * `superadmin.password`.
 * Este usuario tiene acceso completo a todas las funcionalidades de la
 * aplicación.
 * </p>
 */
@Configuration
public class DataInitializer {
    /**
     * Email del usuario SUPER_ADMIN, obtenido desde la configuración.
     * Se espera una dirección de correo válida.
     */
    @Value("${superadmin.email}")
    private String email;

    /**
     * Contraseña del usuario SUPER_ADMIN, obtenida desde la configuración.
     */
    @Value("${superadmin.password}")
    private String password;

    /**
     * Ejecuta lógica al iniciar la aplicación para crear un usuario SUPER_ADMIN si
     * no existe.
     * Este método se ejecuta automáticamente al inicio del ciclo de vida de la
     * aplicación.
     *
     * @param userService servicio de usuarios utilizado para crear el SUPER_ADMIN
     * @return un {@link CommandLineRunner} que inicializa los datos de usuario.
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