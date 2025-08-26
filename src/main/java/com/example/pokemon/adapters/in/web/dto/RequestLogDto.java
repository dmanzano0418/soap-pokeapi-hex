/**
 * 
 */
package com.example.pokemon.adapters.in.web.dto;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * RequestLogDto
 *
 * DTO inmutable que representa un registro de petición para exponer en respuestas HTTP/REST.
 *
 * Motivo:
 * - Usar un DTO inmutable (record) evita exponer la entidad JPA y hace más seguro el flujo
 *   Controller → Service → View.
 * - El DTO contiene solo los campos que la API quiere exponer; los campos sensibles pueden
 *   ser enmascarados/truncados por el mapper antes de construir este DTO.
 *
 * JSON behaviour:
 * - Campos null no se incluyen en la respuesta (gracias a {@link JsonInclude}).
 * - {@code requestDate} se serializa en formato ISO-8601 con offset.
 *
 * Uso recomendado:
 * - Por defecto exponer solo metadatos. Exponer payloads solo bajo flag explícito
 *   (includePayloads=true) o a través de un endpoint con autorización administrativa.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "RequestLog", description = "Registro de petición (metadatos y payloads opcionales).")
public record RequestLogDto(
        
		@Schema(description = "Identificador interno del log", example = "123", required = true)
        Long id,

        @JsonProperty("originIp")
        @Schema(description = "Dirección IP origen de la petición", example = "192.168.0.10")
        String originIp,

        @JsonProperty("requestDate")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        @Schema(description = "Fecha y hora de la petición en formato ISO-8601 con offset (ej: 2025-08-22T14:30:00-06:00)",
                example = "2025-08-22T14:30:00-06:00", required = true)
        OffsetDateTime requestDate,

        @JsonProperty("methodName")
        @Schema(description = "Nombre del método/endpoint invocado (ej: 'abilities')", example = "abilities")
        String methodName,

        @JsonProperty("durationMs")
        @Schema(description = "Duración en milisegundos, si está disponible", example = "27")
        Long durationMs,

        /**
         * Nota: estos campos pueden contener datos sensibles.
         * - En producción se recomienda devolverlos enmascarados/truncados o omitirlos.
         * - El mapper debe aplicar la política de masking (configurable).
         */
        @JsonProperty("requestPayload")
        @Schema(description = "Payload de la petición (puede estar enmascarado o truncado)",
                example = "{\"username\":\"[REDACTED]\",\"action\":\"fight\"}")
        String requestPayload,

        @JsonProperty("responsePayload")
        @Schema(description = "Payload de la respuesta (puede estar enmascarado o truncado)",
                example = "{\"result\":\"ok\"}")
        String responsePayload
) {}
