/**
 * 
 */
package com.example.pokemon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;

import com.example.pokemon.adapters.in.soap.GlobalSoapExceptionResolver;

/**
 * 
 */
@Configuration
public class SoapExceptionConfig {

	@Bean
	SoapFaultMappingExceptionResolver exceptionResolver() {
		return new GlobalSoapExceptionResolver();
	}

}
