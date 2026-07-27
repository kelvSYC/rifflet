package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validGameEntry(playableCivIds: List<Int> = emptyList()): GameEntry = GameEntry(
    defaultGameRules = 0,
    defaultVictoryConditions = 0,
    numberOfPlayableCivs = playableCivIds.size,
    playableCivIds = playableCivIds,
    flags = ByteString.of(0, 0, 0, 0),
    placeCaptureUnits = 0,
    autoPlaceKings = 0,
    autoPlaceVictoryLocations = 0,
    debugMode = 0,
    timeOptions = GameTimeOptions(
        useTimeLimit = 0,
        baseTimeUnit = 0,
        startMonth = 0,
        startWeek = 0,
        startYear = 0,
        minuteTimeLimit = 0,
        turnTimeLimit = 0,
        timescaleNumberOfTurns = List(7) { 0 },
        turnNumberOfTimeUnits = List(7) { 0 },
    ),
    scenarioSearchFolders = "",
    civAllianceStatuses = playableCivIds.map { 0 },
    victoryPointLimits = GameVictoryPointLimits(
        victoryPointLimit = 0,
        cityEliminationCount = 0,
        oneCityCultureWin = 0,
        allCitiesCultureWin = 0,
        dominationTerrain = 0,
        dominationPopulation = 0,
        wonderCost = 0,
        defeatingOpposingUnitCost = 0,
        advancementCost = 0,
        cityConquestPopulation = 0,
        victoryPointScoring = 0,
        capturingSpecialUnit = 0,
        respawnFlagUnits = 0,
        captureAnyFlag = 0.toByte(),
        goldForCapture = 0,
    ),
    unknown = ByteString.of(0, 0, 0, 0, 0),
    lockedAlliance = GameLockedAlliance(
        allianceNames = List(5) { "" },
        allianceWars = List(25) { 0 },
        allianceVictoryType = 0,
    ),
    plagueSettings = GamePlagueSettings(
        plagueName = "",
        permitPlagues = 0.toByte(),
        plagueEarliestStart = 0,
        plagueVariation = 0,
        plagueDuration = 0,
        plagueStrength = 0,
        plagueGracePeriod = 0,
        plagueMaxOccurrence = 0,
    ),
    unknown2 = ByteString.of(*ByteArray(264)),
    mapVisible = 0.toByte(),
    retainCulture = 0.toByte(),
    unknown3 = ByteString.of(0, 0, 0, 0),
    eruptionPeriod = 0,
    mpTimers = GameMpTimers(
        mpBasetime = 0,
        mpCityTime = 0,
        mpUnitTime = 0,
    ),
)

private fun validRaceEntry(): RaceEntry = RaceEntry(
    cityNames = emptyList(),
    greatLeaderNames = emptyList(),
    leader = RaceLeader(name = "", title = "", gender = 0),
    civilopediaEntry = "",
    adjective = "",
    name = "Rome",
    noun = "",
    eras = emptyList(),
    cultureGroup = 0,
    civilizationGender = 0,
    personality = RacePersonality(favoriteGovernment = 0, shunnedGovernment = 0, aggressionLevel = 0),
    uniqueCivilizationCounter = 0,
    defaultColor = 0,
    uniqueColor = 0,
    freeTechs = listOf(0, 0, 0, 0),
    bonuses = 0,
    governor = RaceGovernor(settings = 0, buildNever = 0, buildOften = 0),
    plurality = 0,
    unitTypeForKing = 0,
    flavors = 0,
    unknown = ByteString.of(0, 0, 0, 0),
    diplomacyTextIndex = 0,
    scientificLeaderNames = emptyList(),
)

class GameEntryReferencesTest : FunSpec({

    test("playableCivIdsRace resolves each id, preserving position and length") {
        val race = validRaceEntry()
        val entry = validGameEntry(playableCivIds = listOf(0, 5))
        entry.playableCivIdsRace(listOf(race)) shouldBe listOf(race, null)
    }
})
