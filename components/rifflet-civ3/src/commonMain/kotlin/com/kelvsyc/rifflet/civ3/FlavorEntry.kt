package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One flavor within a `FLAV` group: an AI "flavor" category (e.g. a civilization or barbarian
 * personality) and its relationship strengths to every other flavor in the same group. Real
 * files (confirmed against an actual [Civ3FormatEra.CONQUESTS] scenario) show 7 flavors per
 * group, each with a 7-element [relations] vector forming a personality/relationship affinity
 * matrix (self-affinity highest, others lower) — but the file format declares both the flavor
 * count (see [FlavGroupEntry]) and this per-flavor relation count dynamically, so this type
 * models them as such rather than assuming fixed sizes.
 *
 * @param unknown 4 bytes with zero documented behavior from either cross-referenced source;
 *   preserved raw, not validated. Same treatment as `RaceEntry.unknown`.
 */
data class FlavorEntry(
    val unknown: ByteString,
    val name: String,
    val relations: List<Int>,
) {
    init {
        require(unknown.size == 4) { "FlavorEntry.unknown must be exactly 4 bytes, was ${unknown.size}" }
    }
}
