package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.ClnyImprovementType

/**
 * A placed colony, airfield, radar tower, or outpost, mutable — the domain-layer counterpart to
 * [com.kelvsyc.rifflet.civ3.ClnyEntry].
 *
 * @param x This colony's map X coordinate.
 * @param y This colony's map Y coordinate.
 * @param owner This colony's owner. See [Owner]. Unlike [PlacedUnit], [Owner.Barbarian] is never
 *   legitimate here — the real Rules/Scenario editor forbids it uniformly across all 4
 *   [ClnyImprovementType] values, the same as [City]/[StartingLocation].
 * @param improvementType Which of the 4 documented colony types this is.
 */
data class Colony(
    var x: Int,
    var y: Int,
    var owner: Owner = Owner.None,
    var improvementType: ClnyImprovementType = ClnyImprovementType.COLONY,
)
