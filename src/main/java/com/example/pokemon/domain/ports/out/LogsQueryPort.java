/**
 * 
 */
package com.example.pokemon.domain.ports.out;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.pokemon.adapters.out.persistence.entity.RequestLog;
import com.example.pokemon.domain.model.RequestLogFilterDTO;

/**
 * LogsQueryPort
 *
 * Puerto de salida: la capa de aplicación lo usa para consultar logs.
 * Impl: RequestLogPersistenceAdapter.
 */
public interface LogsQueryPort {
	
	/**
     * Helper simple: findAll pageable (puede implementarse delegando a repo.findAll(pageable)).
     */
    Page<RequestLog> findAll(Pageable pageable);

	 /**
     * Consulta paginada usando filtros dinámicos encapsulados en {@link RequestLogFilterDTO}.
     *
     * @param filter   DTO de criterios (IP, rango de fechas)
     * @param pageable paginación y orden
     * @return página de logs
     */
    Page<RequestLog> findByFilters(RequestLogFilterDTO filter, Pageable pageable);
    
}
