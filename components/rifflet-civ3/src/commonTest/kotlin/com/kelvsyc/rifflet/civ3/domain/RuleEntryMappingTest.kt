package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.GoodResourceType
import com.kelvsyc.rifflet.civ3.PrtoDomain
import com.kelvsyc.rifflet.civ3.RuleCitySizeLevels
import com.kelvsyc.rifflet.civ3.RuleCulture
import com.kelvsyc.rifflet.civ3.RuleDefaultUnits
import com.kelvsyc.rifflet.civ3.RuleDefensiveBonuses
import com.kelvsyc.rifflet.civ3.RuleEntry
import com.kelvsyc.rifflet.civ3.RuleTechnology
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun ruleDefaultUnits(flagUnitType: Int? = null): RuleDefaultUnits = RuleDefaultUnits(
    advancedBarbarianUnitType = 0, basicBarbarianUnitType = 1, barbarianSeaUnitType = 2,
    battleCreatedUnit = 3, buildArmyUnit = 4, scout = 5, slave = 6, startUnit1 = 7, startUnit2 = 8,
    flagUnitType = flagUnitType,
)

private fun ruleEntry(
    defaultDifficultyLevel: Int = -1,
    defaultMoneyResource: Int = -1,
    defaultUnits: RuleDefaultUnits = ruleDefaultUnits(),
): RuleEntry = RuleEntry(
    citySizeLevels = RuleCitySizeLevels(
        citySizeLevel1Name = "Town", citySizeLevel2Name = "City", citySizeLevel3Name = "Metropolis",
        maximumLevel1CitySize = 6, maximumLevel2CitySize = 12,
    ),
    spaceshipPartQuantities = listOf(1, 2, 3),
    defaultUnits = defaultUnits,
    citiesNeededToSupportAnArmy = 1,
    chanceOfRioting = 2,
    turnPenaltyForEachDraftedCitizen = 3,
    shieldCostPerGold = 4,
    defensiveBonuses = RuleDefensiveBonuses(
        fortress = 1, building = 2, citizen = 3, river = 4, town = 5, city = 6, metropolis = 7, fortifications = 8,
    ),
    citizensAffectedByEachHappyFace = 5,
    unknown = ByteString.of(*ByteArray(8)),
    forestValueInShields = 6,
    shieldValueInGold = 7,
    citizenValueInShields = 8,
    defaultDifficultyLevel = defaultDifficultyLevel,
    defaultMoneyResource = defaultMoneyResource,
    chanceToInterceptEnemyAirMissions = 9,
    chanceToInterceptEnemyStealthMissions = 10,
    startingTreasury = 11,
    unknown2 = ByteString.of(*ByteArray(4)),
    foodConsumptionPerCitizen = 12,
    turnPenaltyForEachHurrySacrifice = 13,
    movementAlongRoads = 14,
    minimumPopulationForWeLoveTheKing = 15,
    unknown3 = ByteString.of(*ByteArray(4)),
    culture = RuleCulture(cultureLevelNames = listOf("Fledgling", "Weak"), borderExpansionMultiplier = 1, borderFactor = 2),
    technology = RuleTechnology(futureTechCost = 16, maximumResearchTime = 17, minimumResearchTime = 18),
    goldenAgeDuration = 19,
    upgradeCost = 20,
)

private fun prto(name: String): Prto = Prto(name = name, civilopediaEntry = "", iconIndex = 0, type = PrtoDomain.LAND)
private fun resource(name: String): Resource = Resource(name = name, type = GoodResourceType.BONUS)
private fun difficulty(name: String): Difficulty = Difficulty(name = name)

class RuleEntryMappingTest : FunSpec({

    test("toDomain maps scalar fields and reused sub-types straight across") {
        val entry = ruleEntry()

        val rule = listOf(entry).toDomain(emptyList(), emptyList(), emptyList()).single()

        rule.citySizeLevels shouldBe entry.citySizeLevels
        rule.spaceshipPartQuantities shouldBe mutableListOf(1, 2, 3)
        rule.citiesNeededToSupportAnArmy shouldBe 1
        rule.chanceOfRioting shouldBe 2
        rule.defensiveBonuses shouldBe entry.defensiveBonuses
        rule.culture shouldBe entry.culture
        rule.technology shouldBe entry.technology
        rule.goldenAgeDuration shouldBe 19
        rule.upgradeCost shouldBe 20
    }

    test("toDomain resolves DefaultUnits' 9 always-present Prto fields positionally") {
        val prtos = (0..8).map { prto("Unit$it") }
        val entry = ruleEntry()

        val rule = listOf(entry).toDomain(prtos, emptyList(), emptyList()).single()

        rule.defaultUnits.advancedBarbarianUnitType shouldBe prtos[0]
        rule.defaultUnits.basicBarbarianUnitType shouldBe prtos[1]
        rule.defaultUnits.barbarianSeaUnitType shouldBe prtos[2]
        rule.defaultUnits.battleCreatedUnit shouldBe prtos[3]
        rule.defaultUnits.buildArmyUnit shouldBe prtos[4]
        rule.defaultUnits.scout shouldBe prtos[5]
        rule.defaultUnits.slave shouldBe prtos[6]
        rule.defaultUnits.startUnit1 shouldBe prtos[7]
        rule.defaultUnits.startUnit2 shouldBe prtos[8]
    }

    test("toDomain resolves flagUnitType: null when absent from era, resolved Prto when present") {
        val prtos = listOf(prto("Flag Unit"))
        val absentEntry = ruleEntry(defaultUnits = ruleDefaultUnits(flagUnitType = null))
        val presentEntry = ruleEntry(defaultUnits = ruleDefaultUnits(flagUnitType = 0))

        val rules = listOf(absentEntry, presentEntry).toDomain(prtos, emptyList(), emptyList())

        rules[0].defaultUnits.flagUnitType shouldBe null
        rules[1].defaultUnits.flagUnitType shouldBe prtos[0]
    }

    test("toDomain resolves defaultMoneyResource and defaultDifficulty, null when dangling") {
        val wine = resource("Wine")
        val deity = difficulty("Deity")
        val entries = listOf(
            ruleEntry(defaultMoneyResource = 0, defaultDifficultyLevel = 0),
            ruleEntry(defaultMoneyResource = -1, defaultDifficultyLevel = -1),
        )

        val rules = entries.toDomain(emptyList(), listOf(wine), listOf(deity))

        rules[0].defaultMoneyResource shouldBe wine
        rules[0].defaultDifficulty shouldBe deity
        rules[1].defaultMoneyResource shouldBe null
        rules[1].defaultDifficulty shouldBe null
    }

    test("toDomain().toWire() round-trips") {
        val prtos = (0..8).map { prto("Unit$it") }
        val wine = resource("Wine")
        val deity = difficulty("Deity")
        val entries = listOf(
            ruleEntry(
                defaultUnits = ruleDefaultUnits(flagUnitType = 0),
                defaultMoneyResource = 0,
                defaultDifficultyLevel = 0,
            ),
        )

        val roundTripped = entries.toDomain(prtos, listOf(wine), listOf(deity)).toWire(prtos, listOf(wine), listOf(deity))

        roundTripped shouldBe entries
    }

    test("toWire writes -1 for null cross-references") {
        val entry = ruleEntry(defaultUnits = ruleDefaultUnits(flagUnitType = null), defaultMoneyResource = -1, defaultDifficultyLevel = -1)

        val rule = listOf(entry).toDomain(emptyList(), emptyList(), emptyList()).single()
        val wire = listOf(rule).toWire(emptyList(), emptyList(), emptyList()).single()

        wire.defaultUnits.advancedBarbarianUnitType shouldBe -1
        wire.defaultUnits.flagUnitType shouldBe null
        wire.defaultMoneyResource shouldBe -1
        wire.defaultDifficultyLevel shouldBe -1
    }

    test("toWire throws on a dangling DefaultUnits/defaultMoneyResource/defaultDifficulty reference") {
        val prtos = (0..8).map { prto("Unit$it") }
        val wine = resource("Wine")
        val deity = difficulty("Deity")
        val rule = listOf(ruleEntry(defaultMoneyResource = 0, defaultDifficultyLevel = 0))
            .toDomain(prtos, listOf(wine), listOf(deity)).single()

        val outsiderPrto = prto("Outsider")
        val withDanglingUnit = rule.copy(defaultUnits = rule.defaultUnits.copy(scout = outsiderPrto))
        shouldThrow<IllegalArgumentException> { listOf(withDanglingUnit).toWire(prtos, listOf(wine), listOf(deity)) }

        val withDanglingMoney = rule.copy(defaultMoneyResource = resource("Outsider"))
        shouldThrow<IllegalArgumentException> { listOf(withDanglingMoney).toWire(prtos, listOf(wine), listOf(deity)) }

        val withDanglingDifficulty = rule.copy(defaultDifficulty = difficulty("Outsider"))
        shouldThrow<IllegalArgumentException> { listOf(withDanglingDifficulty).toWire(prtos, listOf(wine), listOf(deity)) }
    }
})
