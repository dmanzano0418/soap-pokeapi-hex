# SOAP Pok��mon Hexagonal Service

Servicio **SOAP** basado en **arquitectura hexagonal** que expone operaciones para consultar informaci��n de Pok��mon consumiendo la **PokeAPI**.  
Desarrollado con **Java 17**, **Spring Boot**, **Spring Web Services**, **JAXB (XSD �� Java)** y herramientas modernas (MapStruct, Lombok, H2 para DEV).

---

## ?? Tabla de Contenido

1. [Descripci��n del Proyecto](#-descripci��n-del-proyecto)
2. [Manual de Negocio](#-manual-de-negocio)
3. [Manual T��cnico](#-manual-t��cnico)  
   - [Arquitectura](#arquitectura)  
   - [Estructura de Paquetes](#estructura-de-paquetes)  
   - [Dependencias Principales](#dependencias-principales)  
   - [Compilaci��n y Generaci��n de Clases JAXB](#compilaci��n-y-generaci��n-de-clases-jaxb)  
   - [Ejecuci��n del Proyecto](#ejecuci��n-del-proyecto)  
   - [Notas de despliegue / IDE](#notas-de-despliegue--ide)  
4. [Endpoints SOAP Disponibles](#-endpoints-soap-disponibles)  
5. [Ejemplos de Requests y Responses (reales)](#-ejemplos-de-requests-y-responses-reales)  
   - [Formato de Fault enriquecido con `ErrorDetail`](#formato-de-fault-enriquecido-con-errordetail)  
6. [C��mo probar (SoapUI / Postman / curl)](#-c��mo-probar-soapui--postman--curl)  
7. [Troubleshooting / Preguntas frecuentes](#troubleshooting--preguntas-frecuentes)  
8. [Diagrama (Mermaid) - Opcional](#diagrama-mermaid---opcional)  
9. [Contacto / Contribuir](#contacto--contribuir)

---

## ?? Descripci��n del Proyecto

`soap-pokeapi-hex` es un servicio SOAP que ofrece operaciones para obtener datos de Pok��mon (habilidades, experiencia base, ��tems sostenidos, id, nombre, ubicaciones). Internamente consulta la **PokeAPI** (adaptador de salida) y presenta los resultados en contratos SOAP definidos por `pokemon.xsd` y `error-detail.xsd`. Implementa un manejo de errores **estructurado** (elemento `ErrorDetail`) para adjuntar informaci��n t��cnica y amigable en Faults SOAP.

---

## ?? Manual de Negocio

- **Prop��sito:** Exponer informaci��n de Pok��mon v��a SOAP para clientes que requieran este protocolo (sistemas legacy / integraciones B2B).
- **Casos de uso principales:**
  - Obtener habilidades por `name`
  - Obtener experiencia base por `name`
  - Obtener ��tems sostenidos por `name`
  - Obtener id por `name`
  - Obtener nombre can��nico por `name`
  - Obtener location area encounters por `name`
- **Errores:** Si la PokeAPI falla o el dominio no encuentra al Pok��mon se devuelve un **SOAP Fault** con un `ErrorDetail` (namespace `http://example.com/pokemon/soap/errors`) que contiene `errorMessage` y `cause`.

---

## ??? Manual T��cnico

### Arquitectura

Se sigue la **arquitectura hexagonal (Ports & Adapters)**:

- **Domain**: entidades + interfaces (ports).
- **Application**: servicios que implementan la l��gica (use cases).
- **Adapters in**: SOAP endpoint (`PokemonEndpoint`), REST controller (logs).
- **Adapters out**: PokeApi HTTP client, persistence (JPA/H2).
- **Infra/config**: WebServiceConfig, WebClientConfig, SoapExceptionConfig, etc.

### Estructura de Paquetes

com.example.pokemon
������ SoapPokeapiHexApplication # Clase principal
��
������ adapters
�� ������ in
�� �� ������ rest
�� �� �� ������ LogsController
�� �� ������ soap
�� �� �� ������ GlobalSoapExceptionResolver
�� �� �� ������ PokemonEndpoint
�� �� �� ������ SoapFaultHelper
�� �� ������ web
�� �� ������ dto/RequestLogDto
�� �� ������ mapper/RequestLogMapper
�� ��
�� ������ out
�� ������ persistence
�� �� ������ RequestLogPersistenceAdapter
�� �� ������ entity/RequestLog
�� �� ������ repo/RequestLogRepository
�� �� ������ spec/RequestLogSpecifications
�� ������ pokeapi
�� ������ PokeApiClient
�� ������ dto/... (AbilityDTO, PokeApiPokemonDTO, ...)
�� ������ mapper/PokeApiMapper
��
������ application
�� ������ service
�� ������ LogsQueryService
�� ������ QueryPokemonService
��
������ common
�� ������ exceptions (PokemonNotFoundException, RemoteClientException)
�� ������ masking/MaskingService
��
������ config
�� ������ MaskingProperties
�� ������ OpenApiConfig
�� ������ PropertiesConfig
�� ������ SoapExceptionConfig
�� ������ WebClientConfig
�� ������ WebServiceConfig
��
������ domain
�� ������ model (Pokemon, RequestLogFilterDTO)
�� ������ ports (in/out)
��
������ resources
������ wsdl/pokemon.xsd
������ wsdl/error-detail.xsd
������ application.yml


---

### Dependencias Principales

En el `pom.xml` del proyecto (resumen):

- `org.springframework.boot:spring-boot-starter-web`
- `org.springframework.boot:spring-boot-starter-web-services` (Spring-WS)
- `org.springframework.boot:spring-boot-starter-data-jpa`
- `org.springframework.boot:spring-boot-starter-webflux` (para llamadas a PokeAPI)
- `org.mapstruct:mapstruct` + `mapstruct-processor`
- `org.projectlombok:lombok`
- `org.springframework.boot:spring-boot-configuration-processor` (opcional)
- `org.jvnet.jaxb:jaxb-maven-plugin:4.0.9` (o `org.jvnet.jaxb` plugin 4.x) para generaci��n JAXB en JDK 17+
- `org.glassfish.jaxb:jaxb-runtime` / `jakarta.xml.bind` si es necesario (revisar plugin config)

> **Nota:** En Java 17 debes usar plugins y artefactos compatibles con Jakarta / JAXB 4.x. En tu POM actual est��s usando `org.jvnet.jaxb:jaxb-maven-plugin:4.0.9`, que es correcto.

---

### Compilaci��n y Generaci��n de Clases JAXB

Las clases Java generadas a partir de `pokemon.xsd` y `error-detail.xsd` quedan en:

target/generated-sources/jaxb

target/generated-sources/jaxb


**Comandos recomendados**:

- Generar fuentes y compilar todo (forma simple):
```bash
mvn clean compile


Forzar s��lo la generaci��n JAXB (opcional):

mvn org.jvnet.jaxb:jaxb-maven-plugin:4.0.9:generate


Generar + compilar + ejecutar tests:

mvn clean verify


El build-helper-maven-plugin (si est�� configurado) a?ade target/generated-sources/jaxb al classpath del compilador para que las clases generadas est��n disponibles durante compile.

Ejecuci��n del Proyecto

Compilar y generar fuentes:

mvn clean install


Ejecutar (desde IDE o terminal):

mvn spring-boot:run


�� o ejecutar la clase principal com.example.pokemon.SoapPokeapiHexApplication.

URLs ��tiles (por defecto):

Endpoint base SOAP: http://localhost:8080/ws

WSDL (si se expone con DefaultWsdl11Definition): http://localhost:8080/ws/pokemon.wsdl

Si tu WebServiceConfig define otra ruta o nombre de WSDL, ajusta las URLs.

Notas de despliegue / IDE

En IntelliJ: mvn clean compile + Reload Maven Project para que se reconozcan las clases generadas.

En Eclipse (m2e): puede requerir ejecutar mvn generate-sources fuera del IDE o habilitar el m2e connector.

Si la compilaci��n no incluye las clases JAXB, ejecuta mvn clean compile -X para debug y revisa que target/generated-sources/jaxb exista y est�� lleno.

?? Endpoints SOAP Disponibles

Operaci��n	LocalPart (Request element)	Response element	Namespace
Abilities	AbilitiesRequest	AbilitiesResponse	http://example.com/pokemon/soap/schemas
BaseExperience	BaseExperienceRequest	BaseExperienceResponse	http://example.com/pokemon/soap/schemas
HeldItems	HeldItemsRequest	HeldItemsResponse	http://example.com/pokemon/soap/schemas
Id	IdRequest	IdResponse	http://example.com/pokemon/soap/schemas
Name	NameRequest	NameResponse	http://example.com/pokemon/soap/schemas
LocationAreaEncounters	LocationAreaEncountersRequest	LocationAreaEncountersResponse	http://example.com/pokemon/soap/schemas

Los elementos y tipos est��n definidos en src/main/resources/wsdl/pokemon.xsd. Las respuestas usan el paquete Java generado com.example.pokemon.soap.schemas.

?? Ejemplos de Requests y Responses (reales)

En todos los ejemplos el namespace usado por el servicio SOAP es:
http://example.com/pokemon/soap/schemas (prefijo pok en ejemplos)

1) Abilities

Request

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:pok="http://example.com/pokemon/soap/schemas">
  <soapenv:Header/>
  <soapenv:Body>
    <pok:AbilitiesRequest>
      <pok:name>pikachu</pok:name>
    </pok:AbilitiesRequest>
  </soapenv:Body>
</soapenv:Envelope>


Response

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:pok="http://example.com/pokemon/soap/schemas">
  <soapenv:Body>
    <pok:AbilitiesResponse>
      <pok:abilities>static</pok:abilities>
      <pok:abilities>lightning-rod</pok:abilities>
    </pok:AbilitiesResponse>
  </soapenv:Body>
</soapenv:Envelope>

2) BaseExperience

Request

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:pok="http://example.com/pokemon/soap/schemas">
  <soapenv:Body>
    <pok:BaseExperienceRequest>
      <pok:name>bulbasaur</pok:name>
    </pok:BaseExperienceRequest>
  </soapenv:Body>
</soapenv:Envelope>


Response

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:pok="http://example.com/pokemon/soap/schemas">
  <soapenv:Body>
    <pok:BaseExperienceResponse>
      <pok:baseExperience>64</pok:baseExperience>
    </pok:BaseExperienceResponse>
  </soapenv:Body>
</soapenv:Envelope>

3) HeldItems

Request

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:pok="http://example.com/pokemon/soap/schemas">
  <soapenv:Body>
    <pok:HeldItemsRequest>
      <pok:name>pikachu</pok:name>
    </pok:HeldItemsRequest>
  </soapenv:Body>
</soapenv:Envelope>


Response

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:pok="http://example.com/pokemon/soap/schemas">
  <soapenv:Body>
    <pok:HeldItemsResponse>
      <pok:heldItems>oran-berry</pok:heldItems>
    </pok:HeldItemsResponse>
  </soapenv:Body>
</soapenv:Envelope>

4) Id

Request

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:pok="http://example.com/pokemon/soap/schemas">
  <soapenv:Body>
    <pok:IdRequest>
      <pok:name>charmander</pok:name>
    </pok:IdRequest>
  </soapenv:Body>
</soapenv:Envelope>


Response

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:pok="http://example.com/pokemon/soap/schemas">
  <soapenv:Body>
    <pok:IdResponse>
      <pok:id>4</pok:id>
    </pok:IdResponse>
  </soapenv:Body>
</soapenv:Envelope>

5) Name

Request

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:pok="http://example.com/pokemon/soap/schemas">
  <soapenv:Body>
    <pok:NameRequest>
      <pok:name>pikachu</pok:name>
    </pok:NameRequest>
  </soapenv:Body>
</soapenv:Envelope>


Response

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:pok="http://example.com/pokemon/soap/schemas">
  <soapenv:Body>
    <pok:NameResponse>
      <pok:name>pikachu</pok:name>
    </pok:NameResponse>
  </soapenv:Body>
</soapenv:Envelope>

6) LocationAreaEncounters

Request

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:pok="http://example.com/pokemon/soap/schemas">
  <soapenv:Body>
    <pok:LocationAreaEncountersRequest>
      <pok:name>pikachu</pok:name>
    </pok:LocationAreaEncountersRequest>
  </soapenv:Body>
</soapenv:Envelope>


Response

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:pok="http://example.com/pokemon/soap/schemas">
  <soapenv:Body>
    <pok:LocationAreaEncountersResponse>
      <pok:encounters>kanto-route-2-south-towards-viridian-city</pok:encounters>
    </pok:LocationAreaEncountersResponse>
  </soapenv:Body>
</soapenv:Envelope>

Formato de Fault enriquecido con ErrorDetail

Cuando ocurre un error (ej. PokemonNotFoundException o fallo remoto), el endpoint devuelve un SOAP Fault cuyo detail incluye un elemento err:ErrorDetail (namespace http://example.com/pokemon/soap/errors) con campos errorMessage y cause.

Ejemplo de SOAP Fault

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:err="http://example.com/pokemon/soap/errors">
  <soapenv:Body>
    <soapenv:Fault>
      <faultcode>soap:Client</faultcode>
      <faultstring>No se pudo obtener las habilidades del Pok��mon: unknown</faultstring>
      <detail>
        <err:ErrorDetail>
          <err:errorMessage>No se pudo obtener las habilidades del Pok��mon: unknown</err:errorMessage>
          <err:cause>PokemonNotFoundException: Pokemon 'unknown' not found</err:cause>
        </err:ErrorDetail>
      </detail>
    </soapenv:Fault>
  </soapenv:Body>
</soapenv:Envelope>


ErrorDetail est�� definido en error-detail.xsd y su namespace es http://example.com/pokemon/soap/errors.

El helper SoapFaultHelper del proyecto se encarga de crear este fault (marshall JAXB + attach to SoapFaultDetail).

?? C��mo probar (SoapUI / Postman / curl)
SoapUI

Abrir SoapUI �� New SOAP Project.

WSDL URL: http://localhost:8080/ws/pokemon.wsdl (ajusta seg��n tu configuraci��n).

SoapUI importar�� los operations; abre la request AbilitiesRequest y pega el XML del ejemplo.

Ejecutar y revisar la respuesta / fault.

Postman

Nueva Request �� m��todo: POST

URL: http://localhost:8080/ws

Headers:

Content-Type: text/xml;charset=UTF-8

SOAPAction: "" (opcional; no siempre requerido)

Body �� raw �� pegar el XML del request.

Send �� revisar el body de la respuesta.

curl
curl -s -X POST \
  -H "Content-Type: text/xml;charset=UTF-8" \
  --data-binary @abilities-request.xml \
  http://localhost:8080/ws


(abilities-request.xml contiene el XML del request)

Troubleshooting / Preguntas frecuentes

Las clases JAXB no aparecen:

Verifica target/generated-sources/jaxb existe y contiene paquetes.

Ejecuta mvn org.jvnet.jaxb:jaxb-maven-plugin:4.0.9:generate o mvn clean compile.

Aseg��rate de que build-helper-maven-plugin o la configuraci��n del plugin agreguen el directorio como source.

Error javax/xml/bind/annotation/XmlSchema / NoClassDefFoundError:

Indica conflicto de JAXB (JDK �� 11). Usa el plugin y dependencias compatibles con Jakarta JAXB 4.x (org.glassfish.jaxb 4.x / jakarta.xml.bind-api 4.x). El plugin jaxb-maven-plugin:4.0.9 es la opci��n moderna.

IDE no compila pero mvn s��:

Forzar actualizaci��n de proyecto maven en IDE (IntelliJ: Maven �� Reimport; Eclipse: Update Maven Project).

Ejecuta mvn clean generate-sources y refresh del IDE.

Error con spring-configuration-metadata.json vac��o:

Si aparece Invalid additional meta-data... End of input at character 0 puede ser que spring-boot-configuration-processor haya generado un archivo vac��o. Revisa logs del compilador y limpia target/classes/META-INF.

Diagrama (Mermaid) - Opcional

flowchart TB
    UI[Cliente SOAP / SoapUI / Postman]
    UI -->|SOAP Request| Endpoint[PokemonEndpoint (Spring-WS)]
    Endpoint -->|UseCase| AppService[QueryPokemonService]
    AppService -->|Port| PokeApiClient[PokeApiClient (WebClient)]
    AppService -->|Port| Repo[RequestLogPersistenceAdapter (JPA/H2)]
    PokeApiClient -->|HTTP| PokeAPI[PokeAPI (external)]


