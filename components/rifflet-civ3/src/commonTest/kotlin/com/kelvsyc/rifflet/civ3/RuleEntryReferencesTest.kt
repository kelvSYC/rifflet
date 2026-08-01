package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validRuleEntry(
    advancedBarbarianUnitType: Int = 0,
    basicBarbarianUnitType: Int = 0,
    barbarianSeaUnitType: Int = 0,
    battleCreatedUnit: Int = 0,
    buildArmyUnit: Int = 0,
    defaultMoneyResource: Int = 0,
    scout: Int = 0,
    slave: Int = 0,
    startUnit1: Int = 0,
    startUnit2: Int = 0,
    flagUnitType: Int? = 0,
): RuleEntry = RuleEntry(
    citySizeLevels = RuleCitySizeLevels("", "", "", 0, 0),
    spaceshipPartQuantities = emptyList(),
    defaultUnits = RuleDefaultUnits(
        advancedBarbarianUnitType = advancedBarbarianUnitType,
        basicBarbarianUnitType = basicBarbarianUnitType,
        barbarianSeaUnitType = barbarianSeaUnitType,
        battleCreatedUnit = battleCreatedUnit,
        buildArmyUnit = buildArmyUnit,
        scout = scout,
        slave = slave,
        startUnit1 = startUnit1,
        startUnit2 = startUnit2,
        flagUnitType = flagUnitType,
    ),
    citiesNeededToSupportAnArmy = 0,
    chanceOfRioting = 0,
    turnPenaltyForEachDraftedCitizen = 0,
    shieldCostPerGold = 0,
    defensiveBonuses = RuleDefensiveBonuses(0, 0, 0, 0, 0, 0, 0, 0),
    citizensAffectedByEachHappyFace = 0,
    unknown = ByteString.of(*ByteArray(8)),
    forestValueInShields = 0,
    shieldValueInGold = 0,
    citizenValueInShields = 0,
    defaultDifficultyLevel = 0,
    defaultMoneyResource = defaultMoneyResource,
    chanceToInterceptEnemyAirMissions = 0,
    chanceToInterceptEnemyStealthMissions = 0,
    startingTreasury = 0,
    unknown2 = ByteString.of(0, 0, 0, 0),
    foodConsumptionPerCitizen = 0,
    turnPenaltyForEachHurrySacrifice = 0,
    movementAlongRoads = 0,
    minimumPopulationForWeLoveTheKing = 0,
    unknown3 = ByteString.of(0, 0, 0, 0),
    culture = RuleCulture(emptyList(), 0, 0),
    technology = RuleTechnology(0, 0, 0),
    goldenAgeDuration = 0,
    upgradeCost = 0,
)

private fun validPrtoEntry(): PrtoEntry = PrtoEntry(
    unitStatistics = PrtoUnitStatistics(
        zoneOfControl = 0, bombardStrength = 0, bombardRange = 0, capacity = 0, shieldCost = 0,
        defense = 0, attack = 0, operationalRange = 0, populationCost = 0, rateOfFire = 0,
        movement = 0, upgradeTo = 0, hpBonus = 0, bombardEffects = 0, requireSupport = 0,
        createCraters = 0, workerStrength = 0f, airDefense = 0,
    ),
    name = "",
    civilopediaEntry = "",
    iconIndex = 0,
    required = 0,
    requiredResource1 = 0,
    requiredResource2 = 0,
    requiredResource3 = 0,
    abilities = 0,
    aiStrategies = 0,
    availableTo = 0,
    flags2 = ByteString.of(*ByteArray(8)),
    type = 0,
    otherStrategy = 0,
    standardOrders = 0,
    specialActions = 0,
    workerActions = 0,
    airMissions = 0,
    flags4 = ByteString.of(*ByteArray(4)),
    ignoreMovementCost = ByteString.of(),
    unknown = ByteString.of(*ByteArray(16)),
    enslaveResults = 0,
    unknown2 = ByteString.of(0, 0, 0, 0),
    stealthTargetUnitTypes = emptyList(),
    unknown3 = ByteString.of(*ByteArray(8)),
    unknown4 = ByteString.of(0, 0, 0, 0),
)

private fun validGoodEntry(): GoodEntry = GoodEntry(
    name = "",
    civilopediaEntry = "",
    type = GoodResourceType.BONUS,
    appearanceRatio = 0,
    disappearanceProbability = 0,
    icon = 0,
    prerequisite = 0,
    foodBonus = 0,
    shieldsBonus = 0,
    commerceBonus = 0,
)

class RuleEntryReferencesTest : FunSpec({

    test("advancedBarbarianUnitTypePrto resolves against the PRTO list") {
        val prto = validPrtoEntry()
        validRuleEntry(advancedBarbarianUnitType = 0).advancedBarbarianUnitTypePrto(listOf(prto)) shouldBe prto
        validRuleEntry(advancedBarbarianUnitType = 5).advancedBarbarianUnitTypePrto(emptyList()) shouldBe null
    }

    test("basicBarbarianUnitTypePrto resolves against the PRTO list") {
        val prto = validPrtoEntry()
        validRuleEntry(basicBarbarianUnitType = 0).basicBarbarianUnitTypePrto(listOf(prto)) shouldBe prto
        validRuleEntry(basicBarbarianUnitType = 5).basicBarbarianUnitTypePrto(emptyList()) shouldBe null
    }

    test("barbarianSeaUnitTypePrto resolves against the PRTO list") {
        val prto = validPrtoEntry()
        validRuleEntry(barbarianSeaUnitType = 0).barbarianSeaUnitTypePrto(listOf(prto)) shouldBe prto
        validRuleEntry(barbarianSeaUnitType = 5).barbarianSeaUnitTypePrto(emptyList()) shouldBe null
    }

    test("battleCreatedUnitPrto resolves against the PRTO list") {
        val prto = validPrtoEntry()
        validRuleEntry(battleCreatedUnit = 0).battleCreatedUnitPrto(listOf(prto)) shouldBe prto
        validRuleEntry(battleCreatedUnit = 5).battleCreatedUnitPrto(emptyList()) shouldBe null
    }

    test("buildArmyUnitPrto resolves against the PRTO list") {
        val prto = validPrtoEntry()
        validRuleEntry(buildArmyUnit = 0).buildArmyUnitPrto(listOf(prto)) shouldBe prto
        validRuleEntry(buildArmyUnit = 5).buildArmyUnitPrto(emptyList()) shouldBe null
    }

    test("scoutPrto resolves against the PRTO list") {
        val prto = validPrtoEntry()
        validRuleEntry(scout = 0).scoutPrto(listOf(prto)) shouldBe prto
        validRuleEntry(scout = 5).scoutPrto(emptyList()) shouldBe null
    }

    test("slavePrto resolves against the PRTO list") {
        val prto = validPrtoEntry()
        validRuleEntry(slave = 0).slavePrto(listOf(prto)) shouldBe prto
        validRuleEntry(slave = 5).slavePrto(emptyList()) shouldBe null
    }

    test("startUnit1Prto resolves against the PRTO list") {
        val prto = validPrtoEntry()
        validRuleEntry(startUnit1 = 0).startUnit1Prto(listOf(prto)) shouldBe prto
        validRuleEntry(startUnit1 = 5).startUnit1Prto(emptyList()) shouldBe null
    }

    test("startUnit2Prto resolves against the PRTO list") {
        val prto = validPrtoEntry()
        validRuleEntry(startUnit2 = 0).startUnit2Prto(listOf(prto)) shouldBe prto
        validRuleEntry(startUnit2 = 5).startUnit2Prto(emptyList()) shouldBe null
    }

    test("defaultMoneyResourceGood resolves against the GOOD list") {
        val good = validGoodEntry()
        validRuleEntry(defaultMoneyResource = 0).defaultMoneyResourceGood(listOf(good)) shouldBe good
        validRuleEntry(defaultMoneyResource = 5).defaultMoneyResourceGood(emptyList()) shouldBe null
    }

    test("flagUnitTypePrto resolves against the PRTO list") {
        val prto = validPrtoEntry()
        validRuleEntry(flagUnitType = 0).flagUnitTypePrto(listOf(prto)) shouldBe prto
        validRuleEntry(flagUnitType = 5).flagUnitTypePrto(emptyList()) shouldBe null
    }

    test("flagUnitTypePrto returns null outright when flagUnitType itself is null") {
        val prto = validPrtoEntry()
        validRuleEntry(flagUnitType = null).flagUnitTypePrto(listOf(prto)) shouldBe null
    }
})
