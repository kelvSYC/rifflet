package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `RACE` section: a civilization's identity, leader/city naming pools, and
 * game-rule parameters.
 *
 * @param eras Sized from the already-parsed `ERAS` section's entry count, not from any field
 *   within this record.
 * @param shunnedGovernment A `GOVT` section index, per the Civilizations editor tab's
 *   "Shunned Government" dropdown. Same treatment applies to [favoriteGovernment].
 * @param freeTech1 A `TECH` section index, per the Civilizations editor tab's "Free Techs"
 *   dropdowns. Same treatment applies to [freeTech2], [freeTech3], and [freeTech4].
 * @param bonuses 4 bytes of packed boolean flags. [RaceEntry.militaristic] and its 7 sibling
 *   trait accessors in `RaceEntryFlags.kt` cover bits 0-7, documented by existing
 *   reverse-engineering work. The Conquests Rules Editor's "Civilizations" tab shows this same
 *   "Bonuses" group box also containing 7 more checkboxes labeled "Flavor1" through "Flavor7",
 *   but [bonuses] itself holds only each civilization's traits, with no extra bits — the real
 *   Flavor1..7 storage is [flavors] (see that field's own KDoc), a wholly separate field from
 *   this one, not extra bits within it. Likewise for [governorSettings], [buildNever], and
 *   [buildOften] — see their own accessor files.
 * @param flavors Bitmask membership in the `FLAV` section's 7 flavor slots: bit *n* means this
 *   civilization belongs to Flavor(*n*+1). The identical scheme as
 *   `TechEntry.flavors`/`BldgEntry.flavors` (see `TechEntry.flavors`'s own KDoc), matching the
 *   `FLAV` section's 7 flavor slots (see `FlavorEntry`).
 * @param unknown 4 bytes with zero documented behavior from either reverse-engineering source;
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
