/**
 * 
 */
package com.example.pokemon.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * PropertiesConfig
 *
 * Configuración de arranque dedicada para habilitar el enlace de propiedades
 * tipadas (type-safe configuration) definidas con {@link org.springframework.boot.context.properties.ConfigurationProperties}.
 *
 * ¿Qué hace?
 * - Registra y habilita la clase {@link MaskingProperties} para que Spring Boot
 *   vincule automáticamente los valores definidos en application.yml bajo el
 *   prefijo "app.masking" (p.ej. app.masking.max-payload-length).
 *
 * ¿Por qué una clase separada?
 * - Mantiene el arranque y la configuración limpia y explícita.
 * - Evita anotar la clase principal con {@code @EnableConfigurationProperties}
 *   si prefieres separar responsabilidades (útil en proyectos medianos/grandes).
 *
 * Requisitos:
 * - Asegúrate de que {@link MaskingProperties} esté anotada con
 *   {@code @ConfigurationProperties(prefix = "app.masking")} y opcionalmente {@code @Validated}.
 * - Coloca esta clase en un paquete escaneado por Spring (debajo de la raíz
 *   donde resides tu {@code @SpringBootApplication}).
 *
 * Sugerencia IDE (autocompletado de YAML):
 * - Añade el procesador de configuración en el POM:
 *
 *   <pre>
 *   &lt;dependency&gt;
 *     &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
 *     &lt;artifactId&gt;spring-boot-configuration-processor&lt;/artifactId&gt;
 *     &lt;optional&gt;true&lt;/optional&gt;
 *   &lt;/dependency&gt;
 *   </pre>
 *
 * Con esto, tu IDE reconocerá propiedades como:
 * - app.masking.max-payload-length
 * - app.masking.sensitive-keywords
 */
@Configuration
@EnableConfigurationProperties(MaskingProperties.class)
public class PropertiesConfig {
	
	// Clase de bootstrap sin estado ni beans adicionales.
    // Su única responsabilidad es habilitar el binding de MaskingProperties.

}
