
## 🧼 soap-pokemon-hex

Servicio SOAP (Spring Web Services) que actúa como fachada hacia la PokeAPI para consultar información de Pokémon.
Implementa arquitectura hexagonal con adaptadores de entrada (SOAP, REST), salida (PokeAPI, JPA) y casos de uso del dominio.
Incluye manejo estandarizado de errores SOAP con err:ErrorDetail (definido en error-detail.xsd).

📑 Tabla de Contenido

Descripción del Proyecto

Funcionalidades

Arquitectura y Estructura de Paquetes

Requisitos Previos

Compilación y Generación de Clases JAXB

Cómo Levantar el Proyecto

Endpoints SOAP Disponibles

Ejemplos de Requests y Responses

Abilities

BaseExperience

HeldItems

Id

Name

LocationAreaEncounters

Ejemplo de Fault con ErrorDetail

Pruebas con SoapUI o Postman

Manual Técnico

Manual de Negocio

📖 Descripción del Proyecto

El proyecto soap-pokemon-hex expone un servicio SOAP que consulta datos de Pokémon desde la PokeAPI
.
Usa Spring Boot 3, Spring Web Services, WebClient, JPA/Hibernate y MapStruct, siguiendo arquitectura hexagonal.

El servicio expone operaciones para devolver:

Habilidades (abilities)

Experiencia base (baseExperience)

Ítems sostenidos (heldItems)

Identificador (id)

Nombre (name)

Áreas de encuentro (encounters)

Los errores se retornan como SOAP Fault con detalle err:ErrorDetail normalizado.

⚡ Funcionalidades

Servicio SOAP contract-first con XSD en src/main/resources/wsdl/.

Generación JAXB automática desde pokemon.xsd y error-detail.xsd.

Consumo de PokeAPI con WebClient.

Manejo de errores SOAP enriquecidos (Fault Detail con ErrorDetail).

Persistencia de logs en H2 (por defecto) vía JPA.

Swagger/OpenAPI para los endpoints REST de logs.

Propiedades externas (application.yml) con soporte de masking.

🏗️ Arquitectura y Estructura de Paquetes
com.example.pokemon
 ├── SoapPokeapiHexApplication
 ├── adapters
 │   ├── in
 │   │   ├── rest
 │   │   │   └── LogsController
 │   │   └── soap
 │   │       ├── GlobalSoapExceptionResolver
 │   │       ├── PokemonEndpoint
 │   │       └── SoapFaultHelper
 │   ├── out
 │   │   ├── persistence
 │   │   │   ├── RequestLogPersistenceAdapter
 │   │   │   ├── entity
 │   │   │   │   └── RequestLog
 │   │   │   ├── repo
 │   │   │   │   └── RequestLogRepository
 │   │   │   └── spec
 │   │   │       └── RequestLogSpecifications
 │   │   └── pokeapi
 │   │       ├── PokeApiClient
 │   │       ├── dto
 │   │       │   ├── AbilityDTO, AbilityEntryDTO, EncounterDetailDTO, EncounterDTO
 │   │       │   ├── EncounterMethodDTO, HeldItemEntryDTO, ItemDTO
 │   │       │   ├── LocationAreaDTO, PokeApiPokemonDTO, VersionDetailDTO, VersionDTO
 │   │       └── mapper
 │   │           └── PokeApiMapper
 ├── application
 │   └── service
 │       ├── LogsQueryService
 │       └── QueryPokemonService
 ├── common
 │   ├── exceptions
 │   │   ├── PokemonNotFoundException
 │   │   └── RemoteClientException
 │   └── masking
 │       └── MaskingService
 ├── config
 │   ├── MaskingProperties
 │   ├── OpenApiConfig
 │   ├── PropertiesConfig
 │   ├── SoapExceptionConfig
 │   ├── WebClientConfig
 │   └── WebServiceConfig
 └── domain
     ├── model
     │   ├── Pokemon
     │   └── RequestLogFilterDTO
     ├── ports
     │   ├── in
     │   │   ├── LogsQueryUseCase
     │   │   └── QueryPokemonUseCase
     │   └── out
     │       ├── LoadPokemonPort
     │       ├── LogsQueryPort
     │       └── SaveRequestLogPort


Recursos relevantes: