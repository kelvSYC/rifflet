package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.RuleCitySizeLevels
import com.kelvsyc.rifflet.civ3.RuleCulture
import com.kelvsyc.rifflet.civ3.RuleDefensiveBonuses
import com.kelvsyc.rifflet.civ3.RuleTechnology
import okio.ByteString

/**
 * The scenario's general game-rule settings, mutable — the domain-layer counterpart to
 * [com.kelvsyc.rifflet.civ3.RuleEntry].
 *
 * @param citySizeLevels Reused directly from the wire layer — no cross-references, nothing a
 *   domain-layer clone would add. Same treatment for [defensiveBonuses], [culture], [technology].
 * @param spaceshipPartQuantities How many of each spaceship part type are needed to build a
 *   working spaceship, in part order — plain list, not resolved against any named identity.
 *   `BLDG`'s `SpaceshipPart.partIndex` references a position in this list, but that position has
 *   no name/identity of its own (just a bare quantity), so there's no richer object to resolve
 *   `partIndex` against — left untouched.
 * @param defaultUnits This scenario's default and barbarian unit assignments, with each `PRTO`
 *   index resolved. See [DefaultUnits].
 * @param defaultDifficulty This scenario's default difficulty level, if it resolves.
 * @param defaultMoneyResource The default money-equivalent resource, if it resolves.
 * @param upgradeCost [com.kelvsyc.rifflet.civ3.Civ3FormatEra.CONQUESTS]-only per existing
 *   documentation; the wire-layer parser already defaults this to `0` when absent from a file's
 *   declared item length, so there's nothing left for the domain layer to model specially — stays
 *   a plain `Int`, not nullable.
 */
data class Rule(
    var citySizeLevels: RuleCitySizeLevels,
    var spaceshipPartQuantities: MutableList<Int> = mutableListOf(),
    var defaultUnits: DefaultUnits,
    var citiesNeededToSupportAnArmy: Int = 0,
    var chanceOfRioting: Int = 0,
    var turnPenaltyForEachDraftedCitizen: Int = 0,
    var shieldCostPerGold: Int = 0,
    var defensiveBonuses: RuleDefensiveBonuses,
    var citizensAffectedByEachHappyFace: Int = 0,
    var unknown: ByteString = ByteString.of(*ByteArray(8)),
    var forestValueInShields: Int = 0,
    var shieldValueInGold: Int = 0,
    var citizenValueInShields: Int = 0,
    var defaultDifficulty: Difficulty? = null,
    var defaultMoneyResource: Resource? = null,
    var chanceToInterceptEnemyAirMissions: Int = 0,
    var chanceToInterceptEnemyStealthMissions: Int = 0,
    var startingTreasury: Int = 0,
    var unknown2: ByteString = ByteString.of(*ByteArray(4)),
    var foodConsumptionPerCitizen: Int = 0,
    var turnPenaltyForEachHurrySacrifice: Int = 0,
    var movementAlongRoads: Int = 0,
    var minimumPopulationForWeLoveTheKing: Int = 0,
    var unknown3: ByteString = ByteString.of(*ByteArray(4)),
    var culture: RuleCulture,
    var technology: RuleTechnology,
    var goldenAgeDuration: Int = 0,
    var upgradeCost: Int = 0,
)
