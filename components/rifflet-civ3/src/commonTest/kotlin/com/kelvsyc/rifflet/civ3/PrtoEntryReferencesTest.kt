package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validPrtoEntry(
    required: Int = 0,
    upgradeTo: Int = 0,
    requiredResource1: Int = 0,
    requiredResource2: Int = 0,
    requiredResource3: Int = 0,
    stealthTargetUnitTypes: List<Int> = emptyList(),
    otherStrategy: Int = 0,
    aiStrategies: Int = 0,
): PrtoEntry = PrtoEntry(
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
    required = required,
    upgradeTo = upgradeTo,
    requiredResource1 = requiredResource1,
    requiredResource2 = requiredResource2,
    requiredResource3 = requiredResource3,
    abilities = 0,
    aiStrategies = aiStrategies,
    availableTo = 0,
    flags2 = ByteString.of(*ByteArray(8)),
    type = 0,
    otherStrategy = otherStrategy,
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
    enslaveResults = 0,
    unknown2 = ByteString.of(0, 0, 0, 0),
    stealthTargetUnitTypes = stealthTargetUnitTypes,
    unknown3 = ByteString.of(*ByteArray(8)),
    createCraters = 0,
    workerStrength = 0f,
    unknown4 = ByteString.of(0, 0, 0, 0),
    airDefense = 0,
)

private fun validTechEntry(): TechEntry = TechEntry(
    name = "",
    civilopediaEntry = "",
    cost = 0,
    era = 0,
    advanceIcon = 0,
    x = 0,
    y = 0,
    prerequisite1 = 0,
    prerequisite2 = 0,
    prerequisite3 = 0,
    prerequisite4 = 0,
    flags = 0,
    flavors = 0,
    unknown = ByteString.of(0, 0, 0, 0),
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

class PrtoEntryReferencesTest : FunSpec({

    test("requiredTech resolves against the TECH list") {
        val tech = validTechEntry()
        validPrtoEntry(required = 0).requiredTech(listOf(tech)) shouldBe tech
        validPrtoEntry(required = 5).requiredTech(emptyList()) shouldBe null
    }

    test("upgradeToPrto resolves a self-reference") {
        val prto = validPrtoEntry()
        validPrtoEntry(upgradeTo = 0).upgradeToPrto(listOf(prto)) shouldBe prto
        validPrtoEntry(upgradeTo = 5).upgradeToPrto(emptyList()) shouldBe null
    }

    test("otherStrategyPrto resolves a self-reference, or null for the -1 sentinel") {
        val prto = validPrtoEntry()
        validPrtoEntry(otherStrategy = 0).otherStrategyPrto(listOf(prto)) shouldBe prto
        validPrtoEntry(otherStrategy = -1).otherStrategyPrto(listOf(prto)) shouldBe null
    }

    test("effectiveAiStrategies merges this entry's bits with its duplicate's, matching the real Rifleman split") {
        val canonical = validPrtoEntry(otherStrategy = -1, aiStrategies = 0b01)
        val duplicate = validPrtoEntry(otherStrategy = 0, aiStrategies = 0b10)
        val prtos = listOf(canonical, duplicate)
        canonical.effectiveAiStrategies(prtos) shouldBe 0b11
        duplicate.effectiveAiStrategies(prtos) shouldBe 0b11
    }

    test("effectiveAiStrategies is just this entry's own bits when there's no duplicate") {
        val entry = validPrtoEntry(otherStrategy = -1, aiStrategies = 0b01)
        entry.effectiveAiStrategies(listOf(entry)) shouldBe 0b01
    }

    test("requiredResource1Good resolves against the GOOD list") {
        val good = validGoodEntry()
        validPrtoEntry(requiredResource1 = 0).requiredResource1Good(listOf(good)) shouldBe good
        validPrtoEntry(requiredResource1 = 5).requiredResource1Good(emptyList()) shouldBe null
    }

    test("requiredResource2Good resolves against the GOOD list") {
        val good = validGoodEntry()
        validPrtoEntry(requiredResource2 = 0).requiredResource2Good(listOf(good)) shouldBe good
        validPrtoEntry(requiredResource2 = 5).requiredResource2Good(emptyList()) shouldBe null
    }

    test("requiredResource3Good resolves against the GOOD list") {
        val good = validGoodEntry()
        validPrtoEntry(requiredResource3 = 0).requiredResource3Good(listOf(good)) shouldBe good
        validPrtoEntry(requiredResource3 = 5).requiredResource3Good(emptyList()) shouldBe null
    }

    test("stealthTargetUnitTypesPrto resolves each id, preserving position and length") {
        val prto = validPrtoEntry()
        val entry = validPrtoEntry(stealthTargetUnitTypes = listOf(0, 5))
        entry.stealthTargetUnitTypesPrto(listOf(prto)) shouldBe listOf(prto, null)
    }
})
