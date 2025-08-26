/**
 * 
 */
package com.example.pokemon.adapters.out.pokeapi.dto;

/**
 * DTO que representa los detalles de un encuentro con un Pokémon, tal como se
 * expone en PokeAPI.
 *
 * Este objeto se utiliza dentro de {@link EncounterDTO} para indicar en qué
 * condiciones y con qué niveles un Pokémon puede aparecer.
 *
 * Ejemplo JSON (fragmento de la API):
 * 
 * <pre>
 * {
 *   "method": {
 *     "name": "walk",
 *     "url": "https://pokeapi.co/api/v2/encounter-method/1/"
 *   },
 *   "min_level": 3,
 *   "max_level": 7
 * }
 * </pre>
 *
 * @param method    Método por el cual se encuentra al Pokémon (ej. caminar,
 *                  surfear, pescar).
 * @param min_level Nivel mínimo en que puede aparecer el Pokémon.
 * @param max_level Nivel máximo en que puede aparecer el Pokémon.
 */
public record EncounterDetailDTO(EncounterMethodDTO method, int min_level, int max_level) {
}
