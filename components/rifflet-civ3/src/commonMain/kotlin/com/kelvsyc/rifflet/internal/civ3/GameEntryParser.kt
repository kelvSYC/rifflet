package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.GameBaseTimeUnit
import com.kelvsyc.rifflet.civ3.GameEntry
import com.kelvsyc.rifflet.civ3.GameLockedAlliance
import com.kelvsyc.rifflet.civ3.GameMpTimers
import com.kelvsyc.rifflet.civ3.GamePlagueSettings
import com.kelvsyc.rifflet.civ3.GameTimeOptions
import com.kelvsyc.rifflet.civ3.GameVictoryPointLimits
import okio.Buffer
import okio.ByteString

/**
 * Parses one `GAME` item, per existing reverse-engineering documentation of the BIX/BIQ format,
 * cross-validated against a separate reverse-engineered reference implementation's struct. Reads
 * directly off `item`, a zero-copy-transferred [Buffer] already stripped of its own length
 * prefix by the generic section loop.
 *
 * [GameEntry.numberOfPlayableCivs] is read once and reused (not re-read) to size two separate
 * dynamic reads at different points in the layout: [GameEntry.playableCivIds] immediately after
 * it, and [GameEntry.civAllianceStatuses] much later, after [GameEntry.scenarioSearchFolders].
 * A 4-byte BIX-only `mapVisible (long)` field documented by existing reverse-engineering work
 * between those two gaps is deliberately NOT read here — see [GameEntry]'s class-level KDoc for
 * why.
 *
 * [GameEntry.numberOfPlayableCivs] is validated via [requireSaneCount] immediately after being
 * read, before it sizes [GameEntry.playableCivIds] or (much later) [GameEntry.civAllianceStatuses]
 * — see that function's KDoc for why.
 *
 * Every field/group after the fixed 5-field header ([GameEntry.defaultGameRules]/
 * [GameEntry.defaultVictoryConditions]/[GameEntry.numberOfPlayableCivs]/
 * [GameEntry.playableCivIds]/[GameEntry.flags]) is read defensively: [Civ3FormatEra.VANILLA]
 * files can end immediately after [GameEntry.flags] (a bare 16-byte item); [Civ3FormatEra.PTW]
 * files are the one confirmed case of genuine within-era `minor` sensitivity in this codebase —
 * they end at one of 3 different cutoff points depending on `minor`
 * ([GameEntry.autoPlaceVictoryLocations] for `minor=6/9/10`, [GameEntry.debugMode] for
 * `minor=13`, [GameEntry.scenarioSearchFolders] for the dominant `minor=18` tier);
 * [Civ3FormatEra.CONQUESTS] files always include everything through
 * [GameEntry.scenarioSearchFolders] and omit only the trailing mp-timing fields on `minor=6`.
 *
 * Every field that belongs to one of [GameEntry]'s 5 group types ([GameTimeOptions],
 * [GameVictoryPointLimits], [GameLockedAlliance], [GamePlagueSettings], [GameMpTimers]) keeps its
 * own individual defensive size check, exactly as before this codebase grouped these fields —
 * only the "absent" default changes, from a concrete zero/empty value to `null`. Each group's
 * object is assembled only once every one of its members has been read as non-`null`; if any
 * member is absent the whole group is `null`, discarding any earlier-read member's real value.
 * This narrows behavior only for a file that is truncated at a byte offset falling strictly
 * *inside* one of these groups — which existing reverse-engineering documentation and every real
 * file checked so far confirm never happens (groups are always either fully present or fully
 * absent) — so this is not a change in parsing behavior for any known real file.
 */
internal object GameEntryParser {
    fun parse(item: Buffer): GameEntry {
        val defaultGameRules = item.readIntLe()
        val defaultVictoryConditions = item.readIntLe()
        val numberOfPlayableCivs = item.requireSaneCount(item.readIntLe(), 4L, "GameEntry.playableCivIds")
        val playableCivIds = List(numberOfPlayableCivs) { item.readIntLe() }
        val flags = item.readByteString(4L)
        val placeCaptureUnits = if (item.size >= 4L) item.readIntLe() else 0
        val autoPlaceKings = if (item.size >= 4L) item.readIntLe() else 0
        val autoPlaceVictoryLocations = if (item.size >= 4L) item.readIntLe() else 0
        val debugMode = if (item.size >= 4L) item.readIntLe() else 0

        val useTimeLimit = if (item.size >= 4L) item.readIntLe() else null
        val baseTimeUnit = if (item.size >= 4L) {
            decodeEnum("GameTimeOptions.baseTimeUnit", item.readIntLe(), GameBaseTimeUnit.entries)
        } else {
            null
        }
        val startMonth = if (item.size >= 4L) item.readIntLe() else null
        val startWeek = if (item.size >= 4L) item.readIntLe() else null
        val startYear = if (item.size >= 4L) item.readIntLe() else null
        val minuteTimeLimit = if (item.size >= 4L) item.readIntLe() else null
        val turnTimeLimit = if (item.size >= 4L) item.readIntLe() else null
        val timescaleNumberOfTurns = if (item.size >= 28L) List(7) { item.readIntLe() } else null
        val turnNumberOfTimeUnits = if (item.size >= 28L) List(7) { item.readIntLe() } else null
        val timeOptions = if (
            useTimeLimit != null && baseTimeUnit != null && startMonth != null && startWeek != null &&
            startYear != null && minuteTimeLimit != null && turnTimeLimit != null &&
            timescaleNumberOfTurns != null && turnNumberOfTimeUnits != null
        ) {
            GameTimeOptions(
                useTimeLimit = useTimeLimit,
                baseTimeUnit = baseTimeUnit,
                startMonth = startMonth,
                startWeek = startWeek,
                startYear = startYear,
                minuteTimeLimit = minuteTimeLimit,
                turnTimeLimit = turnTimeLimit,
                timescaleNumberOfTurns = timescaleNumberOfTurns,
                turnNumberOfTimeUnits = turnNumberOfTimeUnits,
            )
        } else {
            null
        }

        val scenarioSearchFolders = if (item.size >= 5200L) {
            item.readByteString(5200L).truncateAtFirstNull()
        } else {
            ""
        }
        val civAllianceStatuses = if (item.size >= 4L * numberOfPlayableCivs) {
            List(numberOfPlayableCivs) { item.readIntLe() }
        } else {
            List(numberOfPlayableCivs) { 0 }
        }

        val victoryPointLimit = if (item.size >= 4L) item.readIntLe() else null
        val cityEliminationCount = if (item.size >= 4L) item.readIntLe() else null
        val oneCityCultureWin = if (item.size >= 4L) item.readIntLe() else null
        val allCitiesCultureWin = if (item.size >= 4L) item.readIntLe() else null
        val dominationTerrain = if (item.size >= 4L) item.readIntLe() else null
        val dominationPopulation = if (item.size >= 4L) item.readIntLe() else null
        val wonderCost = if (item.size >= 4L) item.readIntLe() else null
        val defeatingOpposingUnitCost = if (item.size >= 4L) item.readIntLe() else null
        val advancementCost = if (item.size >= 4L) item.readIntLe() else null
        val cityConquestPopulation = if (item.size >= 4L) item.readIntLe() else null
        val victoryPointScoring = if (item.size >= 4L) item.readIntLe() else null
        val capturingSpecialUnit = if (item.size >= 4L) item.readIntLe() else null

        val unknown = if (item.size >= 5L) item.readByteString(5L) else ByteString.of(0, 0, 0, 0, 0)

        val allianceNames = if (item.size >= 1280L) {
            List(5) { item.readByteString(256L).truncateAtFirstNull() }
        } else {
            null
        }
        val allianceWars = if (item.size >= 100L) List(25) { item.readIntLe() } else null
        val allianceVictoryType = if (item.size >= 4L) item.readIntLe() else null
        val lockedAlliance = if (allianceNames != null && allianceWars != null && allianceVictoryType != null) {
            GameLockedAlliance(allianceNames, allianceWars, allianceVictoryType)
        } else {
            null
        }

        val plagueName = if (item.size >= 260L) item.readByteString(260L).truncateAtFirstNull() else null
        val permitPlagues = if (item.size >= 1L) item.readByte() else null
        val plagueEarliestStart = if (item.size >= 4L) item.readIntLe() else null
        val plagueVariation = if (item.size >= 4L) item.readIntLe() else null
        val plagueDuration = if (item.size >= 4L) item.readIntLe() else null
        val plagueStrength = if (item.size >= 4L) item.readIntLe() else null
        val plagueGracePeriod = if (item.size >= 4L) item.readIntLe() else null
        val plagueMaxOccurrence = if (item.size >= 4L) item.readIntLe() else null
        val plagueSettings = if (
            plagueName != null && permitPlagues != null && plagueEarliestStart != null &&
            plagueVariation != null && plagueDuration != null && plagueStrength != null &&
            plagueGracePeriod != null && plagueMaxOccurrence != null
        ) {
            GamePlagueSettings(
                plagueName = plagueName,
                permitPlagues = permitPlagues,
                plagueEarliestStart = plagueEarliestStart,
                plagueVariation = plagueVariation,
                plagueDuration = plagueDuration,
                plagueStrength = plagueStrength,
                plagueGracePeriod = plagueGracePeriod,
                plagueMaxOccurrence = plagueMaxOccurrence,
            )
        } else {
            null
        }

        val unknown2 = if (item.size >= 264L) item.readByteString(264L) else ByteString.of(*ByteArray(264))

        val respawnFlagUnits = if (item.size >= 4L) item.readIntLe() else null
        val captureAnyFlag = if (item.size >= 1L) item.readByte() else null
        val goldForCapture = if (item.size >= 4L) item.readIntLe() else null
        val victoryPointLimits = if (
            victoryPointLimit != null && cityEliminationCount != null && oneCityCultureWin != null &&
            allCitiesCultureWin != null && dominationTerrain != null && dominationPopulation != null &&
            wonderCost != null && defeatingOpposingUnitCost != null && advancementCost != null &&
            cityConquestPopulation != null && victoryPointScoring != null && capturingSpecialUnit != null &&
            respawnFlagUnits != null && captureAnyFlag != null && goldForCapture != null
        ) {
            GameVictoryPointLimits(
                victoryPointLimit = victoryPointLimit,
                cityEliminationCount = cityEliminationCount,
                oneCityCultureWin = oneCityCultureWin,
                allCitiesCultureWin = allCitiesCultureWin,
                dominationTerrain = dominationTerrain,
                dominationPopulation = dominationPopulation,
                wonderCost = wonderCost,
                defeatingOpposingUnitCost = defeatingOpposingUnitCost,
                advancementCost = advancementCost,
                cityConquestPopulation = cityConquestPopulation,
                victoryPointScoring = victoryPointScoring,
                capturingSpecialUnit = capturingSpecialUnit,
                respawnFlagUnits = respawnFlagUnits,
                captureAnyFlag = captureAnyFlag,
                goldForCapture = goldForCapture,
            )
        } else {
            null
        }

        val mapVisible = if (item.size >= 1L) item.readByte() else 0.toByte()
        val retainCulture = if (item.size >= 1L) item.readByte() else 0.toByte()
        val unknown3 = if (item.size >= 4L) item.readByteString(4L) else ByteString.of(0, 0, 0, 0)
        val eruptionPeriod = if (item.size >= 4L) item.readIntLe() else 0

        val mpBasetime = if (item.size >= 4L) item.readIntLe() else null
        val mpCityTime = if (item.size >= 4L) item.readIntLe() else null
        val mpUnitTime = if (item.size >= 4L) item.readIntLe() else null
        val mpTimers = if (mpBasetime != null && mpCityTime != null && mpUnitTime != null) {
            GameMpTimers(mpBasetime, mpCityTime, mpUnitTime)
        } else {
            null
        }

        return GameEntry(
            defaultGameRules = defaultGameRules,
            defaultVictoryConditions = defaultVictoryConditions,
            numberOfPlayableCivs = numberOfPlayableCivs,
            playableCivIds = playableCivIds,
            flags = flags,
            placeCaptureUnits = placeCaptureUnits,
            autoPlaceKings = autoPlaceKings,
            autoPlaceVictoryLocations = autoPlaceVictoryLocations,
            debugMode = debugMode,
            timeOptions = timeOptions,
            scenarioSearchFolders = scenarioSearchFolders,
            civAllianceStatuses = civAllianceStatuses,
            victoryPointLimits = victoryPointLimits,
            unknown = unknown,
            lockedAlliance = lockedAlliance,
            plagueSettings = plagueSettings,
            unknown2 = unknown2,
            mapVisible = mapVisible,
            retainCulture = retainCulture,
            unknown3 = unknown3,
            eruptionPeriod = eruptionPeriod,
            mpTimers = mpTimers,
        )
    }
}
