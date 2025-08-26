/**
 * 
 */
package com.example.pokemon.adapters.out.pokeapi.dto;

/**
 * DTO que representa una habilidad básica de un Pokémon, tal como se expone en
 * PokeAPI.
 *
 * Este objeto es un recurso referenciado dentro de {@link AbilityEntryDTO}, y
 * contiene solamente la información mínima (nombre y URL).
 *
 * Ejemplo JSON (fragmento de la API):
 * 
 * <pre>
 * {
 *   "name": "static",
 *   "url": "https://pokeapi.co/api/v2/ability/9/"
 * }
 * </pre>
 *
 * @param name Nombre de la habilidad (ej. "static").
 * @param url  URL del recurso en PokeAPI con más detalles de la habilidad.
 */
public record AbilityDTO(String name, String url) {
}
