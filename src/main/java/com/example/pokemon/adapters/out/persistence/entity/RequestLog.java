/**
 * 
 */
package com.example.pokemon.adapters.out.persistence.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * RequestLog (JPA Entity)
 *
 * Entidad JPA que representa un registro de petición HTTP/soap recibido por la aplicación.
 *
 * Campos principales:
 * - id              : PK autogenerada.
 * - originIp        : dirección IP de origen.
 * - requestDate     : fecha/hora (OffsetDateTime) de la petición.
 * - methodName      : nombre del método/endpoint invocado.
 * - durationMs      : duración en milisegundos (nullable).
 * - requestPayload  : cuerpo del request (Lob, nullable).
 * - responsePayload : cuerpo de la respuesta (Lob, nullable).
 *
 * Notas:
 * - Los campos requestPayload/responsePayload están marcados como {@link LOB} para soportar grandes cuerpos JSON/XML.
 * - OffsetDateTime es mapeado por Hibernate (asegurarse de la versión de driver/hibernate).
 * - Usamos Lombok para reducir boilerplate (constructor no-args necesario para JPA).
 */
@Entity
@Table(name = "request_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestLog {

	 /**
     * Identificador único del log.
     * Se genera automáticamente usando estrategia de identidad (auto-increment).
     */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Dirección IP de origen que inició la petición. Se guarda como String para
	 * admitir IPv4/IPv6.
	 */
	@Column(name = "origin_ip", nullable = true)
    private String originIp;

	/**
	 * Fecha y hora en la que se recibió la petición (con offset).
	 */
	@Column(name = "request_date", nullable = false)
    private OffsetDateTime requestDate;

	/**
	 * Nombre del método ejecutado o ruta invocada. Ej: "abilities", "getPokemon".
	 */
	@Column(name = "method_name", nullable = true)
    private String methodName;

	/**
	 * Duración de la petición en milisegundos (opcional).
	 */
	@Column(name = "duration_ms", nullable = true)
    private Long durationMs;

	/**
	 * Payload entrante (request), almacenado como LOB. Puede quedar null si no se
	 * captura.
	 */
	@Lob
    @Column(name = "request_payload", columnDefinition = "CLOB")
    private String requestPayload;

	/**
	 * Payload de respuesta (response), almacenado como LOB. Puede quedar null si no
	 * se captura.
	 */
	@Lob
    @Column(name = "response_payload", columnDefinition = "CLOB")
    private String responsePayload;

}
