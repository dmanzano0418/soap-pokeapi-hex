/**
 * 
 */
package com.example.pokemon.adapters.out.pokeapi.dto;

/**
 * DTO que representa el detalle de aparición de un ítem en una versión
 * específica del juego, tal como se expone en PokeAPI.
 *
 * Este objeto se utiliza dentro de {@link HeldItemEntryDTO} para indicar en qué
 * versiones un Pokémon puede sostener un ítem determinado y con qué
 * probabilidad de aparición.
 *
 * Ejemplo JSON (fragmento de la API):
 * 
 * <pre>
 * {
 *   "version": {
 *     "name": "red",
 *     "url": "https://pokeapi.co/api/v2/version/1/"
 *   },
 *   "rarity": 5
 * }
 * </pre>
 *
 * @param version Versión del juego en la que se aplica el detalle (ej. "red",
 *                "blue").
 * @param rarity  Probabilidad de aparición del ítem en esa versión (valor
 *                entero, a mayor número menor rareza).
 */
public record VersionDetailDTO(VersionDTO version, int rarity) {
}
