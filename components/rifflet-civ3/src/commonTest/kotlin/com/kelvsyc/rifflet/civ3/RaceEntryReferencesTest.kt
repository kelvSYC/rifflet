package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validRaceEntry(
    freeTech1: Int = 0,
    freeTech2: Int = 0,
    freeTech3: Int = 0,
    freeTech4: Int = 0,
    unitTypeForKing: Int = 0,
    shunnedGovernment: Int = 0,
    favoriteGovernment: Int = 0,
): RaceEntry = RaceEntry(
    cityNames = emptyList(),
    greatLeaderNames = emptyList(),
    leaderName = "",
    leaderTitle = "",
    civilopediaEntry = "",
    adjective = "",
    name = "Rome",
    noun = "",
    eras = emptyList(),
    cultureGroup = 0,
    leaderGender = 0,
    civilizationGender = 0,
    aggressionLevel = 0,
    uniqueCivilizationCounter = 0,
    shunnedGovernment = shunnedGovernment,
    favoriteGovernment = favoriteGovernment,
    defaultColor = 0,
    uniqueColor = 0,
    freeTech1 = freeTech1,
    freeTech2 = freeTech2,
    freeTech3 = freeTech3,
    freeTech4 = freeTech4,
    bonuses = 0,
    governorSettings = 0,
    buildNever = 0,
    buildOften = 0,
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
    abilities = 0,
    aiStrategies = 0,
    availableTo = 0,
    flags2 = ByteString.of(*ByteArray(8)),
    type = 0,
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
    enslaveResults = 0,
    unknown2 = ByteString.of(0, 0, 0, 0),
    stealthTargetUnitTypes = emptyList(),
    unknown3 = ByteString.of(*ByteArray(8)),
    createCraters = 0,
    workerStrength = 0f,
    unknown4 = ByteString.of(0, 0, 0, 0),
    airDefense = 0,
)

private fun validGovtEntry(): GovtEntry = GovtEntry(
    defaultType = 0,
    transitionType = 0,
    requiresMaintenance = 0,
    toggle1 = 0,
    tilePenalty = 0,
    tradeBonus = 0,
    name = "",
    civilopediaEntry = "",
    maleRulerTitle1 = "",
    femaleRulerTitle1 = "",
    maleRulerTitle2 = "",
    femaleRulerTitle2 = "",
    maleRulerTitle3 = "",
    femaleRulerTitle3 = "",
    maleRulerTitle4 = "",
    femaleRulerTitle4 = "",
    corruption = 0,
    immuneTo = 0,
    diplomatsAre = 0,
    spiesAre = 0,
    relationships = emptyList(),
    hurrying = 0,
    assimilationChance = 0,
    draftLimit = 0,
    militaryPoliceLimit = 0,
    rulerTitlePairsUsed = 0,
    prerequisiteTechnology = 0,
    scienceRateCap = 0,
    workerRate = 0,
    toggle2 = 0,
    toggle3 = 0,
    unknown = ByteString.of(0, 0, 0, 0),
    freeUnits = 0,
    freeUnitsPerTown = 0,
    freeUnitsPerCity = 0,
    freeUnitsPerMetropolis = 0,
    unitCost = 0,
    warWeariness = 0,
    xenophobic = 0,
    forceResettle = 0,
)

class RaceEntryReferencesTest : FunSpec({

    test("freeTech1Tech resolves against the TECH list") {
        val tech = validTechEntry()
        validRaceEntry(freeTech1 = 0).freeTech1Tech(listOf(tech)) shouldBe tech
        validRaceEntry(freeTech1 = 5).freeTech1Tech(emptyList()) shouldBe null
    }

    test("freeTech2Tech resolves against the TECH list") {
        val tech = validTechEntry()
        validRaceEntry(freeTech2 = 0).freeTech2Tech(listOf(tech)) shouldBe tech
        validRaceEntry(freeTech2 = 5).freeTech2Tech(emptyList()) shouldBe null
    }

    test("freeTech3Tech resolves against the TECH list") {
        val tech = validTechEntry()
        validRaceEntry(freeTech3 = 0).freeTech3Tech(listOf(tech)) shouldBe tech
        validRaceEntry(freeTech3 = 5).freeTech3Tech(emptyList()) shouldBe null
    }

    test("freeTech4Tech resolves against the TECH list") {
        val tech = validTechEntry()
        validRaceEntry(freeTech4 = 0).freeTech4Tech(listOf(tech)) shouldBe tech
        validRaceEntry(freeTech4 = 5).freeTech4Tech(emptyList()) shouldBe null
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
