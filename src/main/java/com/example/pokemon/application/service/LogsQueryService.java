/**
 * 
 */
package com.example.pokemon.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pokemon.adapters.in.web.dto.RequestLogDto;
import com.example.pokemon.adapters.in.web.mapper.RequestLogMapper;
import com.example.pokemon.common.masking.MaskingService;
import com.example.pokemon.domain.model.RequestLogFilterDTO;
import com.example.pokemon.domain.ports.in.LogsQueryUseCase;
import com.example.pokemon.domain.ports.out.LogsQueryPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * LogsQueryService
 *
 * Implementación del puerto de entrada {@link LogsQueryUseCase}.
 * Orquesta:
 *  - Construcción de filtros dinámicos con {@link RequestLogFilterDTO}.
 *  - Consulta paginada de logs mediante {@link LogsQueryPort}.
 *  - Transformación de entidades JPA a DTOs seguros usando {@link RequestLogMapper} con {@link MaskingService}.
 *
 * Buenas prácticas:
 *  - Nunca expone entidades JPA fuera de la capa de persistencia.
 *  - El mapeo y enmascaramiento se delega al mapper con @Context.
 *  - Incluye soporte para paginación y ordenamiento.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LogsQueryService implements LogsQueryUseCase {

    /** Puerto de salida que abstrae la consulta de logs en persistencia. */
    private final LogsQueryPort logsQueryPort;

    /** Mapper puro MapStruct para transformar RequestLog -> RequestLogDto. */
    private final RequestLogMapper requestLogMapper;

    /** Servicio para enmascaramiento y truncado de payloads sensibles. */
    private final MaskingService maskingService;

    /**
     * Consulta logs paginados aplicando filtros opcionales y flag includePayloads.
     *
     * @param ipFilter       Filtra por IP de origen (opcional)
     * @param fromDate       Fecha/hora inicio del filtro (opcional)
     * @param toDate         Fecha/hora fin del filtro (opcional)
     * @param includePayloads Indica si se deben incluir los payloads completos
     * @param pageable       Paginación y ordenamiento
     * @return Página de {@link RequestLogDto} listos para exponer en la API
     */
    @Override
    @Transactional(readOnly = true)
    public Page<RequestLogDto> queryLogs(RequestLogFilterDTO filter, boolean includePayloads, Pageable pageable) {
        log.debug("Consultando logs con filtros={}, includePayloads={}, pageable={}", filter, includePayloads, pageable);

        return logsQueryPort.findByFilters(filter, pageable)
                .map(entity -> requestLogMapper.toDto(entity, maskingService, includePayloads));
    }
	
}
