/**
 * 
 */
package com.example.pokemon.adapters.out.pokeapi.dto;

import java.util.List;

/**
 * DTO que representa un ítem que puede sostener un Pokémon en batalla, junto
 * con las versiones del juego donde aplica.
 *
 * Ejemplo JSON (fragmento de la API):
 * 
 * <pre>
 * {
 *   "item": {
 *     "name": "oran-berry",
 *     "url": "https://pokeapi.co/api/v2/item/132/"
 *   },
 *   "version_details": [
 *     {
 *       "version": { "name": "ruby", "url": "..." },
 *       "rarity": 50
 *     }
 *   ]
 * }
 * </pre>
 *
 * @param item            Objeto con información básica del ítem (nombre y URL).
 * @param version_details Lista de detalles por versión del juego, incluyendo
 *                        rareza y disponibilidad.
 */
public record HeldItemEntryDTO(ItemDTO item, List<VersionDetailDTO> version_details) {
}
