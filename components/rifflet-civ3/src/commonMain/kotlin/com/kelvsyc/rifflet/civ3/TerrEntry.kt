package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `TERR` section: a terrain type definition.
 *
 * @param numberOfPossibleResources The number of significant bits in [possibleResources];
 *   stored explicitly because [possibleResources]'s byte length alone (rounded up to the
 *   nearest byte) cannot losslessly recover this count.
 * @param possibleResources A bit array, one bit per `GOOD` section entry (packed 8 per byte,
 *   rounded up to `⌈numberOfPossibleResources / 8⌉` bytes), indicating which resources can
 *   appear on this terrain type; preserved raw, not decomposed into individual bit accessors.
 * @param workerJobAllowed A single enum-like value (not a bitmask, confirmed by both
 *   cross-referenced sources) identifying which `TFRM` worker job can be performed on this
 *   terrain type; preserved raw, not decomposed.
 * @param unknown 4 bytes with zero documented behavior from either cross-referenced source;
 *   preserved raw, not validated. Same treatment as `RaceEntry.unknown`.
 * @param unknown2 4 bytes with zero documented behavior from either cross-referenced source;
 *   preserved raw, not validated.
 * @param terrainFlags Opaque. Checked against both of this codebase's primary sources in full —
 *   Apolyton's original "Civilization III BIC file format (2nd thread)" (6 pages) and the later,
 *   fuller "Civilization III BIX/BIQ file format" thread (5 pages plus a third-party archive of
 *   a related dead thread) — neither names a single bit for this field, unlike every other
 *   opaque flags field in this codebase. `QueryCiv3` likewise exposes no named booleans for it.
 *   This is a confirmed dead end, not merely unresearched.
 */
data class TerrEntry(
    val numberOfPossibleResources: Int,
    val possibleResources: ByteString,
    val name: String,
    val civilopediaEntry: String,
    val irrigationBonus: Int,
    val miningBonus: Int,
    val roadBonus: Int,
    val defenseBonus: Int,
    val movementCost: Int,
    val food: Int,
    val shields: Int,
    val commerce: Int,
    val workerJobAllowed: Int,
    val pollutionEffect: Int,
    val allowCities: Byte,
    val allowColonies: Byte,
    val impassable: Byte,
    val impassableByWheeled: Byte,
    val allowAirfields: Byte,
    val allowForts: Byte,
    val allowOutposts: Byte,
    val allowRadarTowers: Byte,
    val unknown: ByteString,
    val landmarkEnabled: Byte,
    val landmarkFood: Int,
    val landmarkShields: Int,
    val landmarkCommerce: Int,
    val landmarkIrrigationBonus: Int,
    val landmarkMiningBonus: Int,
    val landmarkRoadBonus: Int,
    val landmarkMovementBonus: Int,
    val landmarkDefensiveBonus: Int,
    val landmarkName: String,
    val landmarkCivilopediaEntry: String,
    val unknown2: ByteString,
    val terrainFlags: Int,
    val diseaseStrength: Int,
) {
    init {
        require(possibleResources.size == (numberOfPossibleResources + 7) / 8) {
            "TerrEntry.possibleResources must be exactly " +
                "${(numberOfPossibleResources + 7) / 8} bytes for " +
                "numberOfPossibleResources=$numberOfPossibleResources, was ${possibleResources.size}"
        }
        require(unknown.size == 4) { "TerrEntry.unknown must be exactly 4 bytes, was ${unknown.size}" }
        require(unknown2.size == 4) { "TerrEntry.unknown2 must be exactly 4 bytes, was ${unknown2.size}" }
    }
}
