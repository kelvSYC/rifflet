package com.kelvsyc.rifflet.civ3.domain

/**
 * A unit type's numeric statistics, upgrade path, and combat-support/creation flags — the
 * domain-layer counterpart to [com.kelvsyc.rifflet.civ3.PrtoUnitStatistics].
 *
 * The wire type's 5 era-gated fields ([bombardEffects]/[requireSupport] absent before PTW;
 * [createCraters]/[workerStrength]/[airDefense] absent before Conquests) are plain, non-nullable,
 * zero-defaulted fields here — the domain layer is fully era-independent. `toDomain()` fills in
 * these defaults for whatever the source file's era doesn't carry; `toWire()` writes back only
 * what the target era can express.
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
 * @param rateOfFire The "Rate of Fire" field.
 * @param movement The "Moves" field.
 * @param upgradeTo A prerequisite unit type, per the "Upgrade" dropdown.
 * @param hpBonus The "HP Bonus" field.
 * @param bombardEffects The "Bombard Fx" checkbox's value.
 * @param requireSupport The "Req. Support" checkbox's value.
 * @param createCraters The "Create Craters" checkbox's value.
 * @param workerStrength The "Worker Strength" field.
 * @param airDefense The "Air Defense" field.
 */
data class PrtoUnitStatistics(
    var zoneOfControl: Int = 0,
    var bombardStrength: Int = 0,
    var bombardRange: Int = 0,
    var capacity: Int = 0,
    var shieldCost: Int = 0,
    var defense: Int = 0,
    var attack: Int = 0,
    var operationalRange: Int = 0,
    var populationCost: Int = 0,
    var rateOfFire: Int = 0,
    var movement: Int = 0,
    var upgradeTo: Prto? = null,
    var hpBonus: Int = 0,
    var bombardEffects: Int = 0,
    var requireSupport: Int = 0,
    var createCraters: Byte = 0,
    var workerStrength: Float = 0f,
    var airDefense: Int = 0,
)
