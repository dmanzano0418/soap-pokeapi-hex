/**
 * 
 */
package com.example.pokemon.adapters.in.rest;

import java.time.OffsetDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pokemon.adapters.in.web.dto.RequestLogDto;
import com.example.pokemon.domain.model.RequestLogFilterDTO;
import com.example.pokemon.domain.ports.in.LogsQueryUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * LogsController
 *
 * Controlador REST para exponer operaciones de consulta de logs.
 * - Expone endpoint GET /api/logs con filtros, paginación y flag includePayloads.
 * - Usa OpenAPI 3 para documentación.
 */
@RestController
@RequestMapping("/api/logs")
@Tag(name = "Logs", description = "Operaciones para consultar logs de peticiones")
@RequiredArgsConstructor
@Slf4j
public class LogsController {

	/** 
	 * Puerto de entrada que maneja la lógica de consultas de logs.
	 */
    private final LogsQueryUseCase logsQueryUseCase;

    @Operation(summary = "Consulta paginada de logs con filtros opcionales",
            description = "Permite filtrar por IP y rango de fechas. "
                        + "Por defecto oculta payloads salvo que se indique includePayloads=true.")
    @GetMapping
    public Page<RequestLogDto> getLogs(
            @RequestParam(required = false) String ip,
            @RequestParam(required = false) OffsetDateTime fromDate,
            @RequestParam(required = false) OffsetDateTime toDate,
            @RequestParam(defaultValue = "false") boolean includePayloads,
            Pageable pageable) {

        log.debug("Request GET /api/logs con ip={}, from={}, to={}, includePayloads={}, pageable={}",
                ip, fromDate, toDate, includePayloads, pageable);

        RequestLogFilterDTO filter = RequestLogFilterDTO.of(ip, fromDate, toDate);
        return logsQueryUseCase.queryLogs(filter, includePayloads, pageable);
    }

}

	