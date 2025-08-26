package com.example.pokemon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SoapPokeapiHexApplication
 *
 * Punto de entrada de la aplicación Spring Boot.
 *
 * - Arranca el contexto de Spring, detecta componentes y configura las capas
 * (endpoints SOAP, adaptadores, repositorios, beans). - Mantener esta clase lo
 * más pequeña posible: configuración específica de infraestructura debe ir en
 * clases @Configuration.
 */
@SpringBootApplication
public class SoapPokeapiHexApplication {

	public static void main(String[] args) {
		SpringApplication.run(SoapPokeapiHexApplication.class, args);
	}

}
