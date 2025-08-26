/**
 * 
 */
package com.example.pokemon.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

/**
 * Configuración central de los servicios SOAP para el dominio Pokémon.
 *
 * <p>Responsabilidades:</p>
 * <ul>
 *   <li>Habilita soporte para Spring Web Services vía {@link EnableWs}.</li>
 *   <li>Registra el {@link MessageDispatcherServlet}, análogo al DispatcherServlet en REST.</li>
 *   <li>Define y expone un WSDL 1.1 dinámico basado en los esquemas XSD.</li>
 *   <li>Gestiona la carga de los esquemas principales y auxiliares (ej: detalle de errores).</li>
 * </ul>
 *
 * <p>End-to-end: con esta clase y el {@code application.yml}, el servicio SOAP
 * quedará accesible en:</p>
 * <ul>
 *   <li>WSDL: <code>http://localhost:9001/ws/pokemon.wsdl</code></li>
 *   <li>Operaciones SOAP: <code>http://localhost:9001/ws</code></li>
 * </ul>
 */
@EnableWs
@Configuration
public class WebServiceConfig {

    /**
     * Registra el {@link MessageDispatcherServlet}, encargado de:
     * <ul>
     *   <li>Interceptar todas las peticiones bajo "/ws/*".</li>
     *   <li>Resolver endpoints anotados con {@code @Endpoint}.</li>
     *   <li>Serializar las respuestas SOAP automáticamente.</li>
     * </ul>
     *
     * <p>Se recomienda <b>setLoadOnStartup(1)</b> para inicializar el servlet
     * de inmediato y evitar demoras en la primera petición.</p>
     *
     * @param applicationContext el contexto de Spring usado para descubrir endpoints y beans
     * @return un registro de servlet configurado para "/ws/*"
     */
    @Bean(name = "messageDispatcherServlet")
    ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true); // genera URLs correctas en el WSDL
        ServletRegistrationBean<MessageDispatcherServlet> registration =
                new ServletRegistrationBean<>(servlet, "/ws/*");
        registration.setLoadOnStartup(1);
        return registration;
    }

    /**
     * Define el WSDL 1.1 que se expondrá dinámicamente.
     *
     * <p>Este WSDL describe las operaciones disponibles para el dominio Pokémon
     * y se basa en el esquema {@code pokemon.xsd}.</p>
     *
     * Configuración:
     * <ul>
     *   <li><b>PortTypeName</b>: "PokemonPort" → interfaz de servicio.</li>
     *   <li><b>LocationUri</b>: "/ws" → endpoint base.</li>
     *   <li><b>TargetNamespace</b>: "http://example.com/pokemon/soap/schemas" → debe coincidir con los paquetes JAXB generados.</li>
     *   <li><b>Schema</b>: referencia a {@link #pokemonSchema()}.</li>
     * </ul>
     *
     * @param pokemonSchema esquema principal del dominio Pokémon
     * @return definición dinámica de WSDL
     */
    @Bean(name = "pokemon")
    DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema pokemonSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("PokemonPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace("http://example.com/pokemon/soap/schemas");
        wsdl11Definition.setSchema(pokemonSchema);
        return wsdl11Definition;
    }

    /**
     * Bean para el esquema principal (pokemon.xsd).
     *
     * <p>Contiene las definiciones de:
     * <ul>
     *   <li>GetPokemonRequest / GetPokemonResponse</li>
     *   <li>Importación del esquema de errores (error-detail.xsd)</li>
     * </ul>
     * </p>
     *
     * @return esquema XSD de operaciones Pokémon
     */
    @Bean
    XsdSchema pokemonSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/pokemon.xsd"));
    }

    /**
     * Bean auxiliar para el esquema de errores (error-detail.xsd).
     *
     * <p>Aunque se importa desde {@code pokemon.xsd}, registrarlo
     * explícitamente como bean permite:
     * <ul>
     *   <li>Reutilizarlo en validaciones adicionales.</li>
     *   <li>Garantizar que Spring lo cargue en fase de inicialización.</li>
     * </ul>
     * </p>
     *
     * @return esquema XSD de detalle de errores
     */
    @Bean
    XsdSchema errorDetailSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/error-detail.xsd"));
    }

}
