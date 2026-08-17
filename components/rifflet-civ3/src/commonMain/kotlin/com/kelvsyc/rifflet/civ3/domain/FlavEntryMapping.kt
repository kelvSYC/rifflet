package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.FlavGroupEntry
import com.kelvsyc.rifflet.civ3.FlavorEntry
import com.kelvsyc.rifflet.civ3.FlavorSlot
import com.kelvsyc.rifflet.civ3.index

/**
 * Converts a parsed `FLAV` section to its domain-layer form.
 *
 * Throws [IllegalArgumentException] if any group's `flavors` isn't exactly 7 entries, or if any
 * of those entries' `relations` isn't exactly 7 entries — the domain-layer equivalent of the
 * implicit "7 flavors, 7×7 relations" real-file invariant (no wire-layer cardinality rule exists
 * for `FLAV` to mirror).
 */
fun List<FlavGroupEntry>.toDomain(): List<FlavorGroup> = map { group ->
    val slots = FlavorSlot.entries
    require(group.flavors.size == slots.size) {
        "FlavGroupEntry.flavors must have exactly ${slots.size} entries, was ${group.flavors.size}"
    }
    group.flavors.forEach { entry ->
        require(entry.relations.size == slots.size) {
            "FlavorEntry.relations must have exactly ${slots.size} entries, was ${entry.relations.size}"
        }
    }

    val flavors = slots.associateWith { slot ->
        val entry = group.flavors[slot.index]
        Flavor(name = entry.name, unknown = entry.unknown)
    }
    val relations = FlavorRelations()
    slots.forEach { from ->
        val entry = group.flavors[from.index]
        slots.forEach { to -> relations[from, to] = entry.relations[to.index] }
    }

    FlavorGroup(flavors = flavors.toMutableMap(), relations = relations)
}

/**
 * Converts a `FLAV` section's domain-layer form back to wire entries.
 *
 * Throws [IllegalArgumentException] if [FlavorGroup.flavors] isn't keyed by exactly the 7
 * [FlavorSlot] values, or if [FlavorGroup.relations] doesn't have all 49 `(from, to)` pairs
 * present — see [FlavorRelations]'s own KDoc for why this doesn't silently default.
 */
fun List<FlavorGroup>.toWire(): List<FlavGroupEntry> = map { group ->
    val slots = FlavorSlot.entries
    require(group.flavors.keys == slots.toSet()) {
        "FlavorGroup.flavors must have exactly the keys ${slots.toSet()}, had ${group.flavors.keys}"
    }
    require(group.relations.isComplete()) {
        "FlavorGroup.relations must have all ${slots.size * slots.size} (from, to) pairs present"
    }

    val entries = slots.sortedBy { it.index }.map { from ->
        val flavor = group.flavors.getValue(from)
        FlavorEntry(
            unknown = flavor.unknown,
            name = flavor.name,
            relations = slots.sortedBy { it.index }.map { to -> group.relations[from, to] },
        )
    }
    FlavGroupEntry(flavors = entries)
}
