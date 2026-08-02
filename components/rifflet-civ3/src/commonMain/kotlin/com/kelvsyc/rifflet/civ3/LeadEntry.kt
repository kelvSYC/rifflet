package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `LEAD` section: a player/leader slot definition.
 *
 * @param unknown 8 bytes with zero documented behavior;
 *   preserved raw, not validated. Same treatment as `RaceEntry.unknown`.
 * @param customCivData Int-shaped boolean corresponding to the Players tab's "Civilization
 *   Defaults" checkbox, inverted: `1` is the checkbox unchecked (this player's data is customized
 *   away from the civ's defaults), and `0` is checked (using the civ's default data) — the field's
 *   own name describes the presence of custom data, the logical negation of the checkbox's own
 *   "use defaults" framing. A specific [civ] is a prerequisite for `customCivData=1`, since a
 *   nonspecific civ (`-2`/random or `-3`/any) has no defaults to override in the first place; the
 *   converse doesn't hold, since a specific [civ] can still use its own defaults.
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
 * @param unknown2 4 bytes with zero documented behavior;
 *   preserved raw, not validated. Same treatment as `RaceEntry.unknown`.
 * @param startEmbassies Int-shaped boolean matching the Players tab's "Starts with Embassies"
 *   checkbox — stored as a raw `Byte`, unlike this entry's other Int-shaped booleans. Not
 *   inverted, unlike [customCivData].
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
