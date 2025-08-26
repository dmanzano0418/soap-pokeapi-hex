/**
 * 
 */
package com.example.pokemon.adapters.out.pokeapi.dto;

/**
 * DTO que representa una versión específica del juego Pokémon, tal como se
 * expone en PokeAPI.
 *
 * Este objeto se utiliza en {@link VersionDetailDTO} para indicar en qué
 * versión del juego aplica un detalle de ítem sostenido por un Pokémon.
 *
 * Ejemplo JSON (fragmento de la API):
 * 
 * <pre>
 * {
 *   "name": "red",
 *   "url": "https://pokeapi.co/api/v2/version/1/"
 * }
 * </pre>
 *
 * @param name Nombre de la versión (ej. "red", "blue", "yellow").
 * @param url  URL del recurso en PokeAPI con más información sobre la versión.
 */
public record VersionDTO(String name, String url) {
}
