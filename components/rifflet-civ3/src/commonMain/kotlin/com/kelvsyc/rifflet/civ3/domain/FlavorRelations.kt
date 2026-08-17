package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.FlavorSlot

/**
 * A `FLAV` flavor group's dense relationship-strength matrix, mutable — the domain-layer
 * counterpart to every [com.kelvsyc.rifflet.civ3.FlavorEntry.relations] in the same group,
 * bundled into one 7×7 table keyed by [FlavorSlot] pairs rather than embedded per-[Flavor].
 *
 * [get] falls back to `0` for a pair with no stored value — a convenience for hand-building a
 * partial instance (e.g. in tests). `FlavEntryMapping.kt`'s `toWire()` does not rely on that
 * fallback: it requires all 49 pairs to be present before reconstructing wire entries, the same
 * "require the exact expected shape, don't silently default" rule `Map<Slot,T>.toWire()`'s keyset
 * check applies elsewhere in this codebase.
 */
data class FlavorRelations(
    private val strengths: MutableMap<FlavorSlot, MutableMap<FlavorSlot, Int>> = mutableMapOf(),
) {
    operator fun get(from: FlavorSlot, to: FlavorSlot): Int = strengths[from]?.get(to) ?: 0

    operator fun set(from: FlavorSlot, to: FlavorSlot, value: Int) {
        strengths.getOrPut(from) { mutableMapOf() }[to] = value
    }

    internal fun isComplete(): Boolean =
        FlavorSlot.entries.all { from -> FlavorSlot.entries.all { to -> strengths[from]?.containsKey(to) == true } }
}
