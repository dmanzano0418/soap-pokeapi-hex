/**
 * 
 */
package com.example.pokemon.domain.model;

import java.util.List;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

/**
 * Domain model: Pokemon
 *
 * Representa un Pokémon en el dominio de negocio de la aplicación,
 * desacoplado del formato original de la PokeAPI.
 *
 * - Inmutable: anotada con {@link Value}, lo que genera:
 *   - campos privados y finales
 *   - constructor all-args
 *   - getters
 *   - equals/hashCode/toString
 * - Builder: se genera un patrón Builder gracias a {@link Builder}.
 *   Con {@link Singular} se pueden agregar elementos individuales
 *   en lugar de pasar listas completas.
 * - toBuilder: permite clonar y modificar instancias fácilmente.
 *
 * Campos:
 *  - id: identificador único del Pokémon en la PokeAPI
 *  - name: nombre del Pokémon (ej: "pikachu")
 *  - baseExperience: experiencia base que otorga al ser derrotado
 *  - abilities: lista plana de habilidades (ej: "static", "lightning-rod")
 *  - heldItems: lista plana de objetos que puede llevar (ej: "oran-berry")
 *  - locationAreaEncounters: lista de áreas donde puede encontrarse
 */
@Value
@Builder(toBuilder = true)
public class Pokemon {

    /**
     * Identificador único del Pokémon en la PokeAPI.
     */
    Integer id;

    /**
     * Nombre del Pokémon (ejemplo: "pikachu").
     */
    String name;

    /**
     * Experiencia base otorgada al derrotar a este Pokémon.
     */
    Integer baseExperience;

    /**
     * Lista de habilidades asociadas al Pokémon.
     * Ejemplo: ["static", "lightning-rod"].
     */
    @Singular("ability")
    List<String> abilities;

    /**
     * Lista de objetos que el Pokémon puede llevar consigo.
     * Ejemplo: ["oran-berry", "light-ball"].
     */
    @Singular("heldItem")
    List<String> heldItems;

    /**
     * Lista de áreas de encuentro (lugares donde el Pokémon aparece).
     * Ejemplo: ["kanto-route-2-south-towards-viridian-city"].
     */
    @Singular("encounter")
    List<String> locationAreaEncounters;

}
