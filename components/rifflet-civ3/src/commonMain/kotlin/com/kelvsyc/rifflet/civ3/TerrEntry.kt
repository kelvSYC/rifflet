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
 *   reverse-engineering sources) identifying which `TFRM` worker job can be performed on this
 *   terrain type; preserved raw, not decomposed.
 * @param unknown 4 bytes with zero documented behavior from either reverse-engineering source;
 *   preserved raw, not validated. Same treatment as `RaceEntry.unknown`.
 * @param unknown2 4 bytes with zero documented behavior from either reverse-engineering source;
 *   preserved raw, not validated.
 * @param terrainFlags Mostly opaque — neither the earlier, BIC-format reverse-engineering
 *   documentation nor the later, fuller BIX/BIQ-format documentation names a single bit for this
 *   field, unlike every other opaque flags field in this codebase, and a separate
 *   reverse-engineered reference implementation likewise exposes no named booleans for it. Real
 *   terrain-default data from the Conquests map editor is dominated by a repeating `0xCC` bit
 *   pattern across roughly the top 26 of 32 bits — the classic MSVC debug-CRT poison pattern for
 *   uninitialized *stack* memory (distinct from the `0xCD` heap-poison pattern found in
 *   `GameEntry.unknown2`) — suggesting most of this field is unpopulated engine memory,
 *   consistent with neither source documenting it. Not checked against a PTW or vanilla map
 *   editor, only Conquests — the pattern could differ by era.
 *
 *   Two of the low bits falling outside that poison pattern are named: [causesDisease] (bit 2)
 *   and [curedBySanitation] (bit 3), matching the Conquests Rules Editor's per-terrain "Causes
 *   Disease"/"Cured by Sanitation" checkboxes. See `TerrEntryFlags.kt` for these and the
 *   remaining unexplained low bits (e.g. Mountains/Volcano share one bit, Marsh has that same
 *   bit plus [causesDisease] — real signal, still unnamed).
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
