package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `RACE` section: a civilization's identity, leader/city naming pools, and
 * game-rule parameters.
 *
 * @param eras Sized from the already-parsed `ERAS` section's entry count, not from any field
 *   within this record.
 * @param bonuses 4 bytes of packed boolean flags (civilization traits); see [RaceEntry.militaristic]
 *   and its sibling accessors in `RaceEntryFlags.kt` for the named per-bit accessors. Likewise
 *   for [governorSettings], [buildNever], and [buildOften] — see their own accessor files.
 * @param unknown 4 bytes with zero documented behavior from either cross-referenced source;
 *   preserved raw, not validated.
 */
data class RaceEntry(
    val cityNames: List<String>,
    val greatLeaderNames: List<String>,
    val leaderName: String,
    val leaderTitle: String,
    val civilopediaEntry: String,
    val adjective: String,
    val name: String,
    val noun: String,
    val eras: List<RaceEraFilenames>,
    val cultureGroup: Int,
    val leaderGender: Int,
    val civilizationGender: Int,
    val aggressionLevel: Int,
    val uniqueCivilizationCounter: Int,
    val shunnedGovernment: Int,
    val favoriteGovernment: Int,
    val defaultColor: Int,
    val uniqueColor: Int,
    val freeTech1: Int,
    val freeTech2: Int,
    val freeTech3: Int,
    val freeTech4: Int,
    val bonuses: Int,
    val governorSettings: Int,
    val buildNever: Int,
    val buildOften: Int,
    val plurality: Int,
    val unitTypeForKing: Int,
    val flavors: Int,
    val unknown: ByteString,
    val diplomacyTextIndex: Int,
    val scientificLeaderNames: List<String>,
) {
    init {
        require(unknown.size == 4) { "RaceEntry.unknown must be exactly 4 bytes, was ${unknown.size}" }
    }
}
