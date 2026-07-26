package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun ruleEntry(
    maximumLevel1CitySize: Int = 6,
    maximumLevel2CitySize: Int = 12,
    basicBarbarianUnitType: Int = -1,
    advancedBarbarianUnitType: Int = -1,
    barbarianSeaUnitType: Int = -1,
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
    defaultDifficultyLevel = -1,
    battleCreatedUnit = -1,
    buildArmyUnit = -1,
    buildingDefensiveBonus = 0,
    citizenDefensiveBonus = 0,
    defaultMoneyResource = -1,
    chanceToInterceptEnemyAirMissions = 0,
    chanceToInterceptEnemyStealthMissions = 0,
    startingTreasury = 0,
    unknown2 = ByteString.of(*ByteArray(4)),
    foodConsumptionPerCitizen = 0,
    riverDefensiveBonus = 0,
    turnPenaltyForEachHurrySacrifice = 0,
    scout = -1,
    slave = -1,
    movementAlongRoads = 0,
    startUnit1 = -1,
    startUnit2 = -1,
    minimumPopulationForWeLoveTheKing = 0,
    townDefenseBonus = 0,
    cityDefenseBonus = 0,
    metropolisDefenseBonus = 0,
    maximumLevel1CitySize = maximumLevel1CitySize,
    maximumLevel2CitySize = maximumLevel2CitySize,
    unknown3 = ByteString.of(*ByteArray(4)),
    fortificationsDefensiveBonus = 0,
    cultureLevelNames = emptyList(),
    borderExpansionMultiplier = 0,
    borderFactor = 0,
    futureTechCost = 0,
    goldenAgeDuration = 0,
    maximumResearchTime = 0,
    minimumResearchTime = 0,
    flagUnitType = -1,
    upgradeCost = 0,
)

private fun fileWithRule(entry: RuleEntry): Civ3File = Civ3File(
    Civ3Header(major = 12, minor = 0, description = "", title = ""),
    listOf(RuleSection(listOf(entry))),
)

private fun prtoEntry(name: String, type: Int): PrtoEntry = PrtoEntry(
    zoneOfControl = 0,
    name = name,
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
    required = -1,
    upgradeTo = -1,
    requiredResource1 = -1,
    requiredResource2 = -1,
    requiredResource3 = -1,
    abilities = 0,
    aiStrategies = 0,
    availableTo = 0,
    flags2 = ByteString.of(*ByteArray(8)),
    type = type,
    otherStrategy = 0,
    hpBonus = 0,
    standardOrders = 0,
    specialActions = 0,
    workerActions = 0,
    airMissions = 0,
    flags4 = ByteString.of(*ByteArray(4)),
    bombardEffects = 0,
    ignoreMovementCost = ByteString.of(),
    requireSupport = 0,
    unknown = ByteString.of(*ByteArray(16)),
    enslaveResults = -1,
    unknown2 = ByteString.of(*ByteArray(4)),
    stealthTargetUnitTypes = emptyList(),
    unknown3 = ByteString.of(*ByteArray(8)),
    createCraters = 0,
    workerStrength = 0f,
    unknown4 = ByteString.of(*ByteArray(4)),
    airDefense = 0,
)

private fun fileWithRuleAndPrto(rule: RuleEntry, prtos: List<PrtoEntry>): Civ3File = Civ3File(
    Civ3Header(major = 12, minor = 0, description = "", title = ""),
    listOf(RuleSection(listOf(rule)), PrtoSection(prtos)),
)

class RuleEntryValidationTest : FunSpec({

    test("returns no issues when level 2 max is strictly greater than level 1 max") {
        val file = fileWithRule(ruleEntry(maximumLevel1CitySize = 6, maximumLevel2CitySize = 12))

        validateCitySizeLevelThresholds(file) shouldBe emptyList()
    }

    test("returns no issues when level 1 max is zero (cities start at Level 2)") {
        val file = fileWithRule(ruleEntry(maximumLevel1CitySize = 0, maximumLevel2CitySize = 12))

        validateCitySizeLevelThresholds(file) shouldBe emptyList()
    }

    test("flags level 2 max equal to level 1 max") {
        val file = fileWithRule(ruleEntry(maximumLevel1CitySize = 6, maximumLevel2CitySize = 6))

        validateCitySizeLevelThresholds(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.RULE,
                0,
                "maximumLevel2CitySize",
                "maximumLevel2CitySize=6 must be strictly greater than maximumLevel1CitySize=6, or a city " +
                    "cannot grow past the Level 2 threshold before reaching the Level 1 one, and " +
                    "Level-2-gated buildings (e.g. Hospital) may never become buildable",
            ),
        )
    }

    test("flags level 2 max lower than level 1 max") {
        val file = fileWithRule(ruleEntry(maximumLevel1CitySize = 12, maximumLevel2CitySize = 6))

        validateCitySizeLevelThresholds(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.RULE,
                0,
                "maximumLevel2CitySize",
                "maximumLevel2CitySize=6 must be strictly greater than maximumLevel1CitySize=12, or a city " +
                    "cannot grow past the Level 2 threshold before reaching the Level 1 one, and " +
                    "Level-2-gated buildings (e.g. Hospital) may never become buildable",
            ),
        )
    }

    test("returns no issues when RULE is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateCitySizeLevelThresholds(file) shouldBe emptyList()
    }

    test("validateBarbarianUnitDomains returns no issues when all three domains are correct") {
        val prtos = listOf(prtoEntry("Warrior", type = 0), prtoEntry("Horseman", type = 0), prtoEntry("Galley", type = 1))
        val rule = ruleEntry(basicBarbarianUnitType = 0, advancedBarbarianUnitType = 1, barbarianSeaUnitType = 2)

        validateBarbarianUnitDomains(fileWithRuleAndPrto(rule, prtos)) shouldBe emptyList()
    }

    test("validateBarbarianUnitDomains returns no issues when barbarianSeaUnitType is -1 (unset)") {
        val prtos = listOf(prtoEntry("Warrior", type = 0), prtoEntry("Horseman", type = 0))
        val rule = ruleEntry(basicBarbarianUnitType = 0, advancedBarbarianUnitType = 1, barbarianSeaUnitType = -1)

        validateBarbarianUnitDomains(fileWithRuleAndPrto(rule, prtos)) shouldBe emptyList()
    }

    test("validateBarbarianUnitDomains flags a sea unit whose type isn't Sea") {
        val prtos = listOf(prtoEntry("Warrior", type = 0), prtoEntry("Horseman", type = 0), prtoEntry("Fighter", type = 2))
        val rule = ruleEntry(basicBarbarianUnitType = 0, advancedBarbarianUnitType = 1, barbarianSeaUnitType = 2)

        validateBarbarianUnitDomains(fileWithRuleAndPrto(rule, prtos)) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.RULE,
                0,
                "barbarianSeaUnitType",
                "barbarianSeaUnitType=2 (Fighter) has type=2, expected a Sea unit (type=1)",
            ),
        )
    }

    test("validateBarbarianUnitDomains flags a basic/advanced barbarian whose type isn't Land") {
        val prtos = listOf(prtoEntry("Galley", type = 1), prtoEntry("Fighter", type = 2))
        val rule = ruleEntry(basicBarbarianUnitType = 0, advancedBarbarianUnitType = 1, barbarianSeaUnitType = -1)

        validateBarbarianUnitDomains(fileWithRuleAndPrto(rule, prtos)) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.RULE,
                0,
                "basicBarbarianUnitType",
                "basicBarbarianUnitType=0 (Galley) has type=1, expected a Land unit (type=0)",
            ),
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.RULE,
                0,
                "advancedBarbarianUnitType",
                "advancedBarbarianUnitType=1 (Fighter) has type=2, expected a Land unit (type=0)",
            ),
        )
    }

    test("validateBarbarianUnitDomains returns no issues when a reference doesn't resolve") {
        val rule = ruleEntry(basicBarbarianUnitType = 99, advancedBarbarianUnitType = -1, barbarianSeaUnitType = -1)

        validateBarbarianUnitDomains(fileWithRuleAndPrto(rule, emptyList())) shouldBe emptyList()
    }

    test("validateBarbarianUnitDomains returns no issues when RULE is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), listOf(PrtoSection(emptyList())))

        validateBarbarianUnitDomains(file) shouldBe emptyList()
    }

    test("validateBarbarianUnitDomains returns no issues when PRTO is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), listOf(RuleSection(listOf(ruleEntry()))))

        validateBarbarianUnitDomains(file) shouldBe emptyList()
    }
})
