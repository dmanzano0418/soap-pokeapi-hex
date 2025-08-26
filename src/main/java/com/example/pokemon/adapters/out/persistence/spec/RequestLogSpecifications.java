/**
 * 
 */
package com.example.pokemon.adapters.out.persistence.spec;

import java.time.OffsetDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.example.pokemon.adapters.out.persistence.entity.RequestLog;
import com.example.pokemon.domain.model.RequestLogFilterDTO;

/**
 * RequestLogSpecifications
 *
 * Factoría de Specifications (JPA Criteria) para la entidad RequestLog.
 * Los métodos son null-safe y composables.
 *
 * - byIp(ip): "contains" case-insensitive sobre originIp.
 * - byDateRange(from,to): >= from && <= to (aplica solo criterios presentes)
 * - build(filter): arma la Specification compuesta a partir del RequestLogFilterDTO.
 *
 * Importante: retornamos null para condiciones que no aplican para facilitar la composición
 * con Specification#and (o construimos una spec neutra con cb.conjunction()).
 */
public final class RequestLogSpecifications {

    private RequestLogSpecifications() {
        // Evita instanciación
    }

    /** Filtro por IP (contains, case-insensitive). */
    public static Specification<RequestLog> byIp(String ip) {
        if (ip == null || ip.isBlank()) {
            return null;
        }
        String like = "%" + ip.toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("originIp")), like);
    }

    /**
     * Filtro por rango de fecha/hora (inclusive). Retorna null si ambos son nulos.
     */
    public static Specification<RequestLog> byDateRange(OffsetDateTime from, OffsetDateTime to) {
        if (from == null && to == null) return null;

        return (root, query, cb) -> {
            if (from != null && to != null) {
                return cb.between(root.get("requestDate"), from, to);
            } else if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("requestDate"), from);
            } else {
                return cb.lessThanOrEqualTo(root.get("requestDate"), to);
            }
        };
    }

    /**
     * Construye la Specification compuesta a partir del DTO de filtros.
     * - Si filter es null o vacío, devuelve null (caller puede detectar y usar findAll(pageable)).
     */
    public static Specification<RequestLog> build(RequestLogFilterDTO filter) {
        if (filter == null || filter.isEmpty()) {
            return null;
        }

        Specification<RequestLog> spec = null;

        Specification<RequestLog> ipSpec = byIp(filter.ip());
        Specification<RequestLog> dateSpec = byDateRange(filter.from(), filter.to());

        if (ipSpec != null) {
            spec = ipSpec;
        }
        if (dateSpec != null) {
            spec = (spec == null) ? dateSpec : spec.and(dateSpec);
        }

        return spec;
    }

}
