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
    flagUnitType: Int = 0,
): RuleEntry = RuleEntry(
    citySizeLevel1Name = "",
    citySizeLevel2Name = "",
    citySizeLevel3Name = "",
    spaceshipPartQuantities = emptyList(),
    advancedBarbarianUnitType = advancedBarbarianUnitType,
    basicBarbarianUnitType = basicBarbarianUnitType,
    barbarianSeaUnitType = barbarianSeaUnitType,
    citiesNeededToSupportAnArmy = 0,
    chanceOfRioting = 0,
    turnPenaltyForEachDraftedCitizen = 0,
    shieldCostPerGold = 0,
    fortressDefensiveBonus = 0,
    citizensAffectedByEachHappyFace = 0,
    unknown = ByteString.of(*ByteArray(8)),
    forestValueInShields = 0,
    shieldValueInGold = 0,
    citizenValueInShields = 0,
    defaultDifficultyLevel = 0,
    battleCreatedUnit = battleCreatedUnit,
    buildArmyUnit = buildArmyUnit,
    buildingDefensiveBonus = 0,
    citizenDefensiveBonus = 0,
    defaultMoneyResource = defaultMoneyResource,
    chanceToInterceptEnemyAirMissions = 0,
    chanceToInterceptEnemyStealthMissions = 0,
    startingTreasury = 0,
    unknown2 = ByteString.of(0, 0, 0, 0),
    foodConsumptionPerCitizen = 0,
    riverDefensiveBonus = 0,
    turnPenaltyForEachHurrySacrifice = 0,
    scout = scout,
    slave = slave,
    movementAlongRoads = 0,
    startUnit1 = startUnit1,
    startUnit2 = startUnit2,
    minimumPopulationForWeLoveTheKing = 0,
    townDefenseBonus = 0,
    cityDefenseBonus = 0,
    metropolisDefenseBonus = 0,
    maximumLevel1CitySize = 0,
    maximumLevel2CitySize = 0,
    unknown3 = ByteString.of(0, 0, 0, 0),
    fortificationsDefensiveBonus = 0,
    cultureLevelNames = emptyList(),
    borderExpansionMultiplier = 0,
    borderFactor = 0,
    futureTechCost = 0,
    goldenAgeDuration = 0,
    maximumResearchTime = 0,
    minimumResearchTime = 0,
    flagUnitType = flagUnitType,
    upgradeCost = 0,
)

private fun validPrtoEntry(): PrtoEntry = PrtoEntry(
    zoneOfControl = 0,
    name = "",
    civilopediaEntry = "",
    bombardStrength = 0,
    bombardRange = 0,
    capacity = 0,
    shieldCost = 0,
    defense = 0,
    iconIndex = 0,
    attack = 0,
    operationalRange = 0,
    populationCost = 0,
    rateOfFire = 0,
    movement = 0,
    required = 0,
    upgradeTo = 0,
    requiredResource1 = 0,
    requiredResource2 = 0,
    requiredResource3 = 0,
    flags1 = ByteString.of(*ByteArray(8)),
    availableTo = 0,
    flags2 = ByteString.of(*ByteArray(8)),
    type = 0,
    otherStrategy = 0,
    hpBonus = 0,
    flags3 = ByteString.of(*ByteArray(20)),
    bombardEffects = 0,
    ignoreMovementCost = ByteString.of(),
    requireSupport = 0,
    unknown = ByteString.of(*ByteArray(16)),
    enslaveResults = 0,
    unknown2 = ByteString.of(0, 0, 0, 0),
    stealthTargetUnitTypes = emptyList(),
    unknown3 = ByteString.of(*ByteArray(8)),
    createCraters = 0,
    workerStrength = 0f,
    unknown4 = ByteString.of(0, 0, 0, 0),
    airDefense = 0,
)

private fun validGoodEntry(): GoodEntry = GoodEntry(
    name = "",
    civilopediaEntry = "",
    type = 0,
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
})
