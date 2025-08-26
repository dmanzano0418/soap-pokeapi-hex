/**
 * 
 */
package com.example.pokemon.adapters.out.pokeapi.dto;

import java.util.List;

/**
 * DTO que representa la respuesta principal de la API de PokeAPI para un
 * Pokémon específico.
 *
 * Este objeto encapsula tanto la información básica del Pokémon (id, nombre,
 * experiencia base, habilidades, ítems) como la lista de encuentros (lugares
 * donde aparece en el juego).
 *
 * Importante: - Los nombres de campos siguen el estilo de la API externa
 * (snake_case). - Posteriormente se "flatten" o transforman a nombres más
 * expresivos en el dominio usando
 * {@link com.example.pokemon.adapters.out.pokeapi.PokeApiMapper}.
 *
 * Ejemplo de endpoint origen: - GET https://pokeapi.co/api/v2/pokemon/{name} -
 * GET https://pokeapi.co/api/v2/pokemon/{name}/encounters
 *
 * Inmutable: definido como {@code record}.
 *
 * @param id                       Identificador único del Pokémon en la
 *                                 PokeAPI.
 * @param name                     Nombre del Pokémon en minúsculas (ej:
 *                                 "pikachu").
 * @param base_experience          Experiencia base que otorga al derrotarlo.
 * @param abilities                Lista de habilidades conocidas del Pokémon.
 * @param held_items               Lista de ítems que puede sostener en combate.
 * @param location_area_encounters Lista de ubicaciones (encounters) donde
 *                                 aparece este Pokémon.
 */
public record PokeApiPokemonDTO(Integer id, String name, Integer base_experience, List<AbilityEntryDTO> abilities,
		List<HeldItemEntryDTO> held_items, List<EncounterDTO> location_area_encounters) {
}