/**
 * 
 */
package com.example.pokemon.common.exceptions;

/**
 * Excepción para errores en la comunicación con servicios remotos, como PokeAPI
 * (timeouts, 5xx, errores de red, etc).
 */
public class RemoteClientException extends RuntimeException {

	private static final long serialVersionUID = -1390631018209784609L;

	public RemoteClientException(String message) {
		super(message);
	}

	public RemoteClientException(String message, Throwable cause) {
		super(message, cause);
	}

}
