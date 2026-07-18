package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.GameEntry
import okio.Buffer

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
        val civAllianceStatuses = List(numberOfPlayableCivs) { item.readIntLe() }
        val victoryPointLimit = item.readIntLe()
        val cityEliminationCount = item.readIntLe()
        val oneCityCultureWin = item.readIntLe()
        val allCitiesCultureWin = item.readIntLe()
        val dominationTerrain = item.readIntLe()
        val dominationPopulation = item.readIntLe()
        val wonderCost = item.readIntLe()
        val defeatingOpposingUnitCost = item.readIntLe()
        val advancementCost = item.readIntLe()
        val cityConquestPopulation = item.readIntLe()
        val victoryPointScoring = item.readIntLe()
        val capturingSpecialUnit = item.readIntLe()
        val unknown = item.readByteString(5L)
        val allianceNames = List(5) { item.readByteString(256L).truncateAtFirstNull() }
        val allianceWars = List(25) { item.readIntLe() }
        val allianceVictoryType = item.readIntLe()
        val plagueName = item.readByteString(260L).truncateAtFirstNull()
        val permitPlagues = item.readByte()
        val plagueEarliestStart = item.readIntLe()
        val plagueVariation = item.readIntLe()
        val plagueDuration = item.readIntLe()
        val plagueStrength = item.readIntLe()
        val plagueGracePeriod = item.readIntLe()
        val plagueMaxOccurrence = item.readIntLe()
        val unknown2 = item.readByteString(264L)
        val respawnFlagUnits = item.readIntLe()
        val captureAnyFlag = item.readByte()
        val goldForCapture = item.readIntLe()
        val mapVisible = item.readByte()
        val retainCulture = item.readByte()
        val unknown3 = item.readByteString(4L)
        val eruptionPeriod = item.readIntLe()
        val mpBasetime = item.readIntLe()
        val mpCityTime = item.readIntLe()
        val mpUnitTime = item.readIntLe()
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
