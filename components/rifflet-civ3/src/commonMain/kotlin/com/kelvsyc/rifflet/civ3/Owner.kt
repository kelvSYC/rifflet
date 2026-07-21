package com.kelvsyc.rifflet.civ3

/**
 * The resolved meaning of an `ownerType`/`owner` field pair, a pattern shared verbatim across
 * [CityEntry], [ClnyEntry], [SlocEntry], and [UnitEntry]: `ownerType` discriminates what `owner`
 * means (0=None, 1=Barbarian, 2=Civ → a `RACE` section index, 3=Player → a 0-based player
 * index), explicitly documented by existing reverse-engineering work.
 */
sealed interface Owner {
    /** `ownerType == 0`: unowned. */
    data object None : Owner

    /** `ownerType == 1`: owned by barbarians. */
    data object Barbarian : Owner

    /**
     * `ownerType == 3`: owned by a player, identified by a 0-based [index] — likely a `LEAD`
     * section index, but not resolved further by this codebase and not confirmed.
     */
    data class Player(val index: Int) : Owner

    /**
     * `ownerType == 2`: owned by a civilization. [race] is `null` when `owner` doesn't resolve
     * against the supplied `RACE` entries — distinct from [None], which means "not civ-owned at
     * all," not "civ-owned but unresolvable."
     */
    data class Civilization(val race: RaceEntry?) : Owner

    /**
     * An `ownerType` outside the documented `0..3` range. Preserves both raw values rather than
     * throwing, consistent with this codebase's non-validating, best-effort resolution layer.
     */
    data class Unrecognized(val ownerType: Int, val owner: Int) : Owner
}

/**
 * Resolves an `ownerType`/`owner` field pair (see [Owner] for what each case means) against
 * [races]. Shared by [ClnyEntry.resolveOwner], [SlocEntry.resolveOwner], [CityEntry.resolveOwner],
 * and [UnitEntry.resolveOwner] — each is a one-line wrapper delegating here, since all four entry
 * types share this exact field pattern.
 */
internal fun resolveOwner(ownerType: Int, owner: Int, races: List<RaceEntry>): Owner = when (ownerType) {
    0 -> Owner.None
    1 -> Owner.Barbarian
    2 -> Owner.Civilization(races.getOrNull(owner))
    3 -> Owner.Player(owner)
    else -> Owner.Unrecognized(ownerType, owner)
}
