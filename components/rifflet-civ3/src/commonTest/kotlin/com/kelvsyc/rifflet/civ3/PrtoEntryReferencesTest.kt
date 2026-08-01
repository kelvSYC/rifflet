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
    availableTo: Int = 0,
): PrtoEntry = PrtoEntry(
    unitStatistics = PrtoUnitStatistics(
        zoneOfControl = 0, bombardStrength = 0, bombardRange = 0, capacity = 0, shieldCost = 0,
        defense = 0, attack = 0, operationalRange = 0, populationCost = 0, rateOfFire = 0,
        movement = 0, upgradeTo = upgradeTo, hpBonus = 0, bombardEffects = 0, requireSupport = 0,
        createCraters = 0, workerStrength = 0f, airDefense = 0,
    ),
    name = "",
    civilopediaEntry = "",
    iconIndex = 0,
    required = required,
    requiredResource1 = requiredResource1,
    requiredResource2 = requiredResource2,
    requiredResource3 = requiredResource3,
    abilities = 0,
    aiStrategies = aiStrategies,
    availableTo = availableTo,
    flags2 = ByteString.of(*ByteArray(8)),
    type = 0,
    otherStrategy = otherStrategy,
    standardOrders = 0,
    specialActions = 0,
    workerActions = 0,
    airMissions = 0,
    flags4 = ByteString.of(*ByteArray(4)),
    ignoreMovementCost = ByteString.of(),
    unknown = ByteString.of(*ByteArray(16)),
    enslaveResults = 0,
    unknown2 = ByteString.of(0, 0, 0, 0),
    stealthTargetUnitTypes = stealthTargetUnitTypes,
    unknown3 = ByteString.of(*ByteArray(8)),
    unknown4 = ByteString.of(0, 0, 0, 0),
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
    type = GoodResourceType.BONUS,
    appearanceRatio = 0,
    disappearanceProbability = 0,
    icon = 0,
    prerequisite = 0,
    foodBonus = 0,
    shieldsBonus = 0,
    commerceBonus = 0,
)

private fun validRaceEntry(name: String): RaceEntry = RaceEntry(
    cityNames = emptyList(),
    greatLeaderNames = emptyList(),
    leader = RaceLeader(name = "", title = "", gender = 0),
    civilopediaEntry = "",
    adjective = "",
    name = name,
    noun = "",
    eras = emptyList(),
    cultureGroup = -1,
    civilizationGender = 0,
    personality = RacePersonality(favoriteGovernment = -1, shunnedGovernment = -1, aggressionLevel = 0),
    uniqueCivilizationCounter = 0,
    defaultColor = 0,
    uniqueColor = 0,
    freeTechs = listOf(-1, -1, -1, -1),
    bonuses = 0,
    governor = RaceGovernor(settings = 0, buildNever = 0, buildOften = 0),
    plurality = 1,
    unitTypeForKing = -1,
    flavors = 0,
    unknown = ByteString.of(0, 0, 0, 0),
    diplomacyTextIndex = 0,
    scientificLeaderNames = emptyList(),
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

    test("availableToRaces resolves the bitmask into RACE entries, matching Caravel/Carrack exclusivity") {
        val barbarians = validRaceEntry("A Barbarian Chiefdom")
        val rome = validRaceEntry("Rome")
        val portugal = validRaceEntry("Portugal")
        val races = listOf(barbarians, rome, portugal)

        val caravel = validPrtoEntry(availableTo = (1 shl 1))
        val carrack = validPrtoEntry(availableTo = (1 shl 2))

        caravel.availableToRaces(races) shouldBe listOf(rome)
        carrack.availableToRaces(races) shouldBe listOf(portugal)
    }

    test("availableToRaces returns no entries for a zero bitmask") {
        val races = listOf(validRaceEntry("Rome"))
        validPrtoEntry(availableTo = 0).availableToRaces(races) shouldBe emptyList()
    }
})
