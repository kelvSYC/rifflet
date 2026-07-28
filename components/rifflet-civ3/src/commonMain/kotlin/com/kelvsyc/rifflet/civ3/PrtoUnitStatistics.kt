package com.kelvsyc.rifflet.civ3

/**
 * A unit type's numeric statistics, upgrade path, and combat-support/creation flags.
 *
 * Corresponds to the Conquests Rules Editor's `Units` tab's "Unit Statistics" groupbox, in its
 * entirety, confirmed identical in the PTW tab.
 *
 * @param zoneOfControl The "Zone of Control" checkbox.
 * @param bombardStrength The "Bombard" field.
 * @param bombardRange The "Bombard Range" field.
 * @param capacity The "Trans." field: cargo capacity.
 * @param shieldCost The "Shield" field: production cost.
 * @param defense The "Defense" field.
 * @param attack The "Attack" field.
 * @param operationalRange The "Operational" field.
 * @param populationCost The "Pop. Cost" field.
 * @param rateOfFire The "Rate of [Fire]" field.
 * @param movement The "Moves" field.
 * @param upgradeTo A `PRTO` section self-reference, per the "Upgrade" dropdown.
 * @param hpBonus The "HP Bonus" field.
 * @param bombardEffects The "Bombard Fx" checkbox — `null` in [Civ3FormatEra.VANILLA] files,
 *   which predate this and the 2 fields below.
 * @param requireSupport The "Req. Support" checkbox — same era treatment as [bombardEffects].
 * @param createCraters The "Create Craters" checkbox — `null` in [Civ3FormatEra.VANILLA] and
 *   [Civ3FormatEra.PTW] files, which predate this and the 2 fields below.
 * @param workerStrength The "Worker Strength" field. Read as an IEEE-754 single-precision float
 *   via bit-reinterpretation of a little-endian `Int` read (`Float.fromBits`) — same era
 *   treatment as [createCraters].
 * @param airDefense The "Air Defense" field — same era treatment as [createCraters].
 */
data class PrtoUnitStatistics(
    val zoneOfControl: Int,
    val bombardStrength: Int,
    val bombardRange: Int,
    val capacity: Int,
    val shieldCost: Int,
    val defense: Int,
    val attack: Int,
    val operationalRange: Int,
    val populationCost: Int,
    val rateOfFire: Int,
    val movement: Int,
    val upgradeTo: Int,
    val hpBonus: Int,
    val bombardEffects: Int?,
    val requireSupport: Int?,
    val createCraters: Byte?,
    val workerStrength: Float?,
    val airDefense: Int?,
)
