/**
 * 
 */
package com.example.pokemon.adapters.out.pokeapi.dto;

/**
 * DTO que representa un método de encuentro de Pokémon, tal como se expone en
 * PokeAPI.
 *
 * Este objeto se utiliza dentro de {@link EncounterDetailDTO} para indicar la
 * forma en que se encuentra un Pokémon (ejemplo: caminando por hierba alta,
 * usando surf, pescando).
 *
 * Ejemplo JSON (fragmento de la API):
 * 
 * <pre>
 * {
 *   "name": "walk",
 *   "url": "https://pokeapi.co/api/v2/encounter-method/1/"
 * }
 * </pre>
 *
 * @param name Nombre del método de encuentro (ej. "walk", "old-rod").
 * @param url  URL del recurso en PokeAPI con más información sobre el método.
 */
public record EncounterMethodDTO(String name, String url) {
}
