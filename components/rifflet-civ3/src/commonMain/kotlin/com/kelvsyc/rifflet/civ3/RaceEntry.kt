package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `RACE` section: a civilization's identity, leader/city naming pools, and
 * game-rule parameters.
 *
 * @param eras Sized from the already-parsed `ERAS` section's entry count, not from any field
 *   within this record.
 * @param leader This civilization's leader identity. See [RaceLeader].
 * @param personality This civilization's default diplomatic/AI personality. See
 *   [RacePersonality].
 * @param freeTechs This civilization's 4 free-technology grants, per the Civilizations editor
 *   tab's "Free Techs" dropdowns — each a `TECH` section index.
 * @param bonuses 4 bytes of packed boolean flags. [RaceEntry.militaristic] and its 7 sibling
 *   trait accessors in `RaceEntryFlags.kt` cover bits 0-7, documented by existing
 *   reverse-engineering work. The Conquests Rules Editor's "Civilizations" tab shows this same
 *   "Bonuses" group box also containing 7 more checkboxes labeled "Flavor1" through "Flavor7",
 *   but [bonuses] itself holds only each civilization's traits, with no extra bits — the real
 *   Flavor1..7 storage is [flavors] (see that field's own KDoc), a wholly separate field from
 *   this one, not extra bits within it. Likewise for [RaceGovernor.settings],
 *   [RaceGovernor.buildNever], and [RaceGovernor.buildOften] — see their own accessor files.
 * @param governor This civilization's default Governor automation settings. See [RaceGovernor].
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
    val leader: RaceLeader,
    val civilopediaEntry: String,
    val adjective: String,
    val name: String,
    val noun: String,
    val eras: List<RaceEraFilenames>,
    val cultureGroup: Int,
    val civilizationGender: Int,
    val personality: RacePersonality,
    val uniqueCivilizationCounter: Int,
    val defaultColor: Int,
    val uniqueColor: Int,
    val freeTechs: List<Int>,
    val bonuses: Int,
    val governor: RaceGovernor,
    val plurality: Int,
    val unitTypeForKing: Int,
    val flavors: Int,
    val unknown: ByteString,
    val diplomacyTextIndex: Int,
    val scientificLeaderNames: List<String>,
) {
    init {
        require(freeTechs.size == 4) { "RaceEntry.freeTechs must be exactly 4 elements, was ${freeTechs.size}" }
        require(unknown.size == 4) { "RaceEntry.unknown must be exactly 4 bytes, was ${unknown.size}" }
    }
}
