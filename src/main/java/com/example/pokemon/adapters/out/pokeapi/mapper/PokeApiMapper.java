/**
 * 
 */
package com.example.pokemon.adapters.out.pokeapi.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import com.example.pokemon.adapters.out.pokeapi.dto.AbilityEntryDTO;
import com.example.pokemon.adapters.out.pokeapi.dto.EncounterDTO;
import com.example.pokemon.adapters.out.pokeapi.dto.HeldItemEntryDTO;
import com.example.pokemon.adapters.out.pokeapi.dto.PokeApiPokemonDTO;
import com.example.pokemon.domain.model.Pokemon;

/**
 * PokeApiMapper (MapStruct)
 *
 * - Convierte los DTOs de la PokeAPI (paquete adapters.out.pokeapi.dto) al modelo
 *   de dominio {@link Pokemon}.
 * - Centraliza la lógica de extracción/flatten de listas anidadas como:
 *     - abilities: List<AbilityEntryDTO>  -> List<String> (names)
 *     - held_items: List<HeldItemEntryDTO> -> List<String> (item names)
 *     - location_area_encounters: List<EncounterDTO> -> List<String> (location_area names)
 *
 * Notas:
 * - Usamos {@link Named} helpers para que MapStruct invoque esos métodos al mapear
 *   las propiedades complejas. Esto evita tener que escribir `expression = "java(...)"`.
 * - El mapper es null-safe: devuelve listas vacías cuando la entrada es null.
 * - `unmappedTargetPolicy = ReportingPolicy.IGNORE` evita warnings por campos no mapeados
 *   (ajusta si quieres ser estricto).
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PokeApiMapper {
	
	/**
     * Mapea el DTO raíz {@link PokeApiPokemonDTO} al dominio {@link Pokemon}.
     *
     * Mapeos explícitos:
     *  - base_experience -> baseExperience
     *  - abilities -> abilities (usa mapAbilities)
     *  - held_items -> heldItems (usa mapHeldItems)
     *  - location_area_encounters -> locationAreaEncounters (usa mapEncounters)
     *
     * MapStruct hará el resto (id, name) automáticamente si los nombres coinciden.
     *
     * @param dto DTO obtenido de PokeAPI
     * @return instancia de dominio {@link Pokemon}
     */
    @Mapping(source = "base_experience", target = "baseExperience")
    @Mapping(source = "abilities", target = "abilities", qualifiedByName = "mapAbilities")
    @Mapping(source = "held_items", target = "heldItems", qualifiedByName = "mapHeldItems")
    @Mapping(source = "location_area_encounters", target = "locationAreaEncounters", qualifiedByName = "mapEncounters")
    Pokemon toDomain(PokeApiPokemonDTO dto);

    // -------------------------
    // Helpers (named) usados por MapStruct
    // -------------------------

    /**
     * Convierte una lista de AbilityEntryDTO a una lista de nombres (String).
     *
     * @param list lista de AbilityEntryDTO (puede ser null)
     * @return lista no nula de nombres de habilidades
     */
    @Named("mapAbilities")
    default List<String> mapAbilities(List<AbilityEntryDTO> list) {
        if (list == null || list.isEmpty()) return Collections.emptyList();
        return list.stream()
                .filter(Objects::nonNull)
                .map(a -> {
                    try {
                        return a.ability() != null ? a.ability().name() : null;
                    } catch (Exception ex) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una lista de HeldItemEntryDTO a una lista de nombres de item (String).
     *
     * @param list lista de HeldItemEntryDTO (puede ser null)
     * @return lista no nula de nombres de ítems
     */
    @Named("mapHeldItems")
    default List<String> mapHeldItems(List<HeldItemEntryDTO> list) {
        if (list == null || list.isEmpty()) return Collections.emptyList();
        return list.stream()
                .filter(Objects::nonNull)
                .map(h -> {
                    try {
                        return h.item() != null ? h.item().name() : null;
                    } catch (Exception ex) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Convierte la lista de EncounterDTO (location_area_encounters) a una lista de nombres
     * de location_area (String).
     *
     * @param list lista de EncounterDTO (puede ser null)
     * @return lista no nula de nombres de location_area
     */
    @Named("mapEncounters")
    default List<String> mapEncounters(List<EncounterDTO> list) {
        if (list == null || list.isEmpty()) return Collections.emptyList();
        return list.stream()
                .filter(Objects::nonNull)
                .map(e -> {
                    try {
                        return e.location_area() != null ? e.location_area().name() : null;
                    } catch (Exception ex) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
