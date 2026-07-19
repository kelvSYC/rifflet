package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `RULE` section: general game-rule settings (there is always exactly one
 * `RULE` entry per file, per Apolyton's own "(1)" annotation on the section's item count).
 *
 * @param advancedBarbarianUnitType Likely a `PRTO` section index (naming convention only); not
 *   confirmed by either cross-referenced source. Same treatment applies to
 *   [basicBarbarianUnitType], [barbarianSeaUnitType], [battleCreatedUnit], [buildArmyUnit],
 *   [scout], [slave], [startUnit1], [startUnit2].
 * @param unknown 8 bytes with zero documented behavior from either cross-referenced source;
 *   preserved raw, not validated. Same treatment as `RaceEntry.unknown`.
 * @param defaultMoneyResource Likely a `GOOD` section index (naming convention only); not
 *   confirmed by either cross-referenced source.
 * @param unknown2 4 bytes with zero documented behavior from either cross-referenced source;
 *   preserved raw, not validated. Same treatment as `RaceEntry.unknown`.
 * @param unknown3 4 bytes with zero documented behavior from either cross-referenced source;
 *   preserved raw, not validated. Same treatment as `RaceEntry.unknown`.
 * @param flagUnitType A `PRTO` section index — explicitly documented by Apolyton's BIX/BIQ
 *   format reference ("flag unit (PRTO ref)"), not merely a naming-based inference.
 * @param upgradeCost [Civ3FormatEra.CONQUESTS]-only per `QueryCiv3`'s comment; read defensively,
 *   defaults to `0` when absent from a file's declared item length — same treatment as
 *   `TechEntry.unknown`'s disputed trailing field, just `Int`-typed rather than
 *   `ByteString`-typed.
 */
data class RuleEntry(
    val citySizeLevel1Name: String,
    val citySizeLevel2Name: String,
    val citySizeLevel3Name: String,
    val spaceshipPartQuantities: List<Int>,
    val advancedBarbarianUnitType: Int,
    val basicBarbarianUnitType: Int,
    val barbarianSeaUnitType: Int,
    val citiesNeededToSupportAnArmy: Int,
    val chanceOfRioting: Int,
    val turnPenaltyForEachDraftedCitizen: Int,
    val shieldCostPerGold: Int,
    val fortressDefensiveBonus: Int,
    val citizensAffectedByEachHappyFace: Int,
    val unknown: ByteString,
    val forestValueInShields: Int,
    val shieldValueInGold: Int,
    val citizenValueInShields: Int,
    val defaultDifficultyLevel: Int,
    val battleCreatedUnit: Int,
    val buildArmyUnit: Int,
    val buildingDefensiveBonus: Int,
    val citizenDefensiveBonus: Int,
    val defaultMoneyResource: Int,
    val chanceToInterceptEnemyAirMissions: Int,
    val chanceToInterceptEnemyStealthMissions: Int,
    val startingTreasury: Int,
    val unknown2: ByteString,
    val foodConsumptionPerCitizen: Int,
    val riverDefensiveBonus: Int,
    val turnPenaltyForEachHurrySacrifice: Int,
    val scout: Int,
    val slave: Int,
    val movementAlongRoads: Int,
    val startUnit1: Int,
    val startUnit2: Int,
    val minimumPopulationForWeLoveTheKing: Int,
    val townDefenseBonus: Int,
    val cityDefenseBonus: Int,
    val metropolisDefenseBonus: Int,
    val maximumLevel1CitySize: Int,
    val maximumLevel2CitySize: Int,
    val unknown3: ByteString,
    val fortificationsDefensiveBonus: Int,
    val cultureLevelNames: List<String>,
    val borderExpansionMultiplier: Int,
    val borderFactor: Int,
    val futureTechCost: Int,
    val goldenAgeDuration: Int,
    val maximumResearchTime: Int,
    val minimumResearchTime: Int,
    val flagUnitType: Int,
    val upgradeCost: Int,
) {
    init {
        require(unknown.size == 8) { "RuleEntry.unknown must be exactly 8 bytes, was ${unknown.size}" }
        require(unknown2.size == 4) { "RuleEntry.unknown2 must be exactly 4 bytes, was ${unknown2.size}" }
        require(unknown3.size == 4) { "RuleEntry.unknown3 must be exactly 4 bytes, was ${unknown3.size}" }
    }
}
