/**
 * 
 */
package com.example.pokemon.domain.ports.in;

import java.util.List;

import com.example.pokemon.domain.model.Pokemon;

/**
 * Caso de uso de consulta de Pokémon.
 *
 * Este puerto define las operaciones de lectura que el mundo exterior (por
 * ejemplo, el endpoint SOAP) puede invocar.
 *
 * Contrato: - Cada método realiza una consulta en tiempo real contra la
 * PokeAPI. - Si el nombre de Pokémon es nulo o vacío, se lanzará
 * IllegalArgumentException. - Si el Pokémon no existe, la implementación
 * lanzará PokemonNotFoundException. - Si ocurre un problema de red o servidor
 * remoto, lanzará RemoteClientException. - Las colecciones devueltas son
 * inmutables para proteger el dominio.
 */
public interface QueryPokemonUseCase {

	/**
	 * Obtiene el agregado de dominio completo del Pokémon.
	 * 
	 * @param name nombre del Pokémon (no nulo/ni vacío)
	 * @return agregado de dominio {@link Pokemon}
	 */
	Pokemon getPokemon(String name);

	/**
	 * Habilidades del Pokémon.Obtiene la lista de habilidades del Pokémon.
	 * 
	 * @param name nombre del Pokémon (no nulo/ni vacío)
	 * @return lista inmutable de nombres de habilidades
	 */
	List<String> getAbilities(String name);

	/**
	 * Obtiene la experiencia base del Pokémon.
	 *
	 * @param name nombre del Pokémon (no nulo/ni vacío)
	 * @return experiencia base (entero)
	 */
	int getBaseExperience(String name);

	/**
	 * Obtiene los ítems que el Pokémon puede sostener.
	 *
	 * @param name nombre del Pokémon (no nulo/ni vacío)
	 * @return lista inmutable de nombres de ítems
	 */
	List<String> getHeldItems(String name);

	/**
	 * Obtiene el identificador único del Pokémon.
	 *
	 * @param name nombre del Pokémon (no nulo/ni vacío)
	 * @return id numérico
	 */
	int getId(String name);

	/**
	 * Obtiene las áreas o ubicaciones donde puede encontrarse el Pokémon.
	 *
	 * @param name nombre del Pokémon (no nulo/ni vacío)
	 * @return lista inmutable de nombres de áreas/ubicaciones
	 */
	List<String> getLocationAreaEncounters(String name);

}
