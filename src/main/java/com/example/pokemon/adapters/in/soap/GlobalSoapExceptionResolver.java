/**
 * 
 */
package com.example.pokemon.adapters.in.soap;

import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;

/**
 * GlobalSoapExceptionResolver
 * ---------------------------
 * Resolver global de errores para endpoints SOAP (Spring-WS).
 *
 * Responsabilidades:
 *  - Intercepta cualquier excepción arrojada por un @Endpoint SOAP.
 *  - Deja que la superclase maneje la lógica general de mapeo (códigos de fault, etc.).
 *  - Enriquecce el SOAP Fault agregando un bloque <detail> con contenido XML
 *    estructurado, alineado con el esquema `error-detail.xsd`:
 *
 *      <detail>
 *        <ErrorDetail xmlns="http://example.com/pokemon/soap/schemas">
 *          <errorMessage>...</errorMessage>
 *          <cause>...</cause>
 *        </ErrorDetail>
 *      </detail>
 *
 * Notas importantes:
 *  - No se utilizan métodos propios de SAAJ/Jakarta (como setFaultString), porque
 *    Spring-WS expone su propia API (org.springframework.ws.soap.SoapFault) y el
 *    contrato aquí es personalizar el detalle, no construir el Fault a mano.
 *  - Para lograr una estructura anidada bajo <ErrorDetail>, escribimos un fragmento
 *    XML usando un Transformer sobre el Result del SoapFaultDetailElement.
 *  - Se escapan caracteres especiales de XML en los textos de error para evitar
 *    XML malformado.
 */
public class GlobalSoapExceptionResolver extends SoapFaultMappingExceptionResolver {
	
	private static final String NS = "http://example.com/pokemon/soap/schemas";
    private static final QName ERROR_DETAIL_QNAME = new QName(NS, "ErrorDetail");

	/**
     * Sobrescribe la personalización del Fault SOAP.
     *
     * @param endpoint endpoint que procesaba la petición
     * @param ex       excepción capturada
     * @param fault    objeto SOAP Fault que será devuelto al cliente
     */
	@Override
	protected void customizeFault(Object endpoint, Exception ex, SoapFault fault) {
		// 1) Delega en la superclase para mapeos/códigos genéricos de fault
        super.customizeFault(endpoint, ex, fault);

        // 2) Crea el contenedor <detail>
        SoapFaultDetail detail = fault.addFaultDetail();

        // 3) Crea el nodo raíz <ErrorDetail> en el namespace del esquema de errores
        SoapFaultDetailElement errorDetailElement = detail.addFaultDetailElement(ERROR_DETAIL_QNAME);

        // 4) Prepara los textos de error y construye el XML anidado (escapando caracteres)
        String errorMessage = ex.getMessage() != null ? ex.getMessage() : "Unknown error";
        String causeMessage = (ex.getCause() != null && ex.getCause().getMessage() != null)
                ? ex.getCause().getMessage()
                : "No cause available";

        String xml =
                "<ErrorDetail xmlns=\"" + NS + "\">" +
                    "<errorMessage>" + escapeXml(errorMessage) + "</errorMessage>" +
                    "<cause>" + escapeXml(causeMessage) + "</cause>" +
                "</ErrorDetail>";

        // 5) Inserta el fragmento XML dentro del detalle usando Transformer
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StreamSource source = new StreamSource(new StringReader(xml));
            Result result = errorDetailElement.getResult();
            transformer.transform(source, result);
        } catch (Exception transformError) {
            // 6) Fallback: si no se puede transformar, agrega un texto simple
            errorDetailElement.addText("Error serializing fault detail: " + transformError.getMessage());
        }
        
    }
	
	/**
     * Escapa caracteres especiales de XML para evitar generar contenido malformado.
     * Reemplaza: & < > " '
     *
     * @param in Texto de entrada (posiblemente null).
     * @return Texto escapado y seguro para insertar dentro de XML.
     */
    private static String escapeXml(String in) {
        if (in == null) return "";
        String out = in;
        out = out.replace("&", "&amp;");
        out = out.replace("<", "&lt;");
        out = out.replace(">", "&gt;");
        out = out.replace("\"", "&quot;");
        out = out.replace("'", "&apos;");
        return out;
    }

}
