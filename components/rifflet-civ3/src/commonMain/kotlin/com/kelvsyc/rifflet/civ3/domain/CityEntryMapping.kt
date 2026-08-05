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
