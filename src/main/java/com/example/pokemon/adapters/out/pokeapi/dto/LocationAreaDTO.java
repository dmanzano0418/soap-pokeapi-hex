/**
 * 
 */
package com.example.pokemon.adapters.out.pokeapi.dto;

/**
 * DTO que representa un área de ubicación dentro del mundo del juego donde
 * puede encontrarse un Pokémon (parte de un "encounter").
 *
 * Ejemplo JSON (fragmento de la API):
 * 
 * <pre>
 * {
 *   "name": "kanto-route-2-south-towards-viridian-city",
 *   "url": "https://pokeapi.co/api/v2/location-area/296/"
 * }
 * </pre>
 *
 * @param name Nombre del área de ubicación (ej. "kanto-route-2...").
 * @param url  URL del recurso en PokeAPI para más detalles.
 */
public record LocationAreaDTO(String name, String url) {
}
