package com.kelvsyc.rifflet.internal.civ3

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
 * Every field from [GameEntry.civAllianceStatuses] onward is read defensively: confirmed absent
 * entirely from real PTW files with `VER#` header `minor=18` (the dominant PTW sub-tier, 51 of
 * ~91 sampled PTW files) — the item simply ends right after [GameEntry.scenarioSearchFolders].
 * `civAllianceStatuses` defaults to `numberOfPlayableCivs` zeros (not an empty list) to satisfy
 * its own size invariant; every other newly-guarded field defaults to the same full-sized
 * zero/empty placeholder already used throughout this codebase. [GameEntry.mpBasetime]/
 * [GameEntry.mpCityTime]/[GameEntry.mpUnitTime] were already guarded independently by an earlier
 * fix for a separate, narrower Conquests-internal `minor=6` split — unchanged here.
 */
internal object GameEntryParser {
    fun parse(item: Buffer): GameEntry {
        val defaultGameRules = item.readIntLe()
        val defaultVictoryConditions = item.readIntLe()
        val numberOfPlayableCivs = item.readIntLe()
        val playableCivIds = List(numberOfPlayableCivs) { item.readIntLe() }
        val flags = item.readByteString(4L)
        val placeCaptureUnits = item.readIntLe()
        val autoPlaceKings = item.readIntLe()
        val autoPlaceVictoryLocations = item.readIntLe()
        val debugMode = item.readIntLe()
        val useTimeLimit = item.readIntLe()
        val baseTimeUnit = item.readIntLe()
        val startMonth = item.readIntLe()
        val startWeek = item.readIntLe()
        val startYear = item.readIntLe()
        val minuteTimeLimit = item.readIntLe()
        val turnTimeLimit = item.readIntLe()
        val timescaleNumberOfTurns = List(7) { item.readIntLe() }
        val turnNumberOfTimeUnits = List(7) { item.readIntLe() }
        val scenarioSearchFolders = item.readByteString(5200L).truncateAtFirstNull()
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
