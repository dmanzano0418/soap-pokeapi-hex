/**
 * 
 */
package com.example.pokemon.adapters.in.soap;

import java.util.Locale;

import javax.xml.namespace.QName;

import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapMessage;

import com.example.pokemon.soap.schemas.ErrorDetail;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;

/**
 * Helper para construir SOAP Faults con {@link ErrorDetail}.
 * 
 * Este helper permite centralizar la creación de faults y evita duplicar
 * el código de JAXB en cada endpoint.
 */
public final class SoapFaultHelper {
	
	private SoapFaultHelper() {
        // Constructor privado: clase utilitaria
    }

    /**
     * Crea un SOAP Fault tipo "Client" con detalle estructurado {@link ErrorDetail}.
     *
     * @param messageContext contexto del mensaje SOAP
     * @param userMessage mensaje legible para el consumidor
     * @param ex excepción capturada
     */
    public static void createClientFault(MessageContext messageContext, String userMessage, Exception ex) {
        try {
            SoapMessage response = (SoapMessage) messageContext.getResponse();
            SoapBody body = response.getSoapBody();

            // Crear el fault de tipo CLIENT
            SoapFault fault = body.addClientOrSenderFault(userMessage, Locale.ENGLISH);

            // Crear el detalle del fault
            SoapFaultDetail detail = fault.addFaultDetail();

            // Construir objeto ErrorDetail (JAXB)
            ErrorDetail errorDetail = new ErrorDetail();
            errorDetail.setErrorMessage(userMessage);
            errorDetail.setCause(ex != null ? ex.getMessage() : null);

            // Marshalling JAXB
            JAXBContext jaxbContext = JAXBContext.newInstance(ErrorDetail.class);
            Marshaller marshaller = jaxbContext.createMarshaller();

            QName detailQName = new QName("http://example.com/pokemon/soap/errors", "ErrorDetail");
            JAXBElement<ErrorDetail> jaxbElement = new JAXBElement<>(detailQName, ErrorDetail.class, errorDetail);

            marshaller.marshal(jaxbElement, detail.getResult());

        } catch (Exception e) {
            throw new RuntimeException("Error construyendo SOAP Fault con ErrorDetail", e);
        }
    }

}
