package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.ExperienceLevelSlot
import com.kelvsyc.rifflet.civ3.ExprEntry
import com.kelvsyc.rifflet.civ3.index

/**
 * Converts a parsed `EXPR` section to its domain-layer form, keyed by [ExperienceLevelSlot]
 * rather than returned as a flat list — see `ExperienceLevelSlot`'s own KDoc for why. Unlike
 * `TERR`/`TFRM`'s mapping functions, this one takes no `era` parameter and has no
 * cross-references to resolve: `EXPR`'s cardinality and ordering never vary by era, and
 * [ExprEntry] carries no indices into other sections.
 *
 * Throws [IllegalArgumentException] if this list's size isn't exactly 4 — the domain-layer
 * equivalent of `validateExprCardinality`, since a `Map<ExperienceLevelSlot, ExperienceLevel>`
 * can't structurally guarantee completeness the way a fixed-size array could.
 */
fun List<ExprEntry>.toDomain(): Map<ExperienceLevelSlot, ExperienceLevel> {
    val slots = ExperienceLevelSlot.entries
    require(size == slots.size) {
        "EXPR section must have exactly ${slots.size} entries, was $size"
    }
    return slots.associateWith { slot ->
        val entry = this[slot.index]
        ExperienceLevel(
            name = entry.name,
            baseHitPoints = entry.baseHitPoints,
            retreatBonus = entry.retreatBonus,
        )
    }
}

/**
 * Converts an `EXPR` section's domain-layer form back to wire entries, ordered by
 * [ExperienceLevelSlot] wire index.
 *
 * Throws [IllegalArgumentException] if this map's key set isn't exactly the 4
 * [ExperienceLevelSlot] values.
 */
fun Map<ExperienceLevelSlot, ExperienceLevel>.toWire(): List<ExprEntry> {
    val slots = ExperienceLevelSlot.entries
    require(keys == slots.toSet()) {
        "EXPR map must have exactly the keys ${slots.toSet()}, had $keys"
    }
    return slots.sortedBy { it.index }.map { getValue(it) }.map { level ->
        ExprEntry(
            name = level.name,
            baseHitPoints = level.baseHitPoints,
            retreatBonus = level.retreatBonus,
        )
    }
}

/**
 * Returns this map's [ExperienceLevel] values ordered by [ExperienceLevelSlot] wire index — the
 * shape callers resolving a wire index-based cross-reference (e.g. `Government.diplomatsAre`,
 * `PlacedUnit.experienceLevel`) need.
 */
fun Map<ExperienceLevelSlot, ExperienceLevel>.toOrderedList(): List<ExperienceLevel> =
    ExperienceLevelSlot.entries.sortedBy { it.index }.map { getValue(it) }
