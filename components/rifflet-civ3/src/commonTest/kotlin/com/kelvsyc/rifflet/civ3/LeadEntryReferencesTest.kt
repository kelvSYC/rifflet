package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validLeadEntry(
    startingTechnologyIds: List<Int> = emptyList(),
    government: Int = 0,
    civ: Int = 0,
): LeadEntry = LeadEntry(
    customCivData = 0,
    humanPlayer = 0,
    name = "",
    unknown = ByteString.of(*ByteArray(8)),
    startUnits = emptyList(),
    genderOfLeaderName = 0,
    startingTechnologyIds = startingTechnologyIds,
    difficulty = 0,
    initialEra = 0,
    startCash = 0,
    government = government,
    civ = civ,
    color = 0,
    skipFirstTurn = 0,
    unknown2 = ByteString.of(0, 0, 0, 0),
    startEmbassies = 0,
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

private fun validGovtEntry(): GovtEntry = GovtEntry(
    defaultType = 0,
    transitionType = 0,
    requiresMaintenance = 0,
    toggle1 = 0,
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
    unitSupportCosts = GovtUnitSupportCosts(
        freeUnits = 0,
        freeUnitsPerTown = 0,
        freeUnitsPerCity = 0,
        freeUnitsPerMetropolis = 0,
        unitCost = 0,
    ),
    warWeariness = 0,
    xenophobic = 0,
    forceResettle = 0,
)

private fun validRaceEntry(): RaceEntry = RaceEntry(
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
    shunnedGovernment = 0,
    favoriteGovernment = 0,
    defaultColor = 0,
    uniqueColor = 0,
    freeTech1 = 0,
    freeTech2 = 0,
    freeTech3 = 0,
    freeTech4 = 0,
    bonuses = 0,
    governorSettings = 0,
    buildNever = 0,
    buildOften = 0,
    plurality = 0,
    unitTypeForKing = 0,
    flavors = 0,
    unknown = ByteString.of(0, 0, 0, 0),
    diplomacyTextIndex = 0,
    scientificLeaderNames = emptyList(),
)

class LeadEntryReferencesTest : FunSpec({

    test("startingTechnologyIdsTech resolves each id, preserving position and length") {
        val tech = validTechEntry()
        val entry = validLeadEntry(startingTechnologyIds = listOf(0, 5))
        entry.startingTechnologyIdsTech(listOf(tech)) shouldBe listOf(tech, null)
    }

    test("governmentGovt resolves against the GOVT list") {
        val govt = validGovtEntry()
        validLeadEntry(government = 0).governmentGovt(listOf(govt)) shouldBe govt
        validLeadEntry(government = 5).governmentGovt(emptyList()) shouldBe null
    }

    test("civRace resolves a resolving index against the RACE list") {
        val race = validRaceEntry()
        validLeadEntry(civ = 0).civRace(listOf(race)) shouldBe race
    }

    test("civRace returns null for the -2 (random) sentinel without special-casing it") {
        validLeadEntry(civ = -2).civRace(listOf(validRaceEntry())) shouldBe null
    }

    test("civRace returns null for the -3 (any) sentinel without special-casing it") {
        validLeadEntry(civ = -3).civRace(listOf(validRaceEntry())) shouldBe null
    }
})
