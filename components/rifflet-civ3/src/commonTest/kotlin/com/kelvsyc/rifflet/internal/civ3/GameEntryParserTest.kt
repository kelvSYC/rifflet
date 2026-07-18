package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.GameEntry
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer
import okio.ByteString

/** Writes [text] into [fieldSize] bytes, null-padding the remainder. */
private fun Buffer.writePaddedField(text: String, fieldSize: Int) {
    val start = size
    writeString(text, Charsets.US_ASCII)
    write(ByteArray((fieldSize - (size - start)).toInt()))
}

/**
 * Builds a well-formed GAME item body (length prefix excluded, as with prior sections). Uses
 * numberOfPlayableCivs = 2 (a small, non-zero count) to prove playableCivIds and
 * civAllianceStatuses — read from two independent locations in the layout, sharing the same
 * size — are both genuinely dynamic, not hardcoded.
 *
 * [includeMpTimingFields] controls whether the last 3 fields (`mpBasetime`/`mpCityTime`/
 * `mpUnitTime`) are written, matching the real Conquests `minor=6` (absent) vs `minor=7`/`8`
 * (present) split confirmed against real files.
 */
private fun gameItemBinary(
    defaultGameRules: Int = 1,
    defaultVictoryConditions: Int = 1,
    numberOfPlayableCivs: Int = 2,
    playableCivIds: List<Int> = listOf(3, 7),
    flags: ByteString = ByteString.of(*ByteArray(4)),
    placeCaptureUnits: Int = 0,
    autoPlaceKings: Int = 0,
    autoPlaceVictoryLocations: Int = 0,
    debugMode: Int = 0,
    useTimeLimit: Int = 0,
    baseTimeUnit: Int = 0,
    startMonth: Int = 1,
    startWeek: Int = 1,
    startYear: Int = -4000,
    minuteTimeLimit: Int = 0,
    turnTimeLimit: Int = 0,
    timescaleNumberOfTurns: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7),
    turnNumberOfTimeUnits: List<Int> = listOf(10, 20, 30, 40, 50, 60, 70),
    scenarioSearchFolders: String = "Conquests",
    civAllianceStatuses: List<Int> = listOf(0, 2),
    victoryPointLimit: Int = 0,
    cityEliminationCount: Int = 0,
    oneCityCultureWin: Int = 0,
    allCitiesCultureWin: Int = 0,
    dominationTerrain: Int = 0,
    dominationPopulation: Int = 0,
    wonderCost: Int = 0,
    defeatingOpposingUnitCost: Int = 0,
    advancementCost: Int = 0,
    cityConquestPopulation: Int = 0,
    victoryPointScoring: Int = 0,
    capturingSpecialUnit: Int = 0,
    unknown: ByteString = ByteString.of(*ByteArray(5)),
    allianceNames: List<String> = listOf("", "Alliance A", "Alliance B", "Alliance C", "Alliance D"),
    allianceWars: List<Int> = List(25) { 0 },
    allianceVictoryType: Int = 0,
    plagueName: String = "Plague",
    permitPlagues: Byte = 0,
    plagueEarliestStart: Int = 0,
    plagueVariation: Int = 0,
    plagueDuration: Int = 0,
    plagueStrength: Int = 0,
    plagueGracePeriod: Int = 0,
    plagueMaxOccurrence: Int = 0,
    unknown2: ByteString = ByteString.of(*ByteArray(264)),
    respawnFlagUnits: Int = 0,
    captureAnyFlag: Byte = 0,
    goldForCapture: Int = 0,
    mapVisible: Byte = 1,
    retainCulture: Byte = 0,
    unknown3: ByteString = ByteString.of(*ByteArray(4)),
    eruptionPeriod: Int = 0,
    mpBasetime: Int = 0,
    mpCityTime: Int = 0,
    mpUnitTime: Int = 0,
    includeMpTimingFields: Boolean = true,
): Buffer = Buffer().apply {
    writeIntLe(defaultGameRules)
    writeIntLe(defaultVictoryConditions)
    writeIntLe(numberOfPlayableCivs)
    playableCivIds.forEach { writeIntLe(it) }
    write(flags)
    writeIntLe(placeCaptureUnits)
    writeIntLe(autoPlaceKings)
    writeIntLe(autoPlaceVictoryLocations)
    writeIntLe(debugMode)
    writeIntLe(useTimeLimit)
    writeIntLe(baseTimeUnit)
    writeIntLe(startMonth)
    writeIntLe(startWeek)
    writeIntLe(startYear)
    writeIntLe(minuteTimeLimit)
    writeIntLe(turnTimeLimit)
    timescaleNumberOfTurns.forEach { writeIntLe(it) }
    turnNumberOfTimeUnits.forEach { writeIntLe(it) }
    writePaddedField(scenarioSearchFolders, 5200)
    civAllianceStatuses.forEach { writeIntLe(it) }
    writeIntLe(victoryPointLimit)
    writeIntLe(cityEliminationCount)
    writeIntLe(oneCityCultureWin)
    writeIntLe(allCitiesCultureWin)
    writeIntLe(dominationTerrain)
    writeIntLe(dominationPopulation)
    writeIntLe(wonderCost)
    writeIntLe(defeatingOpposingUnitCost)
    writeIntLe(advancementCost)
    writeIntLe(cityConquestPopulation)
    writeIntLe(victoryPointScoring)
    writeIntLe(capturingSpecialUnit)
    write(unknown)
    allianceNames.forEach { writePaddedField(it, 256) }
    allianceWars.forEach { writeIntLe(it) }
    writeIntLe(allianceVictoryType)
    writePaddedField(plagueName, 260)
    writeByte(permitPlagues.toInt())
    writeIntLe(plagueEarliestStart)
    writeIntLe(plagueVariation)
    writeIntLe(plagueDuration)
    writeIntLe(plagueStrength)
    writeIntLe(plagueGracePeriod)
    writeIntLe(plagueMaxOccurrence)
    write(unknown2)
    writeIntLe(respawnFlagUnits)
    writeByte(captureAnyFlag.toInt())
    writeIntLe(goldForCapture)
    writeByte(mapVisible.toInt())
    writeByte(retainCulture.toInt())
    write(unknown3)
    writeIntLe(eruptionPeriod)
    if (includeMpTimingFields) {
        writeIntLe(mpBasetime)
        writeIntLe(mpCityTime)
        writeIntLe(mpUnitTime)
    }
}

class GameEntryParserTest : FunSpec({

    test("well-formed item is parsed into all fields, including both civ-count-sized lists") {
        val entry = GameEntryParser.parse(gameItemBinary())
        entry shouldBe GameEntry(
            defaultGameRules = 1,
            defaultVictoryConditions = 1,
            numberOfPlayableCivs = 2,
            playableCivIds = listOf(3, 7),
            flags = ByteString.of(*ByteArray(4)),
            placeCaptureUnits = 0,
            autoPlaceKings = 0,
            autoPlaceVictoryLocations = 0,
            debugMode = 0,
            useTimeLimit = 0,
            baseTimeUnit = 0,
            startMonth = 1,
            startWeek = 1,
            startYear = -4000,
            minuteTimeLimit = 0,
            turnTimeLimit = 0,
            timescaleNumberOfTurns = listOf(1, 2, 3, 4, 5, 6, 7),
            turnNumberOfTimeUnits = listOf(10, 20, 30, 40, 50, 60, 70),
            scenarioSearchFolders = "Conquests",
            civAllianceStatuses = listOf(0, 2),
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
            unknown = ByteString.of(*ByteArray(5)),
            allianceNames = listOf("", "Alliance A", "Alliance B", "Alliance C", "Alliance D"),
            allianceWars = List(25) { 0 },
            allianceVictoryType = 0,
            plagueName = "Plague",
            permitPlagues = 0,
            plagueEarliestStart = 0,
            plagueVariation = 0,
            plagueDuration = 0,
            plagueStrength = 0,
            plagueGracePeriod = 0,
            plagueMaxOccurrence = 0,
            unknown2 = ByteString.of(*ByteArray(264)),
            respawnFlagUnits = 0,
            captureAnyFlag = 0,
            goldForCapture = 0,
            mapVisible = 1,
            retainCulture = 0,
            unknown3 = ByteString.of(*ByteArray(4)),
            eruptionPeriod = 0,
            mpBasetime = 0,
            mpCityTime = 0,
            mpUnitTime = 0,
        )
    }

    test("well-formed item with numberOfPlayableCivs = 0 parses both civ-count-sized lists as empty") {
        val entry = GameEntryParser.parse(
            gameItemBinary(numberOfPlayableCivs = 0, playableCivIds = emptyList(), civAllianceStatuses = emptyList()),
        )
        entry.numberOfPlayableCivs shouldBe 0
        entry.playableCivIds shouldBe emptyList()
        entry.civAllianceStatuses shouldBe emptyList()
    }

    test("item with mp timing fields absent (confirmed real Conquests minor=6 shape) defaults them to zero") {
        val entry = GameEntryParser.parse(gameItemBinary(includeMpTimingFields = false))
        entry.mpBasetime shouldBe 0
        entry.mpCityTime shouldBe 0
        entry.mpUnitTime shouldBe 0
    }

    test("GameEntry rejects a playableCivIds size that doesn't match numberOfPlayableCivs") {
        shouldThrow<IllegalArgumentException> {
            wellFormedGameEntry(numberOfPlayableCivs = 2, playableCivIds = listOf(1))
        }
    }

    test("GameEntry rejects a civAllianceStatuses size that doesn't match numberOfPlayableCivs") {
        shouldThrow<IllegalArgumentException> {
            wellFormedGameEntry(
                numberOfPlayableCivs = 2,
                playableCivIds = listOf(1, 2),
                civAllianceStatuses = listOf(1),
            )
        }
    }

    test("GameEntry rejects a flags field that is not exactly 4 bytes") {
        shouldThrow<IllegalArgumentException> {
            wellFormedGameEntry(flags = ByteString.of(0, 0))
        }
    }

    test("GameEntry rejects a timescaleNumberOfTurns that does not have exactly 7 elements") {
        shouldThrow<IllegalArgumentException> {
            wellFormedGameEntry(timescaleNumberOfTurns = listOf(1, 2, 3))
        }
    }

    test("GameEntry rejects a turnNumberOfTimeUnits that does not have exactly 7 elements") {
        shouldThrow<IllegalArgumentException> {
            wellFormedGameEntry(turnNumberOfTimeUnits = listOf(1, 2, 3))
        }
    }

    test("GameEntry rejects an unknown field that is not exactly 5 bytes") {
        shouldThrow<IllegalArgumentException> {
            wellFormedGameEntry(unknown = ByteString.of(0, 0))
        }
    }

    test("GameEntry rejects an allianceNames that does not have exactly 5 elements") {
        shouldThrow<IllegalArgumentException> {
            wellFormedGameEntry(allianceNames = listOf("a", "b"))
        }
    }

    test("GameEntry rejects an allianceWars that does not have exactly 25 elements") {
        shouldThrow<IllegalArgumentException> {
            wellFormedGameEntry(allianceWars = listOf(1, 2, 3))
        }
    }

    test("GameEntry rejects an unknown2 field that is not exactly 264 bytes") {
        shouldThrow<IllegalArgumentException> {
            wellFormedGameEntry(unknown2 = ByteString.of(0, 0))
        }
    }

    test("GameEntry rejects an unknown3 field that is not exactly 4 bytes") {
        shouldThrow<IllegalArgumentException> {
            wellFormedGameEntry(unknown3 = ByteString.of(0, 0))
        }
    }
})

/** Builds a well-formed [GameEntry] with all-zero/empty values, for domain-invariant tests that
 * only care about overriding one field. */
private fun wellFormedGameEntry(
    numberOfPlayableCivs: Int = 0,
    playableCivIds: List<Int> = emptyList(),
    flags: ByteString = ByteString.of(*ByteArray(4)),
    timescaleNumberOfTurns: List<Int> = List(7) { 0 },
    turnNumberOfTimeUnits: List<Int> = List(7) { 0 },
    civAllianceStatuses: List<Int> = emptyList(),
    unknown: ByteString = ByteString.of(*ByteArray(5)),
    allianceNames: List<String> = List(5) { "" },
    allianceWars: List<Int> = List(25) { 0 },
    unknown2: ByteString = ByteString.of(*ByteArray(264)),
    unknown3: ByteString = ByteString.of(*ByteArray(4)),
): GameEntry = GameEntry(
    defaultGameRules = 0, defaultVictoryConditions = 0,
    numberOfPlayableCivs = numberOfPlayableCivs,
    playableCivIds = playableCivIds,
    flags = flags,
    placeCaptureUnits = 0, autoPlaceKings = 0, autoPlaceVictoryLocations = 0, debugMode = 0,
    useTimeLimit = 0, baseTimeUnit = 0, startMonth = 0, startWeek = 0, startYear = 0,
    minuteTimeLimit = 0, turnTimeLimit = 0,
    timescaleNumberOfTurns = timescaleNumberOfTurns,
    turnNumberOfTimeUnits = turnNumberOfTimeUnits,
    scenarioSearchFolders = "",
    civAllianceStatuses = civAllianceStatuses,
    victoryPointLimit = 0, cityEliminationCount = 0, oneCityCultureWin = 0, allCitiesCultureWin = 0,
    dominationTerrain = 0, dominationPopulation = 0, wonderCost = 0, defeatingOpposingUnitCost = 0,
    advancementCost = 0, cityConquestPopulation = 0, victoryPointScoring = 0, capturingSpecialUnit = 0,
    unknown = unknown,
    allianceNames = allianceNames,
    allianceWars = allianceWars,
    allianceVictoryType = 0,
    plagueName = "", permitPlagues = 0, plagueEarliestStart = 0, plagueVariation = 0,
    plagueDuration = 0, plagueStrength = 0, plagueGracePeriod = 0, plagueMaxOccurrence = 0,
    unknown2 = unknown2,
    respawnFlagUnits = 0, captureAnyFlag = 0, goldForCapture = 0, mapVisible = 0, retainCulture = 0,
    unknown3 = unknown3,
    eruptionPeriod = 0, mpBasetime = 0, mpCityTime = 0, mpUnitTime = 0,
)
