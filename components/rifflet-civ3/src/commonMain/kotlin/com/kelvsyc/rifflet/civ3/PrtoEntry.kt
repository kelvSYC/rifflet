package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `PRTO` section: a unit type (prototype) definition.
 *
 * @param required Likely a `TECH` section index (naming convention only); not confirmed by
 *   either cross-referenced source.
 * @param upgradeTo Likely a `PRTO` section self-reference (naming convention only); not
 *   confirmed by either cross-referenced source.
 * @param requiredResource1 Likely a `GOOD` section index (naming convention only); not
 *   confirmed by either cross-referenced source. Same treatment applies to
 *   [requiredResource2], [requiredResource3].
 * @param flags1 8 bytes with zero documented behavior from either cross-referenced source;
 *   preserved raw, not validated or decomposed. Same treatment as `RaceEntry.unknown`.
 * @param availableTo Opaque per-civilization availability bitmask; preserved raw, not
 *   decomposed.
 * @param flags2 8 bytes with zero documented behavior from either cross-referenced source;
 *   preserved raw, not validated or decomposed.
 * @param flags3 20 bytes with zero documented behavior from either cross-referenced source;
 *   preserved raw, not validated or decomposed.
 * @param ignoreMovementCost 14 bytes, one flag per `TERR` section entry, preserved raw as an
 *   opaque per-terrain-type bit array; not decomposed.
 * @param unknown 16 bytes with zero documented behavior from either cross-referenced source;
 *   preserved raw, not validated.
 * @param unknown2 4 bytes with zero documented behavior from either cross-referenced source;
 *   preserved raw, not validated.
 * @param stealthTargetUnitTypes Likely `PRTO` section self-references (naming convention
 *   only); not confirmed by either cross-referenced source.
 * @param unknown3 8 bytes with zero documented behavior from either cross-referenced source;
 *   preserved raw, not validated.
 * @param workerStrength Read as an IEEE-754 single-precision float via bit-reinterpretation of
 *   a little-endian `Int` read (`Float.fromBits`) — the first `Float` field in this codebase.
 * @param unknown4 4 bytes with zero documented behavior from either cross-referenced source;
 *   preserved raw, not validated.
 */
data class PrtoEntry(
    val zoneOfControl: Int,
    val name: String,
    val civilopediaEntry: String,
    val bombardStrength: Int,
    val bombardRange: Int,
    val capacity: Int,
    val shieldCost: Int,
    val defense: Int,
    val iconIndex: Int,
    val attack: Int,
    val operationalRange: Int,
    val populationCost: Int,
    val rateOfFire: Int,
    val movement: Int,
    val required: Int,
    val upgradeTo: Int,
    val requiredResource1: Int,
    val requiredResource2: Int,
    val requiredResource3: Int,
    val flags1: ByteString,
    val availableTo: Int,
    val flags2: ByteString,
    val type: Int,
    val otherStrategy: Int,
    val hpBonus: Int,
    val flags3: ByteString,
    val bombardEffects: Int,
    val ignoreMovementCost: ByteString,
    val requireSupport: Int,
    val unknown: ByteString,
    val enslaveResults: Int,
    val unknown2: ByteString,
    val stealthTargetUnitTypes: List<Int>,
    val unknown3: ByteString,
    val createCraters: Byte,
    val workerStrength: Float,
    val unknown4: ByteString,
    val airDefense: Int,
) {
    init {
        require(flags1.size == 8) { "PrtoEntry.flags1 must be exactly 8 bytes, was ${flags1.size}" }
        require(flags2.size == 8) { "PrtoEntry.flags2 must be exactly 8 bytes, was ${flags2.size}" }
        require(flags3.size == 20) { "PrtoEntry.flags3 must be exactly 20 bytes, was ${flags3.size}" }
        require(ignoreMovementCost.size == 14) {
            "PrtoEntry.ignoreMovementCost must be exactly 14 bytes, was ${ignoreMovementCost.size}"
        }
        require(unknown.size == 16) { "PrtoEntry.unknown must be exactly 16 bytes, was ${unknown.size}" }
        require(unknown2.size == 4) { "PrtoEntry.unknown2 must be exactly 4 bytes, was ${unknown2.size}" }
        require(unknown3.size == 8) { "PrtoEntry.unknown3 must be exactly 8 bytes, was ${unknown3.size}" }
        require(unknown4.size == 4) { "PrtoEntry.unknown4 must be exactly 4 bytes, was ${unknown4.size}" }
    }
}
