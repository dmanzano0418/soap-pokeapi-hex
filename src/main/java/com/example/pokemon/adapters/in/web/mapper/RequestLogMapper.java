/**
 * 
 */
package com.example.pokemon.adapters.in.web.mapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.example.pokemon.adapters.in.web.dto.RequestLogDto;
import com.example.pokemon.adapters.out.persistence.entity.RequestLog;
import com.example.pokemon.common.masking.MaskingService;

/**
 * RequestLogMapper (MapStruct)
 *
 * Mapea {@link RequestLog} -> {@link RequestLogDto}.
 *
 * - Usa @Context MaskingService y el flag includePayloads para decidir
 *   si retornar payloads enmascarados, truncados o completamente ocultos.
 * - No intenta mutar el DTO; en su lugar las expresiones (computePayload)
 *   construyen la cadena final que se pasará al constructor del record.
 *
 * Notas de implementación:
 * - MapStruct genera la implementación concreta.
 * - El método computePayload es un helper default que MapStruct puede invocar
 *   desde la expresión Java en @Mapping (aquí usamos expression="java(...)").
 * - @Context permite pasar servicios externos (como MaskingService) y variables (includePayloads)
 *  al mapper sin depender de Spring.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface RequestLogMapper {

	/**
     * MapStruct mapping: RequestLog -> RequestLogDto
     *
     * Las dos líneas below crean las cadenas finales para requestPayload / responsePayload
     * invocando al helper computePayload(...) que usa maskingService e includePayloads.
     */
    @Mapping(target = "requestPayload",
             expression = "java(computePayload(entity.getRequestPayload(), maskingService, includePayloads))")
    @Mapping(target = "responsePayload",
             expression = "java(computePayload(entity.getResponsePayload(), maskingService, includePayloads))")
    RequestLogDto toDto(RequestLog entity, @Context MaskingService maskingService, @Context boolean includePayloads);

    /**
     * Helper que aplica la política:
     * - si payload == null -> null
     * - si includePayloads == false -> "[REDACTED]"
     * - si includePayloads == true -> maskingService.mask(payload)
     *
     * Lo declaramos default para que MapStruct pueda referenciarlo desde 'expression'.
     */
    default String computePayload(String payload, @Context MaskingService maskingService, @Context boolean includePayloads) {
        if (payload == null) return null;
        if (!includePayloads) return "[REDACTED]";
        return maskingService.mask(payload);
    }
    
}
