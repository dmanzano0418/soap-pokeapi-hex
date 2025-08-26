/**
 * 
 */
package com.example.pokemon.adapters.out.pokeapi.dto;

import java.util.List;

/**
 * DTO que representa una ubicación donde puede encontrarse un Pokémon, tal como
 * lo devuelve la API pública de PokeAPI.
 *
 * Este objeto modela un "encounter" (encuentro en el mapa del juego).
 *
 * Ejemplo JSON (simplificado de la API PokeAPI):
 * 
 * <pre>
 * {
 *   "location_area": {
 *     "name": "kanto-route-2-south-towards-viridian-city",
 *     "url": "https://pokeapi.co/api/v2/location-area/296/"
 *   },
 *   "version_details": [
 *     {
 *       "version": { "name": "red", "url": "..." },
 *       "max_chance": 30
 *     }
 *   ]
 * }
 * </pre>
 *
 * @param location_area   Área de ubicación donde aparece el Pokémon.
 * @param version_details Lista de detalles de aparición según versión del juego
 *                        (ej. Red, Blue, Yellow).
 */
public record EncounterDTO(LocationAreaDTO location_area, List<EncounterDetailDTO> version_details) {
}
