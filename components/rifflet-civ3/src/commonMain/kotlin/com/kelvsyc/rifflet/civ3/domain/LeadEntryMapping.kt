package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.DiffEntry
import com.kelvsyc.rifflet.civ3.ErasEntry
import com.kelvsyc.rifflet.civ3.Gender
import com.kelvsyc.rifflet.civ3.LeadEntry
import com.kelvsyc.rifflet.civ3.LeadStartUnit

/**
 * Converts a parsed `LEAD` section to its domain-layer form.
 *
 * [techs]/[governments]/[races]/[prtos] are the already domain-converted `TECH`/`GOVT`/`RACE`/
 * `PRTO` lists; [difficulties]/[eras] stay wire types (`DIFF`/`ERAS` don't have domain types yet).
 */
fun List<LeadEntry>.toDomain(
    techs: List<Tech>,
    governments: List<Government>,
    races: List<Race>,
    prtos: List<Prto>,
    difficulties: List<DiffEntry>,
    eras: List<ErasEntry>,
): List<Leader> = map { entry ->
    Leader(
        name = entry.name,
        humanPlayer = entry.humanPlayer != 0,
        customCivData = entry.customCivData != 0,
        civilization = when (entry.civ) {
            -2 -> LeaderCivilization.Random
            -3 -> LeaderCivilization.Unrestricted
            else -> LeaderCivilization.Preset(races.getOrNull(entry.civ))
        },
        genderOfLeaderName = if (entry.genderOfLeaderName == 1) Gender.FEMALE else Gender.MALE,
        government = governments.getOrNull(entry.government),
        difficulty = if (entry.difficulty == -2) {
            LeaderDifficulty.Unrestricted
        } else {
            LeaderDifficulty.Preset(difficulties.getOrNull(entry.difficulty))
        },
        initialEra = eras.getOrNull(entry.initialEra),
        startCash = entry.startCash,
        color = entry.color,
        startUnits = entry.startUnits.map { StartUnit(quantity = it.quantity, unitType = prtos.getOrNull(it.unitType)) }.toMutableList(),
        startingTechnologies = entry.startingTechnologyIds.map { techs.getOrNull(it) }.toMutableList(),
        skipFirstTurn = entry.skipFirstTurn != 0,
        startEmbassies = entry.startEmbassies.toInt() != 0,
        unknown = entry.unknown,
        unknown2 = entry.unknown2,
    )
}

/**
 * Converts a `LEAD` section's domain-layer form back to wire entries.
 *
 * Throws [IllegalArgumentException] if [LeaderCivilization.Preset.race], [Leader.government],
 * [LeaderDifficulty.Preset.difficulty], or any [StartUnit.unitType] resolves to an object not
 * present in the corresponding list argument — `indexOf`-based, the same accepted
 * structural-equality limitation as every other `toWire()` in this codebase.
 * [LeaderCivilization.Random]/[LeaderCivilization.Unrestricted] write back `-2`/`-3`;
 * [LeaderDifficulty.Unrestricted] writes back `-2`. A `null` [Leader.government]/
 * [Leader.initialEra]/[StartUnit.unitType]/[LeaderCivilization.Preset.race]/
 * [LeaderDifficulty.Preset.difficulty] writes back `-1` — none of these fields preserve a dangling
 * wire index across a `toDomain()`/`toWire()` round-trip.
 */
fun List<Leader>.toWire(
    techs: List<Tech>,
    governments: List<Government>,
    races: List<Race>,
    prtos: List<Prto>,
    difficulties: List<DiffEntry>,
    eras: List<ErasEntry>,
): List<LeadEntry> = map { leader ->
    val civ = when (val c = leader.civilization) {
        LeaderCivilization.Random -> -2
        LeaderCivilization.Unrestricted -> -3
        is LeaderCivilization.Preset -> c.race?.let {
            val index = races.indexOf(it)
            require(index >= 0) { "Leader.civilization references a Race not present in races" }
            index
        } ?: -1
    }
    val difficulty = when (val d = leader.difficulty) {
        LeaderDifficulty.Unrestricted -> -2
        is LeaderDifficulty.Preset -> d.difficulty?.let {
            val index = difficulties.indexOf(it)
            require(index >= 0) { "Leader.difficulty references a DiffEntry not present in difficulties" }
            index
        } ?: -1
    }
    val government = leader.government?.let {
        val index = governments.indexOf(it)
        require(index >= 0) { "Leader.government references a Government not present in governments" }
        index
    } ?: -1
    val initialEra = leader.initialEra?.let {
        val index = eras.indexOf(it)
        require(index >= 0) { "Leader.initialEra references an ErasEntry not present in eras" }
        index
    } ?: -1
    val startUnits = leader.startUnits.map { startUnit ->
        val unitTypeIndex = startUnit.unitType?.let {
            val index = prtos.indexOf(it)
            require(index >= 0) { "StartUnit.unitType references a Prto not present in prtos" }
            index
        } ?: -1
        LeadStartUnit(quantity = startUnit.quantity, unitType = unitTypeIndex)
    }
    val startingTechnologyIds = leader.startingTechnologies.map { tech ->
        tech?.let {
            val index = techs.indexOf(it)
            require(index >= 0) { "Leader.startingTechnologies references a Tech not present in techs" }
            index
        } ?: -1
    }

    LeadEntry(
        customCivData = if (leader.customCivData) 1 else 0,
        humanPlayer = if (leader.humanPlayer) 1 else 0,
        name = leader.name,
        unknown = leader.unknown,
        startUnits = startUnits,
        genderOfLeaderName = if (leader.genderOfLeaderName == Gender.FEMALE) 1 else 0,
        startingTechnologyIds = startingTechnologyIds,
        difficulty = difficulty,
        initialEra = initialEra,
        startCash = leader.startCash,
        government = government,
        civ = civ,
        color = leader.color,
        skipFirstTurn = if (leader.skipFirstTurn) 1 else 0,
        unknown2 = leader.unknown2,
        startEmbassies = if (leader.startEmbassies) 1 else 0,
    )
}
