/**
 * 
 */
package com.example.pokemon.common.exceptions;

/**
 * 
 */
public class PokemonNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -3720453010713477564L;

	public PokemonNotFoundException(String message) {
		super(message);
	}

	public PokemonNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
