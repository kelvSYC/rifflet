package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `LEAD` section: a player/leader slot definition.
 *
 * @param unknown 8 bytes with zero documented behavior from either cross-referenced source;
 *   preserved raw, not validated. Same treatment as `RaceEntry.unknown`.
 * @param startingTechnologyIds Likely `TECH` section indices (naming convention only); not
 *   confirmed by either cross-referenced source.
 * @param government Likely a `GOVT` section index (naming convention only); not confirmed by
 *   either cross-referenced source.
 * @param civ -2 = random, -3 = any; otherwise likely a `RACE` section index (naming convention
 *   only); not confirmed by either cross-referenced source.
 * @param unknown2 4 bytes with zero documented behavior from either cross-referenced source;
 *   preserved raw, not validated. Same treatment as `RaceEntry.unknown`.
 */
data class LeadEntry(
    val customCivData: Int,
    val humanPlayer: Int,
    val name: String,
    val unknown: ByteString,
    val startUnits: List<LeadStartUnit>,
    val genderOfLeaderName: Int,
    val startingTechnologyIds: List<Int>,
    val difficulty: Int,
    val initialEra: Int,
    val startCash: Int,
    val government: Int,
    val civ: Int,
    val color: Int,
    val skipFirstTurn: Int,
    val unknown2: ByteString,
    val startEmbassies: Byte,
) {
    init {
        require(unknown.size == 8) { "LeadEntry.unknown must be exactly 8 bytes, was ${unknown.size}" }
        require(unknown2.size == 4) { "LeadEntry.unknown2 must be exactly 4 bytes, was ${unknown2.size}" }
    }
}
