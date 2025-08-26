/**
 * 
 */
package com.example.pokemon.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * MaskingProperties
 *
 * Clase de configuración que encapsula las propiedades relacionadas con
 * enmascaramiento y truncado de payloads.
 *
 * Fuente de valores: application.yml → app.masking.*
 *
 * Propiedades:
 * - maxPayloadLength: longitud máxima permitida del payload antes de truncarlo.
 * - sensitiveKeywords: lista de palabras clave cuyo valor debe enmascararse.
 *
 * Validaciones:
 * - maxPayloadLength debe ser >= 1.
 * - sensitiveKeywords no puede ser nula (puede estar vacía si no deseas enmascarar nada).
 *
 * Ejemplo de configuración  en application.yml:
 *
 * <pre>
 * app:
 *   masking:
 *     max-payload-length: 1024
 *     sensitive-keywords:
 *       - password
 *       - token
 *       - authorization
 * </pre>
 */
@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "app.masking")
public class MaskingProperties {
	
	/**
     * Longitud máxima de payloads antes de truncarlos.
     * Ejemplo: si el valor es 1024 y el payload tiene 5000 caracteres,
     * solo se mostrarán los primeros 1024 + sufijo "...[TRUNCATED]".
     *
     * Default recomendado: 1024
     */
    @NotNull
    @Min(1)
    private Integer maxPayloadLength = 1024;

    /**
     * Palabras clave que deben considerarse sensibles al momento de
     * aplicar enmascaramiento.
     *
     * Ejemplo:
     *   ["password", "token", "authorization", "ssn"]
     *
     * Si en un JSON existe: "password":"abc123"
     * será reemplazado por: "password":"[MASKED]".
     *
     * Nota:
     *  - El matching es case-insensitive.
     *  - Se aplica tanto en JSON como en cadenas planas.
     */
    @NotNull
    @NotEmpty(message = "Debe configurarse al menos una palabra clave sensible en 'app.masking.sensitive-keywords'")
    private List<String> sensitiveKeywords;

}
