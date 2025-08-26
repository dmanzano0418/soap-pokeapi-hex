/**
 * 
 */
package com.example.pokemon.adapters.out.persistence;

import java.time.OffsetDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.pokemon.adapters.out.persistence.entity.RequestLog;
import com.example.pokemon.adapters.out.persistence.repo.RequestLogRepository;
import com.example.pokemon.adapters.out.persistence.spec.RequestLogSpecifications;
import com.example.pokemon.domain.model.RequestLogFilterDTO;
import com.example.pokemon.domain.ports.out.LogsQueryPort;
import com.example.pokemon.domain.ports.out.SaveRequestLogPort;

import lombok.RequiredArgsConstructor;

/**
 * RequestLogPersistenceAdapter
 *
 * Adaptador de infraestructura que implementa los puertos de salida:
 * - SaveRequestLogPort: persiste registros.
 * - LogsQueryPort: consulta paginada con filtros dinámicos (Specifications).
 *
 * Detalles:
 * - Centraliza la conversión de RequestLogFilterDTO → Specification.
 * - Evita que la capa de aplicación conozca JPA/Hibernate.
 */
@Component
@RequiredArgsConstructor
public class RequestLogPersistenceAdapter implements SaveRequestLogPort, LogsQueryPort {
	
	/**
     * Repositorio JPA para {@link RequestLog}.
     *
     * Se inyecta automáticamente vía constructor gracias a {@link RequiredArgsConstructor}.
     * Spring Boot detecta esta dependencia porque está anotada con {@link Repository}.
     * Se declara como final para reforzar inmutabilidad y claridad del diseño.
     */
	private final RequestLogRepository requestLogRepository;

	/**
     * Guarda un registro en la BBDD (transacción).
     *
     * @param originIp        dirección IP del cliente que hizo la petición.
     * @param methodName      nombre del método o endpoint invocado.
     * @param requestDate     fecha y hora en la que se recibió la petición.
     * @param durationMs      duración del procesamiento en milisegundos (puede ser null).
     * @param requestPayload  cuerpo del request (puede ser null si no se captura).
     * @param responsePayload cuerpo de la respuesta (puede ser null si no se captura).
     */
	@Override
	@Transactional
	public void saveRequestLog(String originIp,
            String methodName,
            OffsetDateTime requestDate,
            Long durationMs,
            String requestPayload,
            String responsePayload) {
		
		RequestLog log = RequestLog.builder()
				.originIp(originIp)
				.methodName(methodName)
				.requestDate(requestDate != null ? requestDate : OffsetDateTime.now())
				.durationMs(durationMs)
				.requestPayload(requestPayload)
				.responsePayload(responsePayload)
				.build();

		// Persistencia del log en la base de datos.
		requestLogRepository.save(log);
		
	}
	
	 /**
     * Consulta logs aplicando filtros dinámicos encapsulados en {@link RequestLogFilterDTO}.
     * Consulta paginada con filtros; si filter == null || filter.isEmpty() usa findAll(pageable).
     */
    @Override
    @Transactional(readOnly = true)
    public Page<RequestLog> findByFilters(RequestLogFilterDTO filter, Pageable pageable) {
        if (filter == null || filter.isEmpty()) {
            return requestLogRepository.findAll(pageable);
        }

        Specification<RequestLog> spec = RequestLogSpecifications.build(filter);
        if (spec == null) {
            return requestLogRepository.findAll(pageable);
        }
        return requestLogRepository.findAll(spec, pageable);
    }
    
    /**
     * Delegación simple a findAll(pageable).
     */
    @Override
    @Transactional(readOnly = true)
    public Page<RequestLog> findAll(Pageable pageable) {
        return requestLogRepository.findAll(pageable);
    }

}
