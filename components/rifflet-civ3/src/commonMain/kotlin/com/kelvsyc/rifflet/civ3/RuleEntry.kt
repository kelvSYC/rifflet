package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `RULE` section: general game-rule settings.
 *
 * There is always exactly one `RULE` entry per file, per existing reverse-engineering
 * documentation's own "(1)" annotation on the section's item count.
 *
 * @param spaceshipPartQuantities The `General Settings` tab's "Spaceship Parts" groupbox: how
 *   many of each spaceship part type are needed to build a working spaceship, in part order. The
 *   groupbox's own "Spaceship Parts" count field is this list's size, not a separately stored
 *   value.
 * @param defaultUnits The `General Settings` tab's "Default Units" groupbox. See
 *   [RuleDefaultUnits].
 * @param defensiveBonuses The `General Settings` tab's "Defensive Bonuses" groupbox. See
 *   [RuleDefensiveBonuses].
 * @param unknown 8 bytes with zero documented behavior from either reverse-engineering source;
 *   preserved raw, not validated. Same treatment as `RaceEntry.unknown`.
 * @param defaultMoneyResource A `GOOD` section index, per the Conquests Rules Editor.
 * @param unknown2 4 bytes with zero documented behavior from either reverse-engineering source;
 *   preserved raw, not validated. Same treatment as `RaceEntry.unknown`.
 * @param unknown3 4 bytes with zero documented behavior from either reverse-engineering source;
 *   preserved raw, not validated. Same treatment as `RaceEntry.unknown`.
 * @param citySizeLevels The `General Settings` tab's "City Size Levels" groupbox. See
 *   [RuleCitySizeLevels].
 * @param culture The `General Settings` tab's "Culture" groupbox. See [RuleCulture].
 * @param technology The `General Settings` tab's "Technology" groupbox. See [RuleTechnology].
 * @param goldenAgeDuration The `General Settings` tab's "Golden Age" groupbox's "Duration
 *   (turns)" field — its own groupbox, not part of [technology] despite sitting between two of
 *   its members in the file.
 * @param upgradeCost [Civ3FormatEra.CONQUESTS]-only per a separate reverse-engineered reference
 *   implementation's comment; read defensively,
 *   defaults to `0` when absent from a file's declared item length — same treatment as
 *   `TechEntry.unknown`'s disputed trailing field, just `Int`-typed rather than
 *   `ByteString`-typed.
 */
data class RuleEntry(
    val citySizeLevels: RuleCitySizeLevels,
    val spaceshipPartQuantities: List<Int>,
    val defaultUnits: RuleDefaultUnits,
    val citiesNeededToSupportAnArmy: Int,
    val chanceOfRioting: Int,
    val turnPenaltyForEachDraftedCitizen: Int,
    val shieldCostPerGold: Int,
    val defensiveBonuses: RuleDefensiveBonuses,
    val citizensAffectedByEachHappyFace: Int,
    val unknown: ByteString,
    val forestValueInShields: Int,
    val shieldValueInGold: Int,
    val citizenValueInShields: Int,
    val defaultDifficultyLevel: Int,
    val defaultMoneyResource: Int,
    val chanceToInterceptEnemyAirMissions: Int,
    val chanceToInterceptEnemyStealthMissions: Int,
    val startingTreasury: Int,
    val unknown2: ByteString,
    val foodConsumptionPerCitizen: Int,
    val turnPenaltyForEachHurrySacrifice: Int,
    val movementAlongRoads: Int,
    val minimumPopulationForWeLoveTheKing: Int,
    val unknown3: ByteString,
    val culture: RuleCulture,
    val technology: RuleTechnology,
    val goldenAgeDuration: Int,
    val upgradeCost: Int,
) {
    init {
        require(unknown.size == 8) { "RuleEntry.unknown must be exactly 8 bytes, was ${unknown.size}" }
        require(unknown2.size == 4) { "RuleEntry.unknown2 must be exactly 4 bytes, was ${unknown2.size}" }
        require(unknown3.size == 4) { "RuleEntry.unknown3 must be exactly 4 bytes, was ${unknown3.size}" }
    }
}
