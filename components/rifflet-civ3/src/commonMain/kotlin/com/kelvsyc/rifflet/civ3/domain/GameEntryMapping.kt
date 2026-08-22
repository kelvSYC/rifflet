package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.AllianceSlot
import com.kelvsyc.rifflet.civ3.GameEntry
import com.kelvsyc.rifflet.civ3.GameLockedAlliance
import com.kelvsyc.rifflet.civ3.index
import com.kelvsyc.rifflet.civ3.toByteStringLe
import com.kelvsyc.rifflet.civ3.toIntLe

/**
 * Converts a parsed `GAME` section to its domain-layer form.
 *
 * [races] is the already domain-converted `RACE` list. Flat list, no cardinality guard — no
 * `validateGameCardinality` rule exists to mirror, matching `WMAP`/`RULE`'s treatment.
 *
 * Throws [IllegalArgumentException] if any entry's `lockedAlliance` is present and its
 * `allianceVictoryType` doesn't resolve to a known [AllianceVictoryType] (0/1 are the only values
 * the editor can ever write).
 */
fun List<GameEntry>.toDomain(races: List<Race>): List<Game> = map { entry ->
    Game(
        defaultGameRules = entry.defaultGameRules != 0,
        defaultVictoryConditions = entry.defaultVictoryConditions != 0,
        playableCivs = entry.playableCivIds.map { races.getOrNull(it) }.toMutableList(),
        flags = entry.flags.toIntLe(),
        placeCaptureUnits = entry.placeCaptureUnits != 0,
        autoPlaceKings = entry.autoPlaceKings != 0,
        autoPlaceVictoryLocations = entry.autoPlaceVictoryLocations != 0,
        debugMode = entry.debugMode != 0,
        timeOptions = entry.timeOptions,
        scenarioSearchFolders = entry.scenarioSearchFolders,
        allianceStatuses = entry.civAllianceStatuses.toMutableList(),
        victoryPointLimits = entry.victoryPointLimits,
        unknown = entry.unknown,
        lockedAlliance = entry.lockedAlliance?.toDomainLockedAlliance(),
        plagueSettings = entry.plagueSettings,
        unknown2 = entry.unknown2,
        mapVisible = entry.mapVisible.toInt() != 0,
        retainCulture = entry.retainCulture.toInt() != 0,
        unknown3 = entry.unknown3,
        eruptionPeriod = entry.eruptionPeriod,
        mpTimers = entry.mpTimers,
    )
}

private fun GameLockedAlliance.toDomainLockedAlliance(): LockedAlliance {
    require(allianceNames.size == 5) {
        "GameLockedAlliance.allianceNames must have exactly 5 elements, had ${allianceNames.size}"
    }
    require(allianceWars.size == 25) {
        "GameLockedAlliance.allianceWars must have exactly 25 elements, had ${allianceWars.size}"
    }
    val slots = AllianceSlot.entries
    val alliances = slots.associateWith { slot -> Alliance(name = allianceNames[slot.index]) }.toMutableMap()
    val relations = AllianceRelations()
    slots.forEach { from -> slots.forEach { to -> relations[from, to] = allianceWars[from.index * 5 + to.index] } }
    val victoryType = AllianceVictoryType.entries.getOrNull(allianceVictoryType)
        ?: throw IllegalArgumentException(
            "GameLockedAlliance.allianceVictoryType=$allianceVictoryType does not resolve to a known AllianceVictoryType",
        )
    return LockedAlliance(alliances = alliances, relations = relations, victoryType = victoryType)
}

/**
 * Converts a `GAME` section's domain-layer form back to wire entries.
 *
 * Throws [IllegalArgumentException] if [Game.playableCivs].size != [Game.allianceStatuses].size
 * (the two lists must stay in lockstep — [GameEntry]'s own `init` block requires both to match
 * `numberOfPlayableCivs`), if any non-null [Game.playableCivs] entry resolves to a [Race] not
 * present in [races] (`indexOf`-based, the same accepted structural-equality limitation as every
 * other `toWire()` in this codebase), if [LockedAlliance.alliances] isn't keyed by exactly the 5
 * [AllianceSlot] values, or if [LockedAlliance.relations] doesn't have all 25 `(from, to)` pairs
 * present. A `null` [Game.playableCivs] entry writes back `-1`.
 */
fun List<Game>.toWire(races: List<Race>): List<GameEntry> = map { game ->
    require(game.playableCivs.size == game.allianceStatuses.size) {
        "Game.playableCivs and Game.allianceStatuses must be the same size, had " +
            "${game.playableCivs.size} and ${game.allianceStatuses.size}"
    }
    val playableCivIds = game.playableCivs.map { race ->
        race?.let {
            val index = races.indexOf(it)
            require(index >= 0) { "Game.playableCivs references a Race not present in races" }
            index
        } ?: -1
    }

    GameEntry(
        defaultGameRules = if (game.defaultGameRules) 1 else 0,
        defaultVictoryConditions = if (game.defaultVictoryConditions) 1 else 0,
        numberOfPlayableCivs = game.playableCivs.size,
        playableCivIds = playableCivIds,
        flags = game.flags.toByteStringLe(),
        placeCaptureUnits = if (game.placeCaptureUnits) 1 else 0,
        autoPlaceKings = if (game.autoPlaceKings) 1 else 0,
        autoPlaceVictoryLocations = if (game.autoPlaceVictoryLocations) 1 else 0,
        debugMode = if (game.debugMode) 1 else 0,
        timeOptions = game.timeOptions,
        scenarioSearchFolders = game.scenarioSearchFolders,
        civAllianceStatuses = game.allianceStatuses,
        victoryPointLimits = game.victoryPointLimits,
        unknown = game.unknown,
        lockedAlliance = game.lockedAlliance?.toWireLockedAlliance(),
        plagueSettings = game.plagueSettings,
        unknown2 = game.unknown2,
        mapVisible = if (game.mapVisible) 1 else 0,
        retainCulture = if (game.retainCulture) 1 else 0,
        unknown3 = game.unknown3,
        eruptionPeriod = game.eruptionPeriod,
        mpTimers = game.mpTimers,
    )
}

private fun LockedAlliance.toWireLockedAlliance(): GameLockedAlliance {
    val slots = AllianceSlot.entries
    require(alliances.keys == slots.toSet()) {
        "LockedAlliance.alliances must have exactly the keys ${slots.toSet()}, had ${alliances.keys}"
    }
    require(relations.isComplete()) {
        "LockedAlliance.relations must have all ${slots.size * slots.size} (from, to) pairs present"
    }
    return GameLockedAlliance(
        allianceNames = slots.sortedBy { it.index }.map { alliances.getValue(it).name },
        allianceWars = slots.sortedBy { it.index }
            .flatMap { from -> slots.sortedBy { it.index }.map { to -> relations[from, to] } },
        allianceVictoryType = victoryType.ordinal,
    )
}
