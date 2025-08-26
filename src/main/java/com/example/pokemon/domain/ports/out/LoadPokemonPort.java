/**
 * 
 */
package com.example.pokemon.domain.ports.out;

import com.example.pokemon.domain.model.Pokemon;

/**
 * 
 */
public interface LoadPokemonPort {
	
	Pokemon loadByName(String name);

}
