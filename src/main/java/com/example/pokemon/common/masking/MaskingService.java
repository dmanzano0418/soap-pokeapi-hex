/**
 * 
 */
package com.example.pokemon.common.masking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.pokemon.config.MaskingProperties;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * MaskingService
 *
 * Servicio responsable de aplicar políticas de enmascaramiento y truncado sobre
 * payloads de texto antes de exponerlos (por ejemplo, en respuestas de API o logs).
 *
 * Qué hace:
 * - Reemplaza valores asociados a palabras sensibles (p. ej., "password", "token")
 *   por el literal "[MASKED]" conservando la estructura original (JSON, query-string, etc.).
 * - Recorta (trunca) el resultado si excede una longitud máxima configurable,
 *   agregando el sufijo "...[TRUNCATED]".
 *
 * Rendimiento:
 * - Compila las expresiones regulares una sola vez en {@link #init()} (al iniciar el bean).
 * - Las reglas compiladas son inmutables y thread-safe para uso concurrente.
 *
 * Integración:
 * - Usado por RequestLogMapper (vía MapStruct con @Context y expresiones) para
 *   producir DTOs sin exponer información sensible.
 * - Puedes reutilizarlo en cualquier otra capa para sanitizar datos antes de serializar.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MaskingService {
	
	/** 
	 * Propiedades externas cargadas desde application.yml (app.masking.*).
	 */
    private final MaskingProperties properties;
    
    /**
     * Reglas compiladas (regex + plantilla de reemplazo).
     * Se calculan en {@link #init()} a partir de {@link #properties}.
     */
    private List<CompiledRule> compiledRules = Collections.emptyList();

    // ---------- Ciclo de vida ----------

    /**
     * Inicializa y compila las reglas de enmascaramiento una sola vez.
     * Llamado automáticamente por el contenedor de Spring al construir el bean.
     */
    @PostConstruct
    void init() {
        final List<String> keywords = normalizeKeywords(properties.getSensitiveKeywords());
        final List<CompiledRule> rules = new ArrayList<>();

        for (String kw : keywords) {
            // 1) JSON con valores entre comillas:  "password": "valor"
            //    Grupos: (prefijo) ("password": ") (valor) (sufijo) (")
            rules.add(new CompiledRule(
                Pattern.compile("(?i)(\"\\s*" + Pattern.quote(kw) + "\\s*\"\\s*:\\s*\")([^\"]*)(\")"),
                "$1[MASKED]$3"
            ));

            // 2) JSON con valor no-quoted:  "token": abc123
            //    Grupos: (prefijo) ("token": ) (valor) (delimitado por , } o espacio)
            rules.add(new CompiledRule(
                Pattern.compile("(?i)(\"\\s*" + Pattern.quote(kw) + "\\s*\"\\s*:\\s*)([^,}\\s]+)"),
                "$1\"[MASKED]\""
            ));

            // 3) Query string / form: token=abc123 (& o fin)
            rules.add(new CompiledRule(
                Pattern.compile("(?i)(\\b" + Pattern.quote(kw) + "\\b\\s*=\\s*)([^&\\s]+)"),
                "$1[MASKED]"
            ));

            // 4) Texto genérico "clave: valor" (no JSON)
            rules.add(new CompiledRule(
                Pattern.compile("(?i)(\\b" + Pattern.quote(kw) + "\\b\\s*:\\s*)(\\S+)"),
                "$1[MASKED]"
            ));
        }

        this.compiledRules = Collections.unmodifiableList(rules);

        log.debug("MaskingService inicializado. keywords={}, maxLen={}",
                keywords, properties.getMaxPayloadLength());
    }

    // ---------- API pública ----------

    /**
     * Aplica enmascaramiento + truncado sobre un input.
     *
     * Política:
     *  - Si el input es null o vacío → se devuelve tal cual.
     *  - Se enmascaran los valores asociados a las palabras clave configuradas.
     *  - Se recorta a la longitud máxima configurada, agregando el sufijo "...[TRUNCATED]".
     *
     * @param input texto original (puede ser JSON, query-string u otro)
     * @return texto enmascarado y posiblemente truncado
     */
    public String mask(String input) {
        if (!StringUtils.hasText(input)) {
            return input;
        }
        String masked = applyMaskingRules(input);
        return truncateIfNeeded(masked, properties.getMaxPayloadLength());
    }

    // ---------- Implementación interna ----------

    /**
     * Aplica en orden todas las reglas compiladas sobre el texto.
     * Cada regla preserva el "prefijo" (clave y separadores) y sustituye el valor por "[MASKED]".
     */
    private String applyMaskingRules(String text) {
        String out = text;
        for (CompiledRule rule : compiledRules) {
            Matcher m = rule.pattern().matcher(out);
            out = m.replaceAll(rule.replacement());
        }
        return out;
    }

    /**
     * Trunca el texto si excede la longitud máxima configurada.
     * La operación se realiza por puntos de código para evitar cortar pares surrogates.
     */
    private String truncateIfNeeded(String text, int maxLen) {
        if (maxLen <= 0 || text == null) return text;
        if (text.length() <= maxLen) return text;

        // Truncado seguro por puntos de código (evita cortar Unicode)
        int endIndex = text.offsetByCodePoints(0, Math.min(text.codePointCount(0, text.length()), maxLen));
        return text.substring(0, endIndex) + "...[TRUNCATED]";
    }

    /**
     * Normaliza y filtra la lista de keywords (trim, descarta vacíos/nulos).
     */
    private static List<String> normalizeKeywords(List<String> raw) {
        if (raw == null || raw.isEmpty()) return Collections.emptyList();
        List<String> out = new ArrayList<>(raw.size());
        for (String s : raw) {
            if (StringUtils.hasText(s)) out.add(s.trim());
        }
        return Collections.unmodifiableList(out);
    }

    // ---------- Tipos auxiliares ----------

    /**
     * Regla compilada: patrón + plantilla de reemplazo.
     * Ejemplo de replacement: "$1[MASKED]$3" para conservar prefijo y sufijo capturados.
     */
    private record CompiledRule(Pattern pattern, String replacement) {
        CompiledRule {
            Objects.requireNonNull(pattern, "pattern");
            Objects.requireNonNull(replacement, "replacement");
        }
    }

}
