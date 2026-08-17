package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.WorldSizeSlot
import com.kelvsyc.rifflet.civ3.WsizEntry
import com.kelvsyc.rifflet.civ3.index

/**
 * Converts a parsed `WSIZ` section to its domain-layer form, keyed by [WorldSizeSlot] rather than
 * returned as a flat list — see `WorldSizeSlot`'s own KDoc for why. Matches `EXPR`'s mapping
 * shape: no `era` parameter, no cross-references to resolve.
 *
 * Throws [IllegalArgumentException] if this list's size isn't exactly 5 — the domain-layer
 * equivalent of `validateWsizCardinality`.
 */
fun List<WsizEntry>.toDomain(): Map<WorldSizeSlot, WorldSizePreset> {
    val slots = WorldSizeSlot.entries
    require(size == slots.size) {
        "WSIZ section must have exactly ${slots.size} entries, was $size"
    }
    return slots.associateWith { slot ->
        val entry = this[slot.index]
        WorldSizePreset(
            name = entry.name,
            optimalNumberOfCities = entry.optimalNumberOfCities,
            techRate = entry.techRate,
            height = entry.height,
            distanceBetweenCivs = entry.distanceBetweenCivs,
            numberOfCivs = entry.numberOfCivs,
            width = entry.width,
            reserved = entry.reserved,
        )
    }
}

/**
 * Converts a `WSIZ` section's domain-layer form back to wire entries, ordered by [WorldSizeSlot]
 * wire index.
 *
 * Throws [IllegalArgumentException] if this map's key set isn't exactly the 5 [WorldSizeSlot]
 * values.
 */
fun Map<WorldSizeSlot, WorldSizePreset>.toWire(): List<WsizEntry> {
    val slots = WorldSizeSlot.entries
    require(keys == slots.toSet()) {
        "WSIZ map must have exactly the keys ${slots.toSet()}, had $keys"
    }
    return slots.sortedBy { it.index }.map { getValue(it) }.map { preset ->
        WsizEntry(
            optimalNumberOfCities = preset.optimalNumberOfCities,
            techRate = preset.techRate,
            reserved = preset.reserved,
            name = preset.name,
            height = preset.height,
            distanceBetweenCivs = preset.distanceBetweenCivs,
            numberOfCivs = preset.numberOfCivs,
            width = preset.width,
        )
    }
}

/**
 * Returns this map's [WorldSizePreset] values ordered by [WorldSizeSlot] wire index — the shape
 * callers resolving a wire index-based cross-reference (e.g.
 * `WorldGenerationSettings.worldSize`) need.
 */
fun Map<WorldSizeSlot, WorldSizePreset>.toOrderedList(): List<WorldSizePreset> =
    WorldSizeSlot.entries.sortedBy { it.index }.map { getValue(it) }
