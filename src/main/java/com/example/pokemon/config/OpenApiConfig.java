/**
 * 
 */
package com.example.pokemon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

/**
 * OpenApiConfig
 *
 * Configuración básica para SpringDoc OpenAPI.
 *
 * - Define metadata (title, description, version, contacto).
 * - El starter springdoc-openapi crea automáticamente los endpoints:
 *     - /v3/api-docs (JSON)
 *     - /v3/api-docs.yaml (YAML)
 *     - /swagger-ui.html o /swagger-ui/index.html (UI)
 */
@Configuration
public class OpenApiConfig {
	
	@Bean
    OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pokémon SOAP/REST Logs API")
                        .version("1.0.0")
                        .description("API de administración para consultar logs de requests del servicio SOAP/REST")
                        .contact(new Contact().name("Equipo Backend").email("dev@example.com"))
                );
    }

}
