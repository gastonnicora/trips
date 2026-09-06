package com.gastonnicora.trips;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Clase principal de la aplicación Trips. Habilita tareas programadas y ejecuta
 * código al iniciar la aplicación.
 */
@EnableScheduling
@SpringBootApplication
public class TripsApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(TripsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Obtiene el perfil activo de Spring, 'prod' por defecto
        String profile = System.getProperty("spring.profiles.active", "prod");

        // Mensajes informativos al iniciar la aplicación
        System.out.println("Profile activo: " + profile);
        System.out.println("Iniciando la aplicación de trips...");
        System.out.println("La aplicación de trips está lista para recibir solicitudes.");
    }

}
