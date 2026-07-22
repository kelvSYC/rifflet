package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `LEAD` section: a player/leader slot definition.
 *
 * @param unknown 8 bytes with zero documented behavior from either reverse-engineering source;
 *   preserved raw, not validated. Same treatment as `RaceEntry.unknown`.
 * @param customCivData Int-shaped boolean, likely corresponding to the Players tab's
 *   "Civilization Defaults" checkbox.
 * @param startingTechnologyIds `TECH` section indices, per the Players tab's "Free Techs"
 *   listbox.
 * @param difficulty A `DIFF` section index; `-2` is the "Any" sentinel, meaning this starting
 *   position isn't restricted to a particular difficulty level.
 * @param initialEra An `ERAS` section index, per the Players tab's "Initial" era dropdown.
 * @param government A `GOVT` section index, per the Players tab's own "Government" dropdown.
 * @param civ `-2` = random, `-3` = any; otherwise a `RACE` section index, per the Players tab's
 *   "Civilization" dropdown.
 * @param genderOfLeaderName Int-shaped boolean matching the Players tab's Gender radio buttons:
 *   0 = Male, 1 = Female.
 * @param unknown2 4 bytes with zero documented behavior from either reverse-engineering source;
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
