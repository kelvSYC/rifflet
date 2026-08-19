package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.AllianceSlot
import com.kelvsyc.rifflet.civ3.Gender
import com.kelvsyc.rifflet.civ3.GameBaseTimeUnit
import com.kelvsyc.rifflet.civ3.GameEntry
import com.kelvsyc.rifflet.civ3.GameLockedAlliance
import com.kelvsyc.rifflet.civ3.GameMpTimers
import com.kelvsyc.rifflet.civ3.GamePlagueSettings
import com.kelvsyc.rifflet.civ3.GameTimeOptions
import com.kelvsyc.rifflet.civ3.GameVictoryPointLimits
import com.kelvsyc.rifflet.civ3.RaceCultureGroup
import com.kelvsyc.rifflet.civ3.RaceLeader
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun race(name: String): Race = Race(
    name = name, civilopediaEntry = "", adjective = "", noun = "",
    leader = RaceLeader(name = "", title = "", gender = Gender.MALE),
    cultureGroup = RaceCultureGroup.NONE, civilizationGender = Gender.MALE,
)

private fun gameLockedAlliance(
    allianceNames: List<String> = List(5) { "" },
    allianceWars: List<Int> = List(25) { 0 },
    allianceVictoryType: Int = 0,
): GameLockedAlliance = GameLockedAlliance(
    allianceNames = allianceNames,
    allianceWars = allianceWars,
    allianceVictoryType = allianceVictoryType,
)

private fun gameEntry(
    playableCivIds: List<Int> = emptyList(),
    civAllianceStatuses: List<Int> = emptyList(),
    lockedAlliance: GameLockedAlliance? = null,
    flags: ByteString = ByteString.of(0, 0, 0, 0),
): GameEntry = GameEntry(
    defaultGameRules = 1,
    defaultVictoryConditions = 0,
    numberOfPlayableCivs = playableCivIds.size,
    playableCivIds = playableCivIds,
    flags = flags,
    placeCaptureUnits = 1,
    autoPlaceKings = 0,
    autoPlaceVictoryLocations = 1,
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
    scenarioSearchFolders = "Search",
    civAllianceStatuses = civAllianceStatuses,
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
    unknown = ByteString.of(*ByteArray(5)),
    lockedAlliance = lockedAlliance,
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
    mapVisible = 1.toByte(),
    retainCulture = 0.toByte(),
    unknown3 = ByteString.of(*ByteArray(4)),
    eruptionPeriod = 5,
    mpTimers = GameMpTimers(mpBasetime = 0, mpCityTime = 0, mpUnitTime = 0),
)

class GameEntryMappingTest : FunSpec({

    test("toDomain maps scalar fields, booleans, and reused sub-types straight across") {
        val entry = gameEntry()

        val game = listOf(entry).toDomain(emptyList()).single()

        game.defaultGameRules shouldBe true
        game.defaultVictoryConditions shouldBe false
        game.placeCaptureUnits shouldBe true
        game.autoPlaceKings shouldBe false
        game.autoPlaceVictoryLocations shouldBe true
        game.debugMode shouldBe false
        game.mapVisible shouldBe true
        game.retainCulture shouldBe false
        game.scenarioSearchFolders shouldBe "Search"
        game.eruptionPeriod shouldBe 5
        game.timeOptions shouldBe entry.timeOptions
        game.victoryPointLimits shouldBe entry.victoryPointLimits
        game.plagueSettings shouldBe entry.plagueSettings
        game.mpTimers shouldBe entry.mpTimers
        game.unknown shouldBe entry.unknown
        game.unknown2 shouldBe entry.unknown2
        game.unknown3 shouldBe entry.unknown3
        game.lockedAlliance shouldBe null
    }

    test("toDomain converts flags from little-endian ByteString to Int") {
        val entry = gameEntry(flags = ByteString.of(0x01, 0x00, 0x01, 0x00))

        val game = listOf(entry).toDomain(emptyList()).single()

        game.flags shouldBe (1 or (1 shl 16))
    }

    test("toDomain resolves playableCivs positionally, null for a dangling index") {
        val races = listOf(race("Rome"), race("Greece"))
        val entry = gameEntry(playableCivIds = listOf(1, 0, 99), civAllianceStatuses = listOf(0, 0, 0))

        val game = listOf(entry).toDomain(races).single()

        game.playableCivs shouldBe mutableListOf(races[1], races[0], null)
        game.allianceStatuses shouldBe mutableListOf(0, 0, 0)
    }

    test("toDomain resolves lockedAlliance into AllianceSlot-keyed identities and relations") {
        val allianceNames = listOf("", "Alliance 1", "Alliance 2", "Alliance 3", "Alliance 4")
        val allianceWars = List(25) { i -> if (i == 1 * 5 + 2 || i == 2 * 5 + 1) 1 else 0 }
        val entry = gameEntry(lockedAlliance = gameLockedAlliance(allianceNames = allianceNames, allianceWars = allianceWars))

        val game = listOf(entry).toDomain(emptyList()).single()

        val locked = game.lockedAlliance!!
        locked.alliances.getValue(AllianceSlot.NONE).name shouldBe ""
        locked.alliances.getValue(AllianceSlot.ALLIANCE_1).name shouldBe "Alliance 1"
        locked.alliances.getValue(AllianceSlot.ALLIANCE_4).name shouldBe "Alliance 4"
        locked.relations[AllianceSlot.ALLIANCE_1, AllianceSlot.ALLIANCE_2] shouldBe 1
        locked.relations[AllianceSlot.ALLIANCE_2, AllianceSlot.ALLIANCE_1] shouldBe 1
        locked.relations[AllianceSlot.ALLIANCE_1, AllianceSlot.ALLIANCE_3] shouldBe 0
        locked.victoryType shouldBe AllianceVictoryType.INDIVIDUAL
    }

    test("toDomain resolves allianceVictoryType 1 to COALITION") {
        val entry = gameEntry(lockedAlliance = gameLockedAlliance(allianceVictoryType = 1))

        val game = listOf(entry).toDomain(emptyList()).single()

        game.lockedAlliance!!.victoryType shouldBe AllianceVictoryType.COALITION
    }

    test("toDomain throws on an out-of-range allianceVictoryType") {
        val entry = gameEntry(lockedAlliance = gameLockedAlliance(allianceVictoryType = 2))

        shouldThrow<IllegalArgumentException> { listOf(entry).toDomain(emptyList()) }
    }

    test("toDomain().toWire() round-trips, including a populated lockedAlliance") {
        val races = listOf(race("Rome"), race("Greece"))
        val allianceNames = listOf("", "Alliance 1", "Alliance 2", "Alliance 3", "Alliance 4")
        val allianceWars = List(25) { i -> if (i == 1 * 5 + 2) 1 else 0 }
        val entries = listOf(
            gameEntry(
                playableCivIds = listOf(0, 1),
                civAllianceStatuses = listOf(0, 1),
                lockedAlliance = gameLockedAlliance(allianceNames = allianceNames, allianceWars = allianceWars, allianceVictoryType = 1),
                flags = ByteString.of(0x01, 0x00, 0x00, 0x00),
            ),
        )

        val roundTripped = entries.toDomain(races).toWire(races)

        roundTripped shouldBe entries
    }

    test("toWire throws when playableCivs and allianceStatuses sizes differ") {
        val races = listOf(race("Rome"))
        val game = listOf(gameEntry(playableCivIds = listOf(0), civAllianceStatuses = listOf(0)))
            .toDomain(races).single()

        val mismatched = game.copy(allianceStatuses = mutableListOf(0, 0))

        shouldThrow<IllegalArgumentException> { listOf(mismatched).toWire(races) }
    }

    test("toWire writes -1 for a null playableCivs entry and throws on a dangling Race") {
        val races = listOf(race("Rome"))
        val game = listOf(gameEntry(playableCivIds = listOf(0), civAllianceStatuses = listOf(0)))
            .toDomain(races).single()

        val withNull = game.copy(playableCivs = mutableListOf(null), allianceStatuses = mutableListOf(0))
        val wire = listOf(withNull).toWire(races).single()
        wire.playableCivIds shouldBe listOf(-1)

        val withDangling = game.copy(playableCivs = mutableListOf(race("Outsider")), allianceStatuses = mutableListOf(0))
        shouldThrow<IllegalArgumentException> { listOf(withDangling).toWire(races) }
    }

    test("toWire throws if LockedAlliance.alliances isn't keyed by exactly the 5 AllianceSlot values") {
        val entry = gameEntry(lockedAlliance = gameLockedAlliance())
        val game = listOf(entry).toDomain(emptyList()).single()
        game.lockedAlliance!!.alliances.remove(AllianceSlot.ALLIANCE_4)

        shouldThrow<IllegalArgumentException> { listOf(game).toWire(emptyList()) }
    }

    test("toWire throws if LockedAlliance.relations is missing any (from, to) pair") {
        val game = Game(lockedAlliance = LockedAlliance(alliances = AllianceSlot.entries.associateWith { Alliance(name = "") }.toMutableMap()))

        shouldThrow<IllegalArgumentException> { listOf(game).toWire(emptyList()) }
    }
})
