package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `FLAV` section: an AI "flavor" category (e.g. Military, Culture) and its
 * relationship strengths to every other flavor. In practice there is always exactly one `FLAV`
 * entry per file, with exactly 7 [flavorRelationships] — the Civ3 Editor does not appear to
 * allow either count to be changed — but the file format itself declares both dynamically, so
 * this type models them as such rather than assuming fixed sizes.
 *
 * @param unknown 4 bytes with zero documented behavior from either cross-referenced source;
 *   preserved raw, not validated. Same treatment as `RaceEntry.unknown`.
 */
data class FlavEntry(
    val unknown: ByteString,
    val name: String,
    val flavorRelationships: List<Int>,
) {
    init {
        require(unknown.size == 4) { "FlavEntry.unknown must be exactly 4 bytes, was ${unknown.size}" }
    }
}
