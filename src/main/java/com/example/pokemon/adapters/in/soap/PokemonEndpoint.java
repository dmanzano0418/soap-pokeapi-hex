/**
 * 
 */
package com.example.pokemon.adapters.in.soap;

import java.util.Optional;

import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.example.pokemon.domain.model.Pokemon;
import com.example.pokemon.domain.ports.in.QueryPokemonUseCase;
import com.example.pokemon.soap.schemas.AbilitiesRequest;
import com.example.pokemon.soap.schemas.AbilitiesResponse;
import com.example.pokemon.soap.schemas.BaseExperienceRequest;
import com.example.pokemon.soap.schemas.BaseExperienceResponse;
import com.example.pokemon.soap.schemas.HeldItemsRequest;
import com.example.pokemon.soap.schemas.HeldItemsResponse;
import com.example.pokemon.soap.schemas.IdRequest;
import com.example.pokemon.soap.schemas.IdResponse;
import com.example.pokemon.soap.schemas.LocationAreaEncountersRequest;
import com.example.pokemon.soap.schemas.LocationAreaEncountersResponse;
import com.example.pokemon.soap.schemas.NameRequest;
import com.example.pokemon.soap.schemas.NameResponse;

import lombok.RequiredArgsConstructor;

/**
 * Endpoint SOAP para exponer operaciones relacionadas con Pokémon.
 * 
 * Esta clase actúa como adaptador de entrada (Inbound Adapter) en la
 * arquitectura hexagonal. Traduce mensajes SOAP (definidos en los XSD/WSDL)
 * a invocaciones del caso de uso {@link QueryPokemonUseCase}, y construye
 * las respuestas SOAP a partir del modelo de dominio.
 * 
 * Características:
 * - Cada método está mapeado a un mensaje definido en el esquema (pokemon.xsd).
 * - Usa el puerto de dominio {@link QueryPokemonUseCase}, sin depender de la
 *   implementación concreta.
 * - Devuelve siempre un objeto de respuesta SOAP con la información solicitada.
 * - Uso de Streams para poblar listas de respuesta.
 * - Envoltorio con Optional para mejorar legibilidad en el manejo de excepciones.
 */
@Endpoint
@RequiredArgsConstructor
public class PokemonEndpoint {

	/**
	 * Namespace definido en el XSD de Pokémon (pokemon.xsd). Debe coincidir
	 * exactamente con el atributo targetNamespace de pokemon.xsd.
	 */
	private static final String NAMESPACE_URI = "http://example.com/pokemon/soap/schemas";

	/** Caso de uso del dominio para consultas de Pokemon. */
	private final QueryPokemonUseCase queryPokemonUseCase;

	// -----------------------------------
	// 🔹 Métodos públicos SOAP
	// -----------------------------------

	/**
	 * Devuelve las habilidades de un Pokémon.
	 * 
	 * @param request mensaje SOAP con el nombre del Pokémon
	 * @return lista de habilidades en el objeto de respuesta
	 */
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "AbilitiesRequest")
	@ResponsePayload
	public AbilitiesResponse abilities(@RequestPayload AbilitiesRequest request, MessageContext messageContext) {
		return handleRequest(request.getName(), messageContext,
				p -> {
					var res = new AbilitiesResponse();
					res.getAbilities().addAll(p.getAbilities());
					return res;
				},
				"habilidades");
	}

	/**
	 * Devuelve la experiencia base de un Pokémon.
	 * 
	 * @param request mensaje SOAP con el nombre del Pokémon
	 * @return experiencia base en el objeto de respuesta
	 */
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "BaseExperienceRequest")
	@ResponsePayload
	public BaseExperienceResponse baseExperience(@RequestPayload BaseExperienceRequest request, MessageContext messageContext) {
		return handleRequest(request.getName(), messageContext,
				p -> {
					var res = new BaseExperienceResponse();
					res.setBaseExperience(p.getBaseExperience());
					return res;
				},
				"experiencia base");
	}

	/**
	 * Devuelve los ítems sostenidos por un Pokémon.
	 * 
	 * @param request mensaje SOAP con el nombre del Pokémon
	 * @return lista de ítems en el objeto de respuesta
	 */
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "HeldItemsRequest")
	@ResponsePayload
	public HeldItemsResponse heldItems(@RequestPayload HeldItemsRequest request, MessageContext messageContext) {
		return handleRequest(request.getName(), messageContext,
				p -> {
					var res = new HeldItemsResponse();
					// Uso de streams para añadir ítems
					p.getHeldItems().forEach(res.getHeldItems()::add);
					return res;
				},
				"ítems sostenidos");
	}

	/**
	 * Devuelve el identificador numérico de un Pokémon.
	 * 
	 * @param request mensaje SOAP con el nombre del Pokémon
	 * @return id numérico en el objeto de respuesta
	 */
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "IdRequest")
	@ResponsePayload
	public IdResponse id(@RequestPayload IdRequest request, MessageContext messageContext) {
		return handleRequest(request.getName(), messageContext,
				p -> {
					var res = new IdResponse();
					res.setId(p.getId());
					return res;
				},
				"ID");
	}

	/**
	 * Devuelve el nombre de un Pokémon (tal cual está en la PokeAPI).
	 * 
	 * @param request mensaje SOAP con el nombre del Pokémon
	 * @return nombre en el objeto de respuesta
	 */
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "NameRequest")
	@ResponsePayload
	public NameResponse name(@RequestPayload NameRequest request, MessageContext messageContext) {
		return handleRequest(request.getName(), messageContext,
				p -> {
					var res = new NameResponse();
					res.setName(p.getName());
					return res;
				},
				"nombre");
	}

	/**
	 * Devuelve las ubicaciones donde puede encontrarse un Pokémon.
	 * 
	 * @param request mensaje SOAP con el nombre del Pokémon
	 * @return lista de ubicaciones en el objeto de respuesta
	 */
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "LocationAreaEncountersRequest")
	@ResponsePayload
	public LocationAreaEncountersResponse locationAreaEncounters(@RequestPayload LocationAreaEncountersRequest request,
			MessageContext messageContext) {
		return handleRequest(request.getName(), messageContext,
				p -> {
					var res = new LocationAreaEncountersResponse();
					// Streams para añadir encounters
					res.getEncounters().addAll(p.getLocationAreaEncounters());
					return res;
				},
				"ubicaciones");
	}
	
	// -----------------------------------
	// 🔹 Método utilitario genérico
	// -----------------------------------

	/**
	 * Maneja las consultas de Pokémon de forma genérica y reduce duplicación.
	 *
	 * @param name nombre del Pokémon solicitado
	 * @param messageContext contexto SOAP
	 * @param mapper función para mapear el Pokémon a una respuesta SOAP
	 * @param entityLabel etiqueta descriptiva para mensajes de error
	 * @return objeto de respuesta SOAP o null si ocurrió un error
	 */
	private <T> T handleRequest(String name, MessageContext messageContext,
			java.util.function.Function<Pokemon, T> mapper, String entityLabel) {

		return Optional.ofNullable(name)
				.map(n -> {
					try {
						var pokemon = queryPokemonUseCase.getPokemon(n);
						return mapper.apply(pokemon);
					} catch (RuntimeException ex) {
						SoapFaultHelper.createClientFault(
								messageContext,
								"No se pudo obtener " + entityLabel + " del Pokémon: " + n,
								ex);
						return null;
					}
				})
				.orElseGet(() -> {
					SoapFaultHelper.createClientFault(
							messageContext,
							"El nombre del Pokémon no puede ser nulo",
							new IllegalArgumentException("Name is null"));
					return null;
				});
	}

}
