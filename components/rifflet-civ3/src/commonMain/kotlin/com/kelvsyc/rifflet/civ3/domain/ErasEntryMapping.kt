package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.EraSlot
import com.kelvsyc.rifflet.civ3.ErasEntry
import com.kelvsyc.rifflet.civ3.index

/**
 * Converts a parsed `ERAS` section to its domain-layer form, keyed by [EraSlot] rather than
 * returned as a flat list — see `EraSlot`'s own KDoc for why. Matches `EXPR`'s mapping shape: no
 * `era` parameter, no cross-references to resolve.
 *
 * Throws [IllegalArgumentException] if this list's size isn't exactly 4 — the domain-layer
 * equivalent of `validateErasCardinality`.
 */
fun List<ErasEntry>.toDomain(): Map<EraSlot, Era> {
    val slots = EraSlot.entries
    require(size == slots.size) {
        "ERAS section must have exactly ${slots.size} entries, was $size"
    }
    return slots.associateWith { slot ->
        val entry = this[slot.index]
        Era(
            name = entry.name,
            civilopediaEntry = entry.civilopediaEntry,
            researchers = listOf(entry.researcher1, entry.researcher2, entry.researcher3, entry.researcher4, entry.researcher5)
                .take(entry.numberOfUsedResearcherNames)
                .toMutableList(),
            unknown = entry.unknown,
        )
    }
}

/**
 * Converts an `ERAS` section's domain-layer form back to wire entries, ordered by [EraSlot] wire
 * index. [Era.researchers] is padded back out to 5 wire slots with `""`, and
 * `numberOfUsedResearcherNames` is derived as `researchers.size` — not a stored [Era] field.
 *
 * Throws [IllegalArgumentException] if this map's key set isn't exactly the 4 [EraSlot] values,
 * or if any [Era.researchers] has more than 5 entries.
 */
fun Map<EraSlot, Era>.toWire(): List<ErasEntry> {
    val slots = EraSlot.entries
    require(keys == slots.toSet()) {
        "ERAS map must have exactly the keys ${slots.toSet()}, had $keys"
    }
    return slots.sortedBy { it.index }.map { getValue(it) }.map { era ->
        require(era.researchers.size <= 5) {
            "Era.researchers must have at most 5 entries, had ${era.researchers.size}"
        }
        val padded = era.researchers + List(5 - era.researchers.size) { "" }
        ErasEntry(
            name = era.name,
            civilopediaEntry = era.civilopediaEntry,
            researcher1 = padded[0],
            researcher2 = padded[1],
            researcher3 = padded[2],
            researcher4 = padded[3],
            researcher5 = padded[4],
            numberOfUsedResearcherNames = era.researchers.size,
            unknown = era.unknown,
        )
    }
}

/**
 * Returns this map's [Era] values ordered by [EraSlot] wire index — the shape callers resolving a
 * wire index-based cross-reference (e.g. `Leader.initialEra`, `Tech.era`) need.
 */
fun Map<EraSlot, Era>.toOrderedList(): List<Era> =
    EraSlot.entries.sortedBy { it.index }.map { getValue(it) }
