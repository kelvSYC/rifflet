package com.kelvsyc.rifflet.civ3.domain

/**
 * A placed starting location, mutable — the domain-layer counterpart to
 * [com.kelvsyc.rifflet.civ3.SlocEntry].
 *
 * @param x This starting location's map X coordinate.
 * @param y This starting location's map Y coordinate.
 * @param owner This starting location's reserved owner. See [Owner]. Never [Owner.Barbarian] for a
 *   domain-constructed instance — the real Rules/Scenario editor doesn't allow it, and `toDomain()`
 *   throws rather than construct one from wire data.
 */
data class StartingLocation(
    var x: Int,
    var y: Int,
    var owner: Owner = Owner.None,
)
