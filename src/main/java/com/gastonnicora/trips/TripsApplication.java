package com.gastonnicora.trips;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TripsApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(TripsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		String profile = System.getProperty("spring.profiles.active","prod");
		System.out.println("Profile activo: " + profile);
		System.out.println("Iniciando la aplicación de trips...");
		System.out.println("La aplicación de trips está lista para recibir solicitudes.");


		
	}

}
