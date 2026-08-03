package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.PrtoEntry
import com.kelvsyc.rifflet.civ3.RaceEntry
import com.kelvsyc.rifflet.civ3.RaceGovernor as WireRaceGovernor
import com.kelvsyc.rifflet.civ3.RacePersonality as WireRacePersonality
import com.kelvsyc.rifflet.civ3.TechEntry

/**
 * Converts a parsed `RACE` section to its domain-layer form, resolving
 * [RaceEntry.personality]'s `GOVT` cross-refs against [governments] (already domain-converted —
 * see [Government]), [RaceEntry.unitTypeForKing] against [units], and each of
 * [RaceEntry.freeTechs]'s 4 slots against [techs], preserving position.
 */
fun List<RaceEntry>.toDomain(
    techs: List<TechEntry>,
    governments: List<Government>,
    units: List<PrtoEntry>,
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
