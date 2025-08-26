/**
 * 
 */
package com.example.pokemon.application.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.pokemon.adapters.out.pokeapi.PokeApiClient;
import com.example.pokemon.adapters.out.pokeapi.dto.PokeApiPokemonDTO;
import com.example.pokemon.adapters.out.pokeapi.mapper.PokeApiMapper;
import com.example.pokemon.domain.model.Pokemon;
import com.example.pokemon.domain.ports.in.QueryPokemonUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación de {@link QueryPokemonUseCase}.
 *
 * Responsabilidades:
 * - Orquestar la lectura remota (REST) vía {@link PokeApiClient}.
 * - Convertir DTOs de PokeAPI al modelo de dominio {@link Pokemon} usando {@link PokeApiMapper} (MapStruct).
 * - Exponer operaciones de lectura atómicas (abilities, baseExperience, etc.) que el endpoint SOAP consume.
 *
 * Decisiones de diseño:
 * - Validamos entradas (name) para fallar temprano con IllegalArgumentException (el GlobalSoapExceptionResolver
 *   mapeará esto a un fault de tipo Client/Sender).
 * - Centralizamos la llamada remota en {@link #fetch(String)} para evitar duplicación.
 * - Devolvemos colecciones inmutables (defensivas) para proteger el dominio de modificaciones accidentales en capas superiores.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QueryPokemonService implements QueryPokemonUseCase {

	/**
     * Cliente REST que encapsula las llamadas a la API pública de PokeAPI.
     * Es inyectado por constructor y se declara como final para garantizar
     * que no se reemplace la referencia durante el ciclo de vida del servicio.
     */
	private final PokeApiClient pokeApiClient;
	
	/**
     * Mapper de MapStruct que convierte los DTOs de PokeAPI en
     * el modelo de dominio {@link Pokemon}. Se mantiene inmutable
     * y es responsabilidad de este servicio orquestar su uso.
     */
    private final PokeApiMapper mapper;

    /**
     * Ejecuta la consulta remota a PokeAPI y convierte el resultado a dominio.
     * @param name nombre del Pokémon
     * @return agregado de dominio {@link Pokemon}
     */
    private Pokemon fetch(String name) {
        validateName(name);
        log.debug("Fetching Pokémon from PokeAPI: name={}", name);

        // 1) Llamada remota (DTO)
        PokeApiPokemonDTO dto = pokeApiClient.getPokemon(name);

        // 2) Mapeo a dominio (MapStruct)
        Pokemon domain = mapper.toDomain(dto);

        // 3) Postcondiciones/defensas
        //    Normalizamos listas nulas a vacías para evitar NPE más arriba
        return domain.toBuilder()
                .abilities(nullSafeList(domain.getAbilities()))
                .heldItems(nullSafeList(domain.getHeldItems()))
                .locationAreaEncounters(nullSafeList(domain.getLocationAreaEncounters()))
                .build();
    }

    @Override
    public Pokemon getPokemon(String name) {
        return fetch(name);
    }

    @Override
    public List<String> getAbilities(String name) {
        return Collections.unmodifiableList(fetch(name).getAbilities());
    }

    @Override
    public int getBaseExperience(String name) {
        return fetch(name).getBaseExperience();
    }

    @Override
    public List<String> getHeldItems(String name) {
        return Collections.unmodifiableList(fetch(name).getHeldItems());
    }

    @Override
    public int getId(String name) {
        return fetch(name).getId();
    }

    @Override
    public List<String> getLocationAreaEncounters(String name) {
        return Collections.unmodifiableList(fetch(name).getLocationAreaEncounters());
    }

    // ------------------------
    // Helpers
    // ------------------------

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Pokemon name must not be null/blank");
        }
    }

    private static <T> List<T> nullSafeList(List<T> list) {
        return (list == null) ? Collections.emptyList() : list;
    }

}
