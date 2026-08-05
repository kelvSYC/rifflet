package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.CityEntry
import com.kelvsyc.rifflet.civ3.LeadEntry

/**
 * Converts a parsed `CITY` section to its domain-layer form.
 *
 * [races]/[buildings] are the already domain-converted `RACE`/`BLDG` lists; [leads] stays a wire
 * type (`LEAD` doesn't have a domain type yet). The caller is responsible for supplying `races`/
 * `buildings` from wherever is appropriate for this file — this file's own sections converted via
 * their own `toDomain()`, or an externally-sourced standard ruleset's, when Custom Rules is off
 * (`CITY`, gated by the separate Custom Map toggle, can exist without them).
 *
 * Throws [IllegalArgumentException] if any entry's `ownerType` is outside the documented `0..3`
 * range — see [resolveOwner]'s own KDoc.
 */
fun List<CityEntry>.toDomain(
    races: List<Race>,
    leads: List<LeadEntry>,
    buildings: List<Building>,
): List<City> = map { entry ->
    City(
        name = entry.name,
        x = entry.x,
        y = entry.y,
        owner = resolveOwner(entry.ownerType, entry.owner, races, leads),
        buildings = entry.buildingIds.map { buildings.getOrNull(it) }.toMutableList(),
        culture = entry.culture,
        size = entry.size,
        cityLevel = entry.cityLevel,
        borderLevel = entry.borderLevel,
        hasWalls = entry.hasWalls.toInt() != 0,
        hasPalace = entry.hasPalace.toInt() != 0,
        useAutoName = entry.useAutoName != 0,
    )
}

/**
 * Converts a `CITY` section's domain-layer form back to wire entries, resolving each [City]'s
 * object references back into indices.
 *
 * Throws [IllegalArgumentException] if any cross-reference resolves to an object not present in
 * the corresponding list argument. `owner`/`buildings` use `indexOf` — structural-equality
 * matches, not true reference identity, the same accepted limitation already documented on
 * GOVT's/TECH's/BLDG's/PRTO's `toWire()`.
 *
 * [Owner.None]/[Owner.Barbarian] write back `-1` for the wire `owner` int — see [Owner]'s own
 * KDoc for why the original raw value (if any) can't be reconstructed.
 */
fun List<City>.toWire(
    races: List<Race>,
    leads: List<LeadEntry>,
    buildings: List<Building>,
): List<CityEntry> = map { city ->
    val (ownerType, owner) = when (val o = city.owner) {
        is Owner.None -> 0 to -1
        is Owner.Barbarian -> 1 to -1
        is Owner.Civilization -> 2 to (
            o.race?.let {
                val index = races.indexOf(it)
                require(index >= 0) { "Owner.Civilization references a Race not present in races" }
                index
            } ?: -1
            )
        is Owner.Player -> 3 to (
            o.lead?.let {
                val index = leads.indexOf(it)
                require(index >= 0) { "Owner.Player references a LeadEntry not present in leads" }
                index
            } ?: -1
            )
    }
    val buildingIds = city.buildings.map { building ->
        building?.let {
            val index = buildings.indexOf(it)
            require(index >= 0) { "City.buildings references a Building not present in buildings" }
            index
        } ?: -1
    }

    CityEntry(
        hasWalls = if (city.hasWalls) 1 else 0,
        hasPalace = if (city.hasPalace) 1 else 0,
        name = city.name,
        ownerType = ownerType,
        buildingIds = buildingIds,
        culture = city.culture,
        owner = owner,
        size = city.size,
        x = city.x,
        y = city.y,
        cityLevel = city.cityLevel,
        borderLevel = city.borderLevel,
        useAutoName = if (city.useAutoName) 1 else 0,
    )
}
