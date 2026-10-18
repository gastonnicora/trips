package com.gastonnicora.trips;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Clase principal de la aplicación Trips.
 *
 * <p>
 * Habilita la ejecución de tareas programadas y permite ejecutar acciones al
 * iniciar la aplicación mediante {@link CommandLineRunner}.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-03-18
 */
@EnableScheduling
@SpringBootApplication
public class TripsApplication implements CommandLineRunner {

    /**
     * Punto de entrada principal de la aplicación.
     *
     * @param args Argumentos recibidos al iniciar la aplicación.
     */
    public static void main(String[] args) {
        SpringApplication.run(TripsApplication.class, args);
    }

    /**
     * Ejecuta las acciones configuradas al iniciar la aplicación.
     *
     * <p>
     * Obtiene el perfil activo de Spring y muestra mensajes informativos sobre
     * el inicio y disponibilidad de la aplicación.
     * </p>
     *
     * @param args Argumentos recibidos durante el inicio de la aplicación.
     *
     * @throws Exception Si ocurre un error durante la ejecución del proceso de
     *                   inicio.
     */
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