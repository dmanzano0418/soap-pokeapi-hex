/**
 * 
 */
package com.example.pokemon.domain.model;

import java.time.OffsetDateTime;

/**
 * RequestLogFilterDTO
 *
 * Value Object inmutable que encapsula criterios de filtrado para las consultas de logs.
 * - ip  : filtro por IP de origen (contains, case-insensitive).
 * - from: fecha/hora inicio (inclusive).
 * - to  : fecha/hora fin (inclusive).
 *
 * Se usa para pasar criterios de Controller -> Service -> PersistenceAdapter sin acoplar
 * al dominio a JPA ni al repositorio.
 */
public record RequestLogFilterDTO(String ip, OffsetDateTime from, OffsetDateTime to) {

    /**
     * Indica si no hay criterios de filtrado.
     *
     * @return true si ip, from y to son null o vacíos
     */
    public boolean isEmpty() {
        return (ip == null || ip.isBlank()) && from == null && to == null;
    }

    /**
     * Factory conveniente para crear instancias.
     *
     * @param ip filtro por IP
     * @param from fecha/hora inicio
     * @param to fecha/hora fin
     * @return nueva instancia de {@link RequestLogFilterDTO}
     */
    public static RequestLogFilterDTO of(String ip, OffsetDateTime from, OffsetDateTime to) {
        return new RequestLogFilterDTO(ip, from, to);
    }

}
