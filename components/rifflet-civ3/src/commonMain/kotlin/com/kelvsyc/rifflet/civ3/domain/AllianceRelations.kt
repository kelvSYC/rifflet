package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.AllianceSlot

/**
 * `GameLockedAlliance`'s dense war-status matrix, mutable — the domain-layer counterpart to
 * [com.kelvsyc.rifflet.civ3.GameLockedAlliance.allianceWars], keyed by [AllianceSlot] pairs rather
 * than a flat row-major list. Same shape and the same non-silent-`toWire()` discipline as
 * [FlavorRelations] — see that type's KDoc.
 */
data class AllianceRelations(
    private val warStatus: MutableMap<AllianceSlot, MutableMap<AllianceSlot, Int>> = mutableMapOf(),
) {
    operator fun get(from: AllianceSlot, to: AllianceSlot): Int = warStatus[from]?.get(to) ?: 0

    operator fun set(from: AllianceSlot, to: AllianceSlot, value: Int) {
        warStatus.getOrPut(from) { mutableMapOf() }[to] = value
    }

    internal fun isComplete(): Boolean =
        AllianceSlot.entries.all { from -> AllianceSlot.entries.all { to -> warStatus[from]?.containsKey(to) == true } }
}
