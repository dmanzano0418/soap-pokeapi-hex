/**
 * 
 */
package com.example.pokemon.domain.ports.out;

import java.time.OffsetDateTime;

/**
 * SaveRequestLogPort
 *
 * Puerto de salida para guardar registros de peticiones.
 */
public interface SaveRequestLogPort {

	/**
	 * Guarda un registro de petición.
	 *
	 * @param originIp        IP de origen de la petición (cliente)
	 * @param methodName      nombre del método ejecutado (ej. "abilities")
	 * @param requestDate     fecha/hora de la petición (preferible pasarla desde el
	 *                        endpoint)
	 * @param durationMs      duración en milisegundos (opcional)
	 * @param requestPayload  payload de request (opcional, puede ser null)
	 * @param responsePayload payload de response (opcional, puede ser null)
	 */
	void saveRequestLog(String originIp, String methodName, OffsetDateTime requestDate, Long durationMs,
			String requestPayload, String responsePayload);

}
