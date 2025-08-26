/**
 * 
 */
package com.example.pokemon.adapters.out.pokeapi;

import java.util.List;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.example.pokemon.adapters.out.pokeapi.dto.EncounterDTO;
import com.example.pokemon.adapters.out.pokeapi.dto.PokeApiPokemonDTO;
import com.example.pokemon.common.exceptions.PokemonNotFoundException;
import com.example.pokemon.common.exceptions.RemoteClientException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Cliente adaptador que consume la API pública de
 * <a href="https://pokeapi.co">PokeAPI</a>.
 *
 * <p>
 * Responsabilidades:
 * </p>
 * <ul>
 * <li>Consumir endpoints remotos de PokeAPI usando {@link WebClient}.</li>
 * <li>Obtener los datos principales de un Pokémon dado su nombre.</li>
 * <li>Consultar las ubicaciones donde puede encontrarse ese Pokémon.</li>
 * <li>Manejar errores HTTP y de red, transformándolos en excepciones del
 * dominio: {@link PokemonNotFoundException},
 * {@link RemoteClientException}.</li>
 * </ul>
 *
 * <p>
 * Este cliente se inyecta en los casos de uso (application layer), encapsulando
 * toda la comunicación HTTP con la API externa.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class PokeApiClient {

	/**
	 * Cliente HTTP reactivo de Spring WebFlux configurado para apuntar a la URL
	 * base de PokeAPI (ej. https://pokeapi.co/api/v2).
	 *
	 * Se inyecta desde {@link com.example.pokemon.config.WebClientConfig}.
	 */
	private final WebClient pokeApiWebClient;

	/**
	 * Obtiene la información de un Pokémon específico desde PokeAPI.
	 *
	 * Flujo del método:
	 * <ol>
	 * <li>Consulta <b>/pokemon/{name}</b> → datos principales (id, nombre,
	 * experiencia base, habilidades, ítems).</li>
	 * <li>Si la respuesta es 404, lanza {@link PokemonNotFoundException}.</li>
	 * <li>Si ocurre un error 5xx, lanza {@link RemoteClientException}.</li>
	 * <li>Valida que la respuesta no sea nula (cuerpo vacío).</li>
	 * <li>Consulta <b>/pokemon/{name}/encounters</b> → ubicaciones donde aparece el
	 * Pokémon.</li>
	 * <li>Devuelve un {@link PokeApiPokemonDTO} que combina la información base +
	 * los encuentros.</li>
	 * </ol>
	 *
	 * Manejo de errores:
	 * <ul>
	 * <li>{@link WebClientResponseException.NotFound} → el Pokémon no existe.</li>
	 * <li>{@link WebClientRequestException} → problemas de red, timeouts o
	 * DNS.</li>
	 * <li>{@link Exception} genérico → envuelto en
	 * {@link RemoteClientException}.</li>
	 * </ul>
	 *
	 * @param name nombre del Pokémon a consultar (ej: "pikachu")
	 * @return DTO con datos combinados del Pokémon
	 * @throws PokemonNotFoundException si no existe el Pokémon en la API
	 * @throws RemoteClientException    si hay errores de red o del servidor remoto
	 */
	public PokeApiPokemonDTO getPokemon(String name) {
		try {
			// 1) Consulta de datos principales del Pokémon
			PokeApiPokemonDTO baseData = pokeApiWebClient.get().uri("/pokemon/{name}", name).retrieve()
					.onStatus(HttpStatusCode::is4xxClientError, response -> {
						if (response.statusCode().value() == 404) {
							return Mono.error(new PokemonNotFoundException("Pokémon not found: " + name));
						}
						return Mono.error(
								new RemoteClientException("Client error from PokeAPI: " + response.statusCode()));
					})
					.onStatus(HttpStatusCode::is5xxServerError,
							response -> Mono.error(
									new RemoteClientException("Server error from PokeAPI: " + response.statusCode())))
					.bodyToMono(PokeApiPokemonDTO.class).block();

			if (baseData == null) {
				throw new RemoteClientException("Empty body from PokeAPI for: " + name);
			}

			// 2) Consulta de encounters (lugares donde aparece el Pokémon)
			List<EncounterDTO> encounters = pokeApiWebClient.get().uri("/pokemon/{name}/encounters", name).retrieve()
					.onStatus(HttpStatusCode::is5xxServerError,
							response -> Mono.error(new RemoteClientException(
									"Server error from PokeAPI (encounters): " + response.statusCode())))
					.bodyToFlux(EncounterDTO.class).collectList().block();

			// 3) Combina la información base + los encuentros en un nuevo DTO inmutable
			return new PokeApiPokemonDTO(baseData.id(), baseData.name(), baseData.base_experience(),
					baseData.abilities(), baseData.held_items(), encounters);
		} catch (WebClientResponseException.NotFound e) {
			throw new PokemonNotFoundException("Pokémon not found: " + name, e);
		} catch (WebClientRequestException e) {
			throw new RemoteClientException("Network error calling PokeAPI", e);
		} catch (Exception e) {
			throw new RemoteClientException("Unexpected error calling PokeAPI", e);
		}
	}

}