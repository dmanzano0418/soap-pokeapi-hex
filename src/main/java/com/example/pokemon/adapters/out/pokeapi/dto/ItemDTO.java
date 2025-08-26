/**
 * 
 */
package com.example.pokemon.adapters.out.pokeapi.dto;

/**
 * DTO que representa un ítem del juego que un Pokémon puede sostener, tal como
 * se expone en PokeAPI.
 *
 * Este objeto es un recurso referenciado dentro de {@link HeldItemEntryDTO}, y
 * contiene solamente la información mínima (nombre y URL).
 *
 * Ejemplo JSON (fragmento de la API):
 * 
 * <pre>
 * {
 *   "name": "oran-berry",
 *   "url": "https://pokeapi.co/api/v2/item/132/"
 * }
 * </pre>
 *
 * @param name Nombre del ítem (ej. "oran-berry").
 * @param url  URL del recurso en PokeAPI con más detalles del ítem.
 */
public record ItemDTO(String name, String url) {
}
