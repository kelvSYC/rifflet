package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.RuleCitySizeLevels
import com.kelvsyc.rifflet.civ3.RuleCulture
import com.kelvsyc.rifflet.civ3.RuleDefaultUnits
import com.kelvsyc.rifflet.civ3.RuleDefensiveBonuses
import com.kelvsyc.rifflet.civ3.RuleEntry
import com.kelvsyc.rifflet.civ3.RuleTechnology
import okio.Buffer

/**
 * Parses one `RULE` item, per existing reverse-engineering documentation of the BIX/BIQ format. Reads directly off
 * [item], a zero-copy-transferred [Buffer] already stripped of its own length prefix by the
 * generic section loop.
 *
 * Both trailing fields are read defensively: [Civ3FormatEra.VANILLA] files have neither
 * [RuleDefaultUnits.flagUnitType] nor [RuleEntry.upgradeCost]; [Civ3FormatEra.PTW] files (`VER#`
 * header `minor=18` — the only PTW minor confirmed to have a `RULE` section, so other PTW minors'
 * shape here is unconfirmed) have [RuleDefaultUnits.flagUnitType] but not [RuleEntry.upgradeCost]
 * (a separate reverse-engineered reference implementation's struct comments the latter
 * `// Only in conquests`); [Civ3FormatEra.CONQUESTS]
 * files have both. Because the generic section loop in `Civ3RootParserImpl` already slices
 * [item] to the file's own declared length, `item.size` reliably reflects how many bytes
 * actually remain for this specific file.
 *
 * Both dynamic-array counts (`numberOfSpaceshipParts`, `numberOfCultureLevels`) are validated
 * via [requireSaneCount] before sizing their respective lists — see that function's KDoc for
 * why.
 *
 * [RuleEntry.defaultUnits], [RuleEntry.defensiveBonuses], and [RuleEntry.citySizeLevels] are each
 * assembled from fields that are not contiguous in the file — every member field is still read
 * at its original position, exactly as before this codebase grouped these fields; only the final
 * assembly into a group object happens out of read order.
 */
internal object RuleEntryParser {
    fun parse(item: Buffer): RuleEntry {
        val citySizeLevel1Name = item.readByteString(32L).truncateAtFirstNull()
        val citySizeLevel2Name = item.readByteString(32L).truncateAtFirstNull()
        val citySizeLevel3Name = item.readByteString(32L).truncateAtFirstNull()
        val numberOfSpaceshipParts = item.requireSaneCount(item.readIntLe(), 4L, "RuleEntry.spaceshipPartQuantities")
        val spaceshipPartQuantities = List(numberOfSpaceshipParts) { item.readIntLe() }
        val advancedBarbarianUnitType = item.readIntLe()
        val basicBarbarianUnitType = item.readIntLe()
        val barbarianSeaUnitType = item.readIntLe()
        val citiesNeededToSupportAnArmy = item.readIntLe()
        val chanceOfRioting = item.readIntLe()
        val turnPenaltyForEachDraftedCitizen = item.readIntLe()
        val shieldCostPerGold = item.readIntLe()
        val fortressDefensiveBonus = item.readIntLe()
        val citizensAffectedByEachHappyFace = item.readIntLe()
        val unknown = item.readByteString(8L)
        val forestValueInShields = item.readIntLe()
        val shieldValueInGold = item.readIntLe()
        val citizenValueInShields = item.readIntLe()
        val defaultDifficultyLevel = item.readIntLe()
        val battleCreatedUnit = item.readIntLe()
        val buildArmyUnit = item.readIntLe()
        val buildingDefensiveBonus = item.readIntLe()
        val citizenDefensiveBonus = item.readIntLe()
        val defaultMoneyResource = item.readIntLe()
        val chanceToInterceptEnemyAirMissions = item.readIntLe()
        val chanceToInterceptEnemyStealthMissions = item.readIntLe()
        val startingTreasury = item.readIntLe()
        val unknown2 = item.readByteString(4L)
        val foodConsumptionPerCitizen = item.readIntLe()
        val riverDefensiveBonus = item.readIntLe()
        val turnPenaltyForEachHurrySacrifice = item.readIntLe()
        val scout = item.readIntLe()
        val slave = item.readIntLe()
        val movementAlongRoads = item.readIntLe()
        val startUnit1 = item.readIntLe()
        val startUnit2 = item.readIntLe()
        val minimumPopulationForWeLoveTheKing = item.readIntLe()
        val townDefenseBonus = item.readIntLe()
        val cityDefenseBonus = item.readIntLe()
        val metropolisDefenseBonus = item.readIntLe()
        val maximumLevel1CitySize = item.readIntLe()
        val maximumLevel2CitySize = item.readIntLe()
        val unknown3 = item.readByteString(4L)
        val fortificationsDefensiveBonus = item.readIntLe()
        val numberOfCultureLevels = item.requireSaneCount(item.readIntLe(), 64L, "RuleEntry.cultureLevelNames")
        val cultureLevelNames = List(numberOfCultureLevels) {
            item.readByteString(64L).truncateAtFirstNull()
        }
        val borderExpansionMultiplier = item.readIntLe()
        val borderFactor = item.readIntLe()
        val futureTechCost = item.readIntLe()
        val goldenAgeDuration = item.readIntLe()
        val maximumResearchTime = item.readIntLe()
        val minimumResearchTime = item.readIntLe()
        val flagUnitType = if (item.size >= 4L) item.readIntLe() else null
        val upgradeCost = if (item.size >= 4L) item.readIntLe() else 0
        return RuleEntry(
            citySizeLevels = RuleCitySizeLevels(
                citySizeLevel1Name, citySizeLevel2Name, citySizeLevel3Name,
                maximumLevel1CitySize, maximumLevel2CitySize,
            ),
            spaceshipPartQuantities = spaceshipPartQuantities,
            defaultUnits = RuleDefaultUnits(
                advancedBarbarianUnitType, basicBarbarianUnitType, barbarianSeaUnitType,
                battleCreatedUnit, buildArmyUnit, scout, slave, startUnit1, startUnit2, flagUnitType,
            ),
            citiesNeededToSupportAnArmy = citiesNeededToSupportAnArmy,
            chanceOfRioting = chanceOfRioting,
            turnPenaltyForEachDraftedCitizen = turnPenaltyForEachDraftedCitizen,
            shieldCostPerGold = shieldCostPerGold,
            defensiveBonuses = RuleDefensiveBonuses(
                fortressDefensiveBonus, buildingDefensiveBonus, citizenDefensiveBonus,
                riverDefensiveBonus, townDefenseBonus, cityDefenseBonus, metropolisDefenseBonus,
                fortificationsDefensiveBonus,
            ),
            citizensAffectedByEachHappyFace = citizensAffectedByEachHappyFace,
            unknown = unknown,
            forestValueInShields = forestValueInShields,
            shieldValueInGold = shieldValueInGold,
            citizenValueInShields = citizenValueInShields,
            defaultDifficultyLevel = defaultDifficultyLevel,
            defaultMoneyResource = defaultMoneyResource,
            chanceToInterceptEnemyAirMissions = chanceToInterceptEnemyAirMissions,
            chanceToInterceptEnemyStealthMissions = chanceToInterceptEnemyStealthMissions,
            startingTreasury = startingTreasury,
            unknown2 = unknown2,
            foodConsumptionPerCitizen = foodConsumptionPerCitizen,
            turnPenaltyForEachHurrySacrifice = turnPenaltyForEachHurrySacrifice,
            movementAlongRoads = movementAlongRoads,
            minimumPopulationForWeLoveTheKing = minimumPopulationForWeLoveTheKing,
            unknown3 = unknown3,
            culture = RuleCulture(cultureLevelNames, borderExpansionMultiplier, borderFactor),
            technology = RuleTechnology(futureTechCost, maximumResearchTime, minimumResearchTime),
            goldenAgeDuration = goldenAgeDuration,
            upgradeCost = upgradeCost,
        )
    }
}
