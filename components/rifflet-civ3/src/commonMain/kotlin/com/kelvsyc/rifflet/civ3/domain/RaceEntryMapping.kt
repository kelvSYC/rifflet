package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.RaceEntry
import com.kelvsyc.rifflet.civ3.RaceGovernor as WireRaceGovernor
import com.kelvsyc.rifflet.civ3.RacePersonality as WireRacePersonality

/**
 * Converts a parsed `RACE` section to its domain-layer form, resolving
 * [RaceEntry.personality]'s `GOVT` cross-refs against [governments] (already domain-converted —
 * see [Government]), [RaceEntry.unitTypeForKing] against [units] (already domain-converted `PRTO`
 * — see [Prto]), and each of [RaceEntry.freeTechs]'s 4 slots against [techs] (already
 * domain-converted `TECH` — see [Tech]), preserving position.
 */
fun List<RaceEntry>.toDomain(
    techs: List<Tech>,
    governments: List<Government>,
    units: List<Prto>,
): List<Race> = map { entry ->
    Race(
        name = entry.name,
        civilopediaEntry = entry.civilopediaEntry,
        adjective = entry.adjective,
        noun = entry.noun,
        leader = entry.leader,
        cultureGroup = entry.cultureGroup,
        civilizationGender = entry.civilizationGender,
        personality = RacePersonality(
            favoriteGovernment = governments.getOrNull(entry.personality.favoriteGovernment),
            shunnedGovernment = governments.getOrNull(entry.personality.shunnedGovernment),
            aggressionLevel = entry.personality.aggressionLevel,
        ),
        uniqueCivilizationCounter = entry.uniqueCivilizationCounter,
        defaultColor = entry.defaultColor,
        uniqueColor = entry.uniqueColor,
        freeTechs = entry.freeTechs.map { techs.getOrNull(it) }.toMutableList(),
        bonuses = entry.bonuses,
        governor = RaceGovernor(
            settings = entry.governor.settings,
            buildNever = entry.governor.buildNever,
            buildOften = entry.governor.buildOften,
        ),
        plurality = entry.plurality,
        unitTypeForKing = units.getOrNull(entry.unitTypeForKing),
        flavors = entry.flavors,
        unknown = entry.unknown,
        diplomacyTextIndex = entry.diplomacyTextIndex,
        cityNames = entry.cityNames,
        greatLeaderNames = entry.greatLeaderNames,
        scientificLeaderNames = entry.scientificLeaderNames,
        eras = entry.eras,
    )
}

/**
 * Converts a `RACE` section's domain-layer form back to wire entries, resolving each [Race]'s
 * object references back into indices against [techs]/[governments]/[units] and this list's own
 * roster.
 *
 * Throws [IllegalArgumentException] if [RacePersonality.favoriteGovernment],
 * [RacePersonality.shunnedGovernment], [Race.unitTypeForKing], or any non-null [Race.freeTechs]
 * slot references an object not present in the corresponding list argument — a dangling reference
 * at encode time is a real bug, not something to default silently. [Race.freeTechs] itself is
 * never reordered — whatever occupies each slot is exactly what's written, preserving fidelity
 * for real files that don't front-pack their free techs (confirmed via corpus survey).
 */
fun List<Race>.toWire(
    techs: List<Tech>,
    governments: List<Government>,
    units: List<Prto>,
): List<RaceEntry> = map { race ->
    val favoriteGovernmentIndex = race.personality.favoriteGovernment?.let { government ->
        val index = governments.indexOf(government)
        require(index >= 0) { "RacePersonality.favoriteGovernment references a Government not present in governments" }
        index
    } ?: -1
    val shunnedGovernmentIndex = race.personality.shunnedGovernment?.let { government ->
        val index = governments.indexOf(government)
        require(index >= 0) { "RacePersonality.shunnedGovernment references a Government not present in governments" }
        index
    } ?: -1
    val unitTypeForKingIndex = race.unitTypeForKing?.let { unit ->
        val index = units.indexOf(unit)
        require(index >= 0) { "Race.unitTypeForKing references a Prto not present in units" }
        index
    } ?: -1
    val freeTechIndices = race.freeTechs.map { tech ->
        tech?.let {
            val index = techs.indexOf(it)
            require(index >= 0) { "Race.freeTechs references a Tech not present in techs" }
            index
        } ?: -1
    }

    RaceEntry(
        cityNames = race.cityNames,
        greatLeaderNames = race.greatLeaderNames,
        leader = race.leader,
        civilopediaEntry = race.civilopediaEntry,
        adjective = race.adjective,
        name = race.name,
        noun = race.noun,
        eras = race.eras,
        cultureGroup = race.cultureGroup,
        civilizationGender = race.civilizationGender,
        personality = WireRacePersonality(
            favoriteGovernment = favoriteGovernmentIndex,
            shunnedGovernment = shunnedGovernmentIndex,
            aggressionLevel = race.personality.aggressionLevel,
        ),
        uniqueCivilizationCounter = race.uniqueCivilizationCounter,
        defaultColor = race.defaultColor,
        uniqueColor = race.uniqueColor,
        freeTechs = freeTechIndices,
        bonuses = race.bonuses,
        governor = WireRaceGovernor(
            settings = race.governor.settings,
            buildNever = race.governor.buildNever,
            buildOften = race.governor.buildOften,
        ),
        plurality = race.plurality,
        unitTypeForKing = unitTypeForKingIndex,
        flavors = race.flavors,
        unknown = race.unknown,
        diplomacyTextIndex = race.diplomacyTextIndex,
        scientificLeaderNames = race.scientificLeaderNames,
    )
}
