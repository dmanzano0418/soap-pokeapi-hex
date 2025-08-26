/**
 * 
 */
package com.example.pokemon.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import reactor.netty.http.client.HttpClient;

/**
 * WebClientConfig
 *
 * Configura y expone un bean {@link WebClient} listo para consumir la PokeAPI.
 *
 * - baseUrl: https://pokeapi.co/api/v2
 * - timeouts: conexión y respuesta configurados con Reactor Netty
 * - exchangeStrategies: aumenta el buffer para respuestas JSON grandes
 *
 * Este bean se inyecta en {@link com.example.pokemon.adapters.out.pokeapi.PokeApiClient}.
 */
@Configuration
public class WebClientConfig {

    /**
     * Crea e inyecta un {@link WebClient} configurado para consumir la PokeAPI.
     *
     * - Usa {@link HttpClient} de Reactor Netty para configurar timeouts de red.
     * - Usa {@link ExchangeStrategies} para ampliar el tamaño de buffer de
     *   deserialización (por defecto es pequeño y PokeAPI devuelve JSONs extensos).
     *
     * @return una instancia de {@link WebClient} lista para inyección
     */
    @Bean
    WebClient pokeApiWebClient() {
    	// Reactor Netty HttpClient con timeouts
        HttpClient httpClient = HttpClient.create()
        		.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5_000) // conexión: 5s
                .responseTimeout(Duration.ofSeconds(10));             // lectura/respuesta: 10s

        // Ampliar buffer para bodyToMono/bodyToFlux si las respuestas son grandes
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)) // 16 MB
                .build();

        return WebClient.builder()
                .baseUrl("https://pokeapi.co/api/v2")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(exchangeStrategies)
                .build();
    }
    
}