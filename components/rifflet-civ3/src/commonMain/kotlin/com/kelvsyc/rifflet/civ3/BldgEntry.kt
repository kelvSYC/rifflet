package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `BLDG` section: a building or wonder definition.
 *
 * @param requiredBuilding A `BLDG` section self-reference, per the Conquests Rules Editor (not
 *   merely a naming-based inference).
 * @param requiredGovernment A `GOVT` section index, per the Conquests Rules Editor (not merely
 *   a naming-based inference).
 * @param requiredAdvance A `TECH` section index, per the Conquests Rules Editor (not merely a
 *   naming-based inference). Same treatment applies to [renderedObsoleteBy].
 * @param requiredResource1 A `GOOD` section index, per the Conquests Rules Editor (not merely a
 *   naming-based inference). Same treatment applies to [requiredResource2].
 * @param spaceshipPart `-1` if this building doesn't produce a spaceship part, or an index into
 *   `RuleEntry.spaceshipPartQuantities` identifying which part it produces — confirmed by real
 *   data (the Conquests base ruleset's 10 "SS ..." buildings each carry a distinct index 0-9,
 *   matching the General Settings tab's "Spaceship Parts" group's part dropdown/count).
 * @param flags 16 bytes, four packed 4-byte named sub-fields per existing reverse-engineering
 *   documentation of the BIX/BIQ format: [BldgEntry.improvements] (bytes 0-3), [BldgEntry.otherCharacteristics]
 *   (bytes 4-7), [BldgEntry.smallWonders] (bytes 8-11), [BldgEntry.wonders] (bytes 12-15) — see
 *   each sub-field property and its sibling named-bit accessors in `BldgEntryFlags.kt`.
 * @param flavors Bitmask membership in the `FLAV` section's 7 flavor slots: bit *n* means this
 *   building belongs to Flavor(*n*+1). See `TechEntry.flavors`'s own KDoc for the identical
 *   scheme on advances and civilizations.
 * @param unknown 4 bytes with zero documented behavior from either reverse-engineering source;
 *   preserved raw, not validated.
 * @param unitProduced A `PRTO` section index — explicitly documented by existing
 *   reverse-engineering work ("Unit produced (PRTO ref)"), not merely a naming-based inference.
 */
data class BldgEntry(
    val description: String,
    val name: String,
    val civilopediaEntry: String,
    val doublesHappiness: Int,
    val gainInEveryCity: Int,
    val gainInEveryCityOnContinent: Int,
    val requiredBuilding: Int,
    val cost: Int,
    val culture: Int,
    val bombardDefense: Int,
    val navalBombardDefense: Int,
    val defenseBonus: Int,
    val navalDefenseBonus: Int,
    val maintenanceCost: Int,
    val contentFacesAllCities: Int,
    val contentFaces: Int,
    val unhappyFacesAllCities: Int,
    val unhappyFaces: Int,
    val numberOfRequiredBuildings: Int,
    val airPower: Int,
    val navalPower: Int,
    val pollution: Int,
    val production: Int,
    val requiredGovernment: Int,
    val spaceshipPart: Int,
    val requiredAdvance: Int,
    val renderedObsoleteBy: Int,
    val requiredResource1: Int,
    val requiredResource2: Int,
    val flags: ByteString,
    val numberOfArmiesRequired: Int,
    val flavors: Int,
    val unknown: ByteString,
    val unitProduced: Int,
    val unitFrequency: Int,
) {
    init {
        require(flags.size == 16) { "BldgEntry.flags must be exactly 16 bytes, was ${flags.size}" }
        require(unknown.size == 4) { "BldgEntry.unknown must be exactly 4 bytes, was ${unknown.size}" }
    }
}
