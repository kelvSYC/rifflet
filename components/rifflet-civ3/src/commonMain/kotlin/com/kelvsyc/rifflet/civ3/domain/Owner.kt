package com.kelvsyc.rifflet.civ3.domain

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
    /** `ownerType == 0`: unowned. No comparable raw index exists to preserve: the real
     * Rules/Scenario editor's "select owner" control offers no dropdown/index for "None" the way
     * it does for the other three cases. */
    data object None : Owner

    /**
     * `ownerType == 1`: owned by barbarians. [tribeIndex] is the raw wire `owner` value — a
     * barbarian tribe identity, used in-game for flavor (tribe names drawn from the barbarian
     * placeholder civ's city-name list, e.g. "Zapotec Barbarians"). There is no separate object to
     * resolve it against, so it's preserved directly rather than resolved, and survives a
     * `toDomain()`/`toWire()` round-trip.
     */
    data class Barbarian(val tribeIndex: Int = -1) : Owner

    /**
     * `ownerType == 3`: owned by a player. [unresolvedIndex] preserves the raw wire `owner` value
     * whenever [lead] is `null` (either a genuinely dangling index, or `LEAD` legitimately absent
     * because Custom Player Data is off) — consulted by `toWire()` only in that case; when [lead]
     * is non-null, its position is re-derived instead, so reassigning [lead] to a different object
     * still round-trips correctly.
     */
    data class Player(val lead: Leader? = null, val unresolvedIndex: Int = -1) : Owner

    /**
     * `ownerType == 2`: owned by a civilization. [race] is `null` when the wire `owner` index
     * doesn't resolve against the supplied [Race] entries — distinct from [None], which means
     * "not civ-owned at all." [unresolvedIndex] preserves the raw wire `owner` value whenever
     * [race] is `null`, the same treatment as [Player.unresolvedIndex].
     */
    data class Civilization(val race: Race? = null, val unresolvedIndex: Int = -1) : Owner
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
internal fun resolveOwner(ownerType: Int, owner: Int, races: List<Race>, leads: List<Leader>): Owner {
    require(ownerType in 0..3) { "ownerType=$ownerType is not a recognized value (0..3)" }
    return when (ownerType) {
        0 -> Owner.None
        1 -> Owner.Barbarian(tribeIndex = owner)
        2 -> Owner.Civilization(race = races.getOrNull(owner), unresolvedIndex = owner)
        else -> Owner.Player(lead = leads.getOrNull(owner), unresolvedIndex = owner)
    }
}
