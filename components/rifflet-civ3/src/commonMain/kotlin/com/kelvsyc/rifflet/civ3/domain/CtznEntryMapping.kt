package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.CtznEntry

/**
 * Converts a parsed `CTZN` section to its domain-layer form.
 *
 * [techs] is the already domain-converted `TECH` list. No `require()` guards: the "exactly one
 * default, default has no prerequisite" invariants are already enforced by
 * `validateCtznDefaultCount`/`validateCtznDefaultPrerequisite` at the wire layer.
 */
fun List<CtznEntry>.toDomain(techs: List<Tech>): List<Citizen> = map { entry ->
    Citizen(
        singularName = entry.singularName,
        pluralName = entry.pluralName,
        civilopediaEntry = entry.civilopediaEntry,
        isDefault = entry.defaultCitizen != 0,
        prerequisite = techs.getOrNull(entry.prerequisite),
        luxuries = entry.luxuries,
        research = entry.research,
        taxes = entry.taxes,
        corruption = entry.corruption,
        construction = entry.construction,
    )
}

/**
 * Converts a `CTZN` section's domain-layer form back to wire entries.
 *
 * Throws [IllegalArgumentException] if [Citizen.prerequisite] resolves to an object not present
 * in [techs] — `indexOf`-based, the same accepted structural-equality limitation as every other
 * `toWire()` in this codebase. A `null` [Citizen.prerequisite] writes back `-1`.
 */
fun List<Citizen>.toWire(techs: List<Tech>): List<CtznEntry> = map { citizen ->
    val prerequisiteIndex = citizen.prerequisite?.let {
        val index = techs.indexOf(it)
        require(index >= 0) { "Citizen.prerequisite references a Tech not present in techs" }
        index
    } ?: -1
    CtznEntry(
        defaultCitizen = if (citizen.isDefault) 1 else 0,
        singularName = citizen.singularName,
        civilopediaEntry = citizen.civilopediaEntry,
        pluralName = citizen.pluralName,
        prerequisite = prerequisiteIndex,
        luxuries = citizen.luxuries,
        research = citizen.research,
        taxes = citizen.taxes,
        corruption = citizen.corruption,
        construction = citizen.construction,
    )
}
