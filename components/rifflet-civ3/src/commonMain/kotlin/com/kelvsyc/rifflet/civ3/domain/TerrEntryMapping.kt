package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.TerrEntry
import com.kelvsyc.rifflet.civ3.TerrainSlot
import com.kelvsyc.rifflet.civ3.TfrmEntry
import com.kelvsyc.rifflet.civ3.index
import com.kelvsyc.rifflet.civ3.pollutionEffectResolved
import com.kelvsyc.rifflet.civ3.workerJobAllowedTfrm
import com.kelvsyc.rifflet.civ3.TerrPollutionEffect as WireTerrPollutionEffect
import okio.ByteString

/**
 * Converts a parsed `TERR` section to its domain-layer form, keyed by [TerrainSlot] rather than
 * returned as a flat list — see `TerrainSlot`'s own KDoc for why.
 *
 * [resources] is the already domain-converted `GOOD` list; [tfrmJobs] stays wire-typed (`TFRM`
 * doesn't have a domain type yet).
 *
 * Throws [IllegalArgumentException] if this list's size doesn't exactly match the number of slots
 * valid for [era] — the domain-layer equivalent of `validateTerrCardinality`, since a
 * `Map<TerrainSlot, Terrain>` can't structurally guarantee completeness the way a fixed-size array
 * could.
 */
fun List<TerrEntry>.toDomain(
    era: Civ3FormatEra,
    resources: List<Resource>,
    tfrmJobs: List<TfrmEntry>,
): Map<TerrainSlot, Terrain> {
    val slots = TerrainSlot.entries.filter { it.index(era) != null }
    require(size == slots.size) {
        "TERR section must have exactly ${slots.size} entries for $era, was $size"
    }

    val bySlot = slots.associateWith { slot ->
        val entry = this[slot.index(era)!!]
        Terrain(
            name = entry.name,
            civilopediaEntry = entry.civilopediaEntry,
            possibleResources = resources.filterIndexed { index, _ ->
                val byteIndex = index / 8
                val bitIndex = index % 8
                byteIndex < entry.possibleResources.size &&
                    (entry.possibleResources[byteIndex].toInt() and (1 shl bitIndex)) != 0
            }.toMutableSet(),
            terraformBonuses = entry.terraformBonuses,
            defenseBonus = entry.defenseBonus,
            movementCost = entry.movementCost,
            tileValues = entry.tileValues,
            workerJobAllowed = entry.workerJobAllowedTfrm(tfrmJobs),
            allowances = entry.allowances,
            landmark = entry.landmark,
            terrainFlags = entry.terrainFlags,
            diseaseStrength = entry.diseaseStrength,
            unknown = entry.unknown,
            unknown2 = entry.unknown2,
            numberOfPossibleResources = entry.numberOfPossibleResources,
        )
    }

    // Second pass: pollutionEffect resolves against sibling Terrain objects, which must all
    // already exist — resolving it inline above would recurse if two entries' pollutionEffect
    // fields pointed at each other.
    slots.forEach { slot ->
        val entry = this[slot.index(era)!!]
        bySlot.getValue(slot).pollutionEffect = when (entry.pollutionEffectResolved(this)) {
            is WireTerrPollutionEffect.None -> TerrPollutionEffect.None
            is WireTerrPollutionEffect.BaseTerrainType -> TerrPollutionEffect.BaseTerrainType
            is WireTerrPollutionEffect.Terrain -> {
                val targetSlot = slots.firstOrNull { it.index(era) == entry.pollutionEffect }
                TerrPollutionEffect.SpecificTerrain(targetSlot?.let { bySlot.getValue(it) })
            }
        }
    }

    return bySlot
}

/**
 * Converts a `TERR` section's domain-layer form back to wire entries, ordered by [TerrainSlot]
 * wire index for [era].
 *
 * Throws [IllegalArgumentException] if this map's key set isn't exactly the slots valid for [era],
 * or if any [Terrain.possibleResources] entry, [Terrain.workerJobAllowed], or
 * [TerrPollutionEffect.SpecificTerrain.terrain] resolves to an object not present in the
 * corresponding list argument (or, for `pollutionEffect`, not present in this map's own values) —
 * `indexOf`-based, the same accepted structural-equality limitation as every other `toWire()` in
 * this codebase. [Terrain.workerJobAllowed] writes back `-1` for `null`; `pollutionEffect` writes
 * back `-1` for [TerrPollutionEffect.None], this map's size for [TerrPollutionEffect.BaseTerrainType],
 * or a dangling [TerrPollutionEffect.SpecificTerrain]'s target index.
 */
fun Map<TerrainSlot, Terrain>.toWire(
    era: Civ3FormatEra,
    resources: List<Resource>,
    tfrmJobs: List<TfrmEntry>,
): List<TerrEntry> {
    val slots = TerrainSlot.entries.filter { it.index(era) != null }
    require(keys == slots.toSet()) {
        "TERR map must have exactly the keys ${slots.toSet()} for $era, had $keys"
    }
    val orderedSlots = slots.sortedBy { it.index(era) }
    val orderedTerrains = orderedSlots.map { getValue(it) }

    return orderedTerrains.map { terrain ->
        val possibleResourcesBytes = ByteArray((terrain.numberOfPossibleResources + 7) / 8)
        terrain.possibleResources.forEach { resource ->
            val index = resources.indexOf(resource)
            require(index >= 0) { "Terrain.possibleResources references a Resource not present in resources" }
            possibleResourcesBytes[index / 8] = (possibleResourcesBytes[index / 8].toInt() or (1 shl (index % 8))).toByte()
        }

        val workerJobAllowedIndex = terrain.workerJobAllowed?.let {
            val index = tfrmJobs.indexOf(it)
            require(index >= 0) { "Terrain.workerJobAllowed references a TfrmEntry not present in tfrmJobs" }
            index
        } ?: -1

        val pollutionEffectIndex = when (val effect = terrain.pollutionEffect) {
            TerrPollutionEffect.None -> -1
            TerrPollutionEffect.BaseTerrainType -> orderedTerrains.size
            is TerrPollutionEffect.SpecificTerrain -> effect.terrain?.let {
                val index = orderedTerrains.indexOf(it)
                require(index >= 0) { "Terrain.pollutionEffect references a Terrain not present in this map" }
                index
            } ?: -1
        }

        TerrEntry(
            numberOfPossibleResources = terrain.numberOfPossibleResources,
            possibleResources = ByteString.of(*possibleResourcesBytes),
            name = terrain.name,
            civilopediaEntry = terrain.civilopediaEntry,
            terraformBonuses = terrain.terraformBonuses,
            defenseBonus = terrain.defenseBonus,
            movementCost = terrain.movementCost,
            tileValues = terrain.tileValues,
            workerJobAllowed = workerJobAllowedIndex,
            pollutionEffect = pollutionEffectIndex,
            allowances = terrain.allowances,
            unknown = terrain.unknown,
            landmark = terrain.landmark,
            unknown2 = terrain.unknown2,
            terrainFlags = terrain.terrainFlags,
            diseaseStrength = terrain.diseaseStrength,
        )
    }
}

/**
 * Returns this map's [Terrain] values ordered by [TerrainSlot] wire index for [era] — the shape
 * callers resolving a wire index-based cross-reference (e.g. `Tile.baseTerrain`,
 * `Prto.ignoreMovementCost`) need.
 */
fun Map<TerrainSlot, Terrain>.toOrderedList(era: Civ3FormatEra): List<Terrain> =
    TerrainSlot.entries
        .filter { it.index(era) != null }
        .sortedBy { it.index(era) }
        .map { getValue(it) }
