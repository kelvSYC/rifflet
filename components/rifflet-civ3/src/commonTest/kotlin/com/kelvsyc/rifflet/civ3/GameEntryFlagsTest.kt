package com.kelvsyc.rifflet.civ3

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validGameEntry(
    flags: ByteString = ByteString.of(0, 0, 0, 0),
    allianceWars: List<Int> = List(25) { 0 },
): GameEntry = GameEntry(
    defaultGameRules = 0,
    defaultVictoryConditions = 0,
    numberOfPlayableCivs = 0,
    playableCivIds = emptyList(),
    flags = flags,
    placeCaptureUnits = 0,
    autoPlaceKings = 0,
    autoPlaceVictoryLocations = 0,
    debugMode = 0,
    timeOptions = GameTimeOptions(
        useTimeLimit = 0,
        baseTimeUnit = GameBaseTimeUnit.YEARS,
        startMonth = 0,
        startWeek = 0,
        startYear = 0,
        minuteTimeLimit = 0,
        turnTimeLimit = 0,
        timescaleNumberOfTurns = List(7) { 0 },
        turnNumberOfTimeUnits = List(7) { 0 },
    ),
    scenarioSearchFolders = "",
    civAllianceStatuses = emptyList(),
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
        allianceWars = allianceWars,
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

private fun flagsFor(value: Int): ByteString = ByteString.of(
    (value and 0xFF).toByte(),
    ((value shr 8) and 0xFF).toByte(),
    ((value shr 16) and 0xFF).toByte(),
    ((value shr 24) and 0xFF).toByte(),
)

class GameEntryFlagsTest : FunSpec({

    val properties: List<Pair<Int, (GameEntry) -> Boolean>> = listOf(
        0 to GameEntry::dominationVictoryEnabled,
        1 to GameEntry::spaceRaceVictoryEnabled,
        2 to GameEntry::diplomaticVictoryEnabled,
        3 to GameEntry::victoryByConquestEnabled,
        4 to GameEntry::culturalVictoryEnabled,
        5 to GameEntry::civSpecificAbilitiesEnabled,
        6 to GameEntry::culturallyLinkedStart,
        7 to GameEntry::restartPlayers,
        8 to GameEntry::preserveRandomSeed,
        9 to GameEntry::acceleratedProduction,
        10 to GameEntry::eliminationEnabled,
        11 to GameEntry::regicideEnabled,
        12 to GameEntry::massRegicideEnabled,
        13 to GameEntry::victoryPointScoringEnabled,
        14 to GameEntry::captureTheFlagEnabled,
        15 to GameEntry::allowCulturalConversions,
        16 to GameEntry::wonderVictoryEnabled,
    )

    test("each bit maps to exactly its own named property") {
        for ((bit, _) in properties) {
            val entry = validGameEntry(flags = flagsFor(1 shl bit))
            for ((otherBit, otherProperty) in properties) {
                otherProperty(entry) shouldBe (otherBit == bit)
            }
        }
    }

    test("all named bits set") {
        val allBits = properties.fold(0) { acc, (bit, _) -> acc or (1 shl bit) }
        val entry = validGameEntry(flags = flagsFor(allBits))
        properties.forEach { (_, property) -> property(entry) shouldBe true }
    }

    test("all named bits clear") {
        val entry = validGameEntry(flags = flagsFor(0))
        properties.forEach { (_, property) -> property(entry) shouldBe false }
    }
})

class GameEntryAllianceWarMatrixTest : FunSpec({

    test("allianceWarMatrix chunks the flat list into 5 row-major rows of 5") {
        val flat = (0 until 25).toList()
        val entry = validGameEntry(allianceWars = flat)
        val matrix = entry.allianceWarMatrix()
        matrix shouldBe listOf(
            listOf(0, 1, 2, 3, 4),
            listOf(5, 6, 7, 8, 9),
            listOf(10, 11, 12, 13, 14),
            listOf(15, 16, 17, 18, 19),
            listOf(20, 21, 22, 23, 24),
        )
        for (i in 0 until 5) {
            for (j in 0 until 5) {
                matrix[i][j] shouldBe flat[i * 5 + j]
            }
        }
    }

    test("allianceWarMatrix row access throws for an out-of-range alliance index") {
        val entry = validGameEntry()
        val matrix = entry.allianceWarMatrix()
        shouldThrow<IndexOutOfBoundsException> { matrix[5][0] }
        shouldThrow<IndexOutOfBoundsException> { matrix[0][5] }
    }
})
