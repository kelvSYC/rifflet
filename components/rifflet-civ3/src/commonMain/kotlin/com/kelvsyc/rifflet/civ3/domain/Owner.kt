package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.LeadEntry

/**
 * The resolved meaning of an `ownerType`/`owner` wire field pair — the domain-layer counterpart to
 * [com.kelvsyc.rifflet.civ3.Owner], shared across every section using this pattern
 * ([com.kelvsyc.rifflet.civ3.CityEntry] first; [com.kelvsyc.rifflet.civ3.ClnyEntry],
 * [com.kelvsyc.rifflet.civ3.SlocEntry], and [com.kelvsyc.rifflet.civ3.UnitEntry] are expected to
 * reuse this same type in later passes).
 *
 * Unlike the wire `Owner`, there is no `Unrecognized` case: the real Rules/Scenario editor only
 * ever produces `ownerType` 0-3, so the domain layer treats that range as closed — an out-of-range
 * `ownerType` is a construction-time error (see [resolveOwner]'s guard), not a representable
 * domain value. The wire `Owner.Unrecognized` stays exactly as-is; only the domain layer is
 * closed.
 */
sealed interface Owner {
    /** `ownerType == 0`: unowned. */
    data object None : Owner

    /** `ownerType == 1`: owned by barbarians. */
    data object Barbarian : Owner

    /**
     * `ownerType == 3`: owned by a player. References the wire `LeadEntry` — `LEAD` doesn't have
     * its own domain type yet.
     */
    data class Player(val lead: LeadEntry?) : Owner

    /**
     * `ownerType == 2`: owned by a civilization. [race] is `null` when the wire `owner` index
     * doesn't resolve against the supplied [Race] entries — distinct from [None], which means
     * "not civ-owned at all."
     */
    data class Civilization(val race: Race?) : Owner
}

/**
 * Resolves an `ownerType`/`owner` wire field pair (see [Owner] for what each case means) against
 * [races]/[leads].
 *
 * Throws [IllegalArgumentException] if [ownerType] is outside the documented `0..3` range — the
 * real Rules/Scenario editor never produces such a value, so this is treated as a hard
 * construction-time error, the same way TECH/BLDG/PRTO's cycle guards treat other
 * "impossible per the editor" conditions.
 */
internal fun resolveOwner(ownerType: Int, owner: Int, races: List<Race>, leads: List<LeadEntry>): Owner {
    require(ownerType in 0..3) { "ownerType=$ownerType is not a recognized value (0..3)" }
    return when (ownerType) {
        0 -> Owner.None
        1 -> Owner.Barbarian
        2 -> Owner.Civilization(races.getOrNull(owner))
        else -> Owner.Player(leads.getOrNull(owner))
    }
}
