/**
 * 
 */
package com.example.pokemon.adapters.out.pokeapi.dto;

/**
 * DTO que representa una entrada de habilidad de un Pokémon, tal como lo
 * devuelve la API pública de PokeAPI.
 *
 * Ejemplo JSON (fragmento de la API):
 * 
 * <pre>
* {
*   "ability": {
*     "name": "static",
*     "url": "https://pokeapi.co/api/v2/ability/9/"
*   },
*   "is_hidden": false,
*   "slot": 1
* }
 * </pre>
 *
 * @param ability   Objeto con información básica de la habilidad (nombre y
 *                  URL).
 * @param is_hidden Indica si la habilidad es oculta (true = hidden ability).
 * @param slot      Orden de la habilidad (1, 2, o habilidad oculta).
 */
public record AbilityEntryDTO(AbilityDTO ability, boolean is_hidden, int slot) {
}
