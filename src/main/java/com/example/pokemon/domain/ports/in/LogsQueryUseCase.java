/**
 * 
 */
package com.example.pokemon.domain.ports.in;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.pokemon.adapters.in.web.dto.RequestLogDto;
import com.example.pokemon.application.service.LogsQueryService;
import com.example.pokemon.domain.model.RequestLogFilterDTO;

/**
 * LogsQueryUseCase
 *
 * Puerto de entrada de la capa de aplicación para consultar logs. Define la
 * operación principal de consulta paginada y filtrada de logs, devolviendo DTOs
 * de salida en lugar de la entidad JPA. Separa la API pública de la lógica de
 * aplicación de los detalles de infraestructura.
 *
 * Implementaciones: - {@link LogsQueryService} implementa esta interfaz usando
 * un puerto de salida
 *
 * Buenas prácticas: - El controller solo interactúa con este puerto. - La
 * entidad JPA no se expone nunca directamente fuera del adaptador de
 * persistencia.
 */
public interface LogsQueryUseCase {

	/**
	 * Consulta paginada de logs.
	 *
	 * @param filter          criterios de búsqueda
	 * @param includePayloads si true, se incluyen payloads en el resultado
	 *                        (aplicando enmascaramiento)
	 * @param pageable        configuración de paginación
	 * @return Página de logs transformada a {@link RequestLogDto}
	 */
	Page<RequestLogDto> queryLogs(RequestLogFilterDTO filter, boolean includePayloads, Pageable pageable);

}
