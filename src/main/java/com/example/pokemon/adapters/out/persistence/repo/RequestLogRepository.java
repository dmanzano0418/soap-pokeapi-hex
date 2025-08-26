/**
 * 
 */
package com.example.pokemon.adapters.out.persistence.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.pokemon.adapters.out.persistence.entity.RequestLog;

/**
 * RequestLogRepository
 *
 * Repositorio Spring Data JPA para la entidad {@link RequestLog}.
 * - Extiende JpaRepository para CRUD y paginación.
 * - Extiende JpaSpecificationExecutor para ejecutar Specifications dinámicas.
 *
 * No definimos métodos derivables adicionales aquí porque usamos Specifications
 * (flexibilidad y menor cantidad de firmas).
 */
@Repository
public interface RequestLogRepository extends JpaRepository<RequestLog, Long>, JpaSpecificationExecutor<RequestLog> {
}
