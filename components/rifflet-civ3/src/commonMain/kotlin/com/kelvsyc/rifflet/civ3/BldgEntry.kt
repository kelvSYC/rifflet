package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `BLDG` section: a building or wonder definition.
 *
 * @param requiredBuilding Likely a `BLDG` section self-reference (naming convention only); not
 *   confirmed by either primary source.
 * @param requiredGovernment Likely a `GOVT` section index (naming convention only); not
 *   confirmed by either primary source.
 * @param requiredAdvance Likely a `TECH` section index (naming convention only); not confirmed
 *   by either primary source. Same treatment applies to [renderedObsoleteBy].
 * @param requiredResource1 Likely a `GOOD` section index (naming convention only); not
 *   confirmed by either primary source. Same treatment applies to [requiredResource2].
 * @param flags 16 bytes, four packed 4-byte named sub-fields per Apolyton's "Civilization III
 *   BIX/BIQ file format" thread: [BldgEntry.improvements] (bytes 0-3), [BldgEntry.otherCharacteristics]
 *   (bytes 4-7), [BldgEntry.smallWonders] (bytes 8-11), [BldgEntry.wonders] (bytes 12-15) — see
 *   each sub-field property and its sibling named-bit accessors in `BldgEntryFlags.kt`.
 * @param flavors Opaque; Apolyton documents this as a binary flags field, but it is preserved
 *   raw and not decomposed, matching this codebase's established flags treatment.
 * @param unknown 4 bytes with zero documented behavior from either primary source;
 *   preserved raw, not validated.
 * @param unitProduced A `PRTO` section index — explicitly documented by Apolyton's BIX/BIQ
 *   format reference ("Unit produced (PRTO ref)"), not merely a naming-based inference.
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
