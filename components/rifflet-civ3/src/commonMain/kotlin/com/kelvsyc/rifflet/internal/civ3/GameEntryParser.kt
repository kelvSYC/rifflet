package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.GameEntry
import okio.Buffer
import okio.ByteString

/**
 * Parses one `GAME` item, per the Apolyton BIX/BIQ format documentation cross-validated against
 * `QueryCiv3`'s struct. Reads directly off [item], a zero-copy-transferred [Buffer] already
 * stripped of its own length prefix by the generic section loop.
 *
 * [GameEntry.numberOfPlayableCivs] is read once and reused (not re-read) to size two separate
 * dynamic reads at different points in the layout: [GameEntry.playableCivIds] immediately after
 * it, and [GameEntry.civAllianceStatuses] much later, after [GameEntry.scenarioSearchFolders].
 * A 4-byte BIX-only `mapVisible (long)` field documented by Apolyton between those two gaps is
 * deliberately NOT read here — see [GameEntry]'s class-level KDoc for why.
 *
 * [GameEntry.numberOfPlayableCivs] is validated via [requireSaneCount] immediately after being
 * read, before it sizes [GameEntry.playableCivIds] or (much later) [GameEntry.civAllianceStatuses]
 * — see that function's KDoc for why.
 *
 * Every field after the fixed 5-field header ([GameEntry.defaultGameRules]/
 * [GameEntry.defaultVictoryConditions]/[GameEntry.numberOfPlayableCivs]/
 * [GameEntry.playableCivIds]/[GameEntry.flags]) is read defensively: [Civ3FormatEra.VANILLA]
 * files can end immediately after [GameEntry.flags] (a bare 16-byte item); [Civ3FormatEra.PTW]
 * files are the one confirmed case of genuine within-era `minor` sensitivity in this codebase —
 * they end at one of 3 different cutoff points depending on `minor`
 * ([GameEntry.autoPlaceVictoryLocations] for `minor=6/9/10`, [GameEntry.debugMode] for
 * `minor=13`, [GameEntry.scenarioSearchFolders] for the dominant `minor=18` tier);
 * [Civ3FormatEra.CONQUESTS] files always include everything through
 * [GameEntry.scenarioSearchFolders] and omit only the trailing mp-timing fields on `minor=6`.
 * `civAllianceStatuses` defaults to `numberOfPlayableCivs` zeros (not an empty list) to satisfy
 * its own size invariant; every other newly-guarded field defaults to the same full-sized
 * zero/empty placeholder used throughout this codebase.
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
        val useTimeLimit = if (item.size >= 4L) item.readIntLe() else 0
        val baseTimeUnit = if (item.size >= 4L) item.readIntLe() else 0
        val startMonth = if (item.size >= 4L) item.readIntLe() else 0
        val startWeek = if (item.size >= 4L) item.readIntLe() else 0
        val startYear = if (item.size >= 4L) item.readIntLe() else 0
        val minuteTimeLimit = if (item.size >= 4L) item.readIntLe() else 0
        val turnTimeLimit = if (item.size >= 4L) item.readIntLe() else 0
        val timescaleNumberOfTurns = if (item.size >= 28L) List(7) { item.readIntLe() } else List(7) { 0 }
        val turnNumberOfTimeUnits = if (item.size >= 28L) List(7) { item.readIntLe() } else List(7) { 0 }
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
        val victoryPointLimit = if (item.size >= 4L) item.readIntLe() else 0
        val cityEliminationCount = if (item.size >= 4L) item.readIntLe() else 0
        val oneCityCultureWin = if (item.size >= 4L) item.readIntLe() else 0
        val allCitiesCultureWin = if (item.size >= 4L) item.readIntLe() else 0
        val dominationTerrain = if (item.size >= 4L) item.readIntLe() else 0
        val dominationPopulation = if (item.size >= 4L) item.readIntLe() else 0
        val wonderCost = if (item.size >= 4L) item.readIntLe() else 0
        val defeatingOpposingUnitCost = if (item.size >= 4L) item.readIntLe() else 0
        val advancementCost = if (item.size >= 4L) item.readIntLe() else 0
        val cityConquestPopulation = if (item.size >= 4L) item.readIntLe() else 0
        val victoryPointScoring = if (item.size >= 4L) item.readIntLe() else 0
        val capturingSpecialUnit = if (item.size >= 4L) item.readIntLe() else 0
        val unknown = if (item.size >= 5L) item.readByteString(5L) else ByteString.of(0, 0, 0, 0, 0)
        val allianceNames = if (item.size >= 1280L) {
            List(5) { item.readByteString(256L).truncateAtFirstNull() }
        } else {
            List(5) { "" }
        }
        val allianceWars = if (item.size >= 100L) List(25) { item.readIntLe() } else List(25) { 0 }
        val allianceVictoryType = if (item.size >= 4L) item.readIntLe() else 0
        val plagueName = if (item.size >= 260L) item.readByteString(260L).truncateAtFirstNull() else ""
        val permitPlagues = if (item.size >= 1L) item.readByte() else 0.toByte()
        val plagueEarliestStart = if (item.size >= 4L) item.readIntLe() else 0
        val plagueVariation = if (item.size >= 4L) item.readIntLe() else 0
        val plagueDuration = if (item.size >= 4L) item.readIntLe() else 0
        val plagueStrength = if (item.size >= 4L) item.readIntLe() else 0
        val plagueGracePeriod = if (item.size >= 4L) item.readIntLe() else 0
        val plagueMaxOccurrence = if (item.size >= 4L) item.readIntLe() else 0
        val unknown2 = if (item.size >= 264L) item.readByteString(264L) else ByteString.of(*ByteArray(264))
        val respawnFlagUnits = if (item.size >= 4L) item.readIntLe() else 0
        val captureAnyFlag = if (item.size >= 1L) item.readByte() else 0.toByte()
        val goldForCapture = if (item.size >= 4L) item.readIntLe() else 0
        val mapVisible = if (item.size >= 1L) item.readByte() else 0.toByte()
        val retainCulture = if (item.size >= 1L) item.readByte() else 0.toByte()
        val unknown3 = if (item.size >= 4L) item.readByteString(4L) else ByteString.of(0, 0, 0, 0)
        val eruptionPeriod = if (item.size >= 4L) item.readIntLe() else 0
        val mpBasetime = if (item.size >= 4L) item.readIntLe() else 0
        val mpCityTime = if (item.size >= 4L) item.readIntLe() else 0
        val mpUnitTime = if (item.size >= 4L) item.readIntLe() else 0
        return GameEntry(
            defaultGameRules,
            defaultVictoryConditions,
            numberOfPlayableCivs,
            playableCivIds,
            flags,
            placeCaptureUnits,
            autoPlaceKings,
            autoPlaceVictoryLocations,
            debugMode,
            useTimeLimit,
            baseTimeUnit,
            startMonth,
            startWeek,
            startYear,
            minuteTimeLimit,
            turnTimeLimit,
            timescaleNumberOfTurns,
            turnNumberOfTimeUnits,
            scenarioSearchFolders,
            civAllianceStatuses,
            victoryPointLimit,
            cityEliminationCount,
            oneCityCultureWin,
            allCitiesCultureWin,
            dominationTerrain,
            dominationPopulation,
            wonderCost,
            defeatingOpposingUnitCost,
            advancementCost,
            cityConquestPopulation,
            victoryPointScoring,
            capturingSpecialUnit,
            unknown,
            allianceNames,
            allianceWars,
            allianceVictoryType,
            plagueName,
            permitPlagues,
            plagueEarliestStart,
            plagueVariation,
            plagueDuration,
            plagueStrength,
            plagueGracePeriod,
            plagueMaxOccurrence,
            unknown2,
            respawnFlagUnits,
            captureAnyFlag,
            goldForCapture,
            mapVisible,
            retainCulture,
            unknown3,
            eruptionPeriod,
            mpBasetime,
            mpCityTime,
            mpUnitTime,
        )
    }
}
