package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validRaceEntry(
    freeTechs: List<Int> = listOf(0, 0, 0, 0),
    unitTypeForKing: Int = 0,
    shunnedGovernment: Int = 0,
    favoriteGovernment: Int = 0,
): RaceEntry = RaceEntry(
    cityNames = emptyList(),
    greatLeaderNames = emptyList(),
    leader = RaceLeader(name = "", title = "", gender = 0),
    civilopediaEntry = "",
    adjective = "",
    name = "Rome",
    noun = "",
    eras = emptyList(),
    cultureGroup = RaceCultureGroup.AMERICAN,
    civilizationGender = 0,
    personality = RacePersonality(
        favoriteGovernment = favoriteGovernment,
        shunnedGovernment = shunnedGovernment,
        aggressionLevel = 0,
    ),
    uniqueCivilizationCounter = 0,
    defaultColor = 0,
    uniqueColor = 0,
    freeTechs = freeTechs,
    bonuses = 0,
    governor = RaceGovernor(settings = 0, buildNever = 0, buildOften = 0),
    plurality = 0,
    unitTypeForKing = unitTypeForKing,
    flavors = 0,
    unknown = ByteString.of(0, 0, 0, 0),
    diplomacyTextIndex = 0,
    scientificLeaderNames = emptyList(),
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
    type = PrtoDomain.LAND,
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

private fun validGovtEntry(): GovtEntry = GovtEntry(
    defaultType = 0,
    transitionType = 0,
    requiresMaintenance = 0,
    toggle1 = ByteString.of(*ByteArray(4)),
    tilePenalty = 0,
    tradeBonus = 0,
    name = "",
    civilopediaEntry = "",
    rulerTitles = GovtRulerTitles(
        male1 = "", female1 = "",
        male2 = "", female2 = "",
        male3 = "", female3 = "",
        male4 = "", female4 = "",
    ),
    corruption = GovtCorruption.MINIMAL,
    immuneTo = 0,
    diplomatsAre = 0,
    spiesAre = 0,
    relationships = emptyList(),
    hurrying = GovtHurrying.CANNOT_HURRY,
    assimilationChance = 0,
    draftLimit = 0,
    militaryPoliceLimit = 0,
    rulerTitlePairsUsed = 0,
    prerequisiteTechnology = 0,
    scienceRateCap = 0,
    workerRate = 0,
    unknown = ByteString.of(*ByteArray(12)),
    unitSupportCosts = GovtUnitSupportCosts(
        freeUnits = 0,
        freeUnitsPerTown = 0,
        freeUnitsPerCity = 0,
        freeUnitsPerMetropolis = 0,
        unitCost = 0,
    ),
    warWeariness = GovtWarWeariness.NONE,
    xenophobic = 0,
    forceResettle = 0,
)

class RaceEntryReferencesTest : FunSpec({

    test("freeTechsTech resolves each slot against the TECH list, preserving position") {
        val tech = validTechEntry()
        val entry = validRaceEntry(freeTechs = listOf(0, 5, 0, 5))
        entry.freeTechsTech(listOf(tech)) shouldBe listOf(tech, null, tech, null)
    }

    test("unitTypeForKingPrto resolves against the PRTO list") {
        val prto = validPrtoEntry()
        validRaceEntry(unitTypeForKing = 0).unitTypeForKingPrto(listOf(prto)) shouldBe prto
        validRaceEntry(unitTypeForKing = 5).unitTypeForKingPrto(emptyList()) shouldBe null
    }

    test("shunnedGovernmentGovt resolves against the GOVT list") {
        val govt = validGovtEntry()
        validRaceEntry(shunnedGovernment = 0).shunnedGovernmentGovt(listOf(govt)) shouldBe govt
        validRaceEntry(shunnedGovernment = 5).shunnedGovernmentGovt(emptyList()) shouldBe null
    }

    test("favoriteGovernmentGovt resolves against the GOVT list") {
        val govt = validGovtEntry()
        validRaceEntry(favoriteGovernment = 0).favoriteGovernmentGovt(listOf(govt)) shouldBe govt
        validRaceEntry(favoriteGovernment = 5).favoriteGovernmentGovt(emptyList()) shouldBe null
    }
})
