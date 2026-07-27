package com.kelvsyc.rifflet.civ3

/**
 * The Conquests Rules Editor's `Scenario Properties` → `Victory Point Limits` tab, in its
 * entirety — present ([GameEntry.victoryPointLimits] non-`null`) only in [Civ3FormatEra.CONQUESTS]
 * files, which are the only files with this tab at all.
 *
 * [victoryPointLimit] through [capturingSpecialUnit] and [respawnFlagUnits] through
 * [goldForCapture] are two separate, non-contiguous byte ranges in the file — `GameEntry.unknown`,
 * [GameEntry.lockedAlliance], [GameEntry.plagueSettings], and `GameEntry.unknown2` sit between
 * them — bundled here into one type because the editor presents them as a single tab.
 *
 * @param victoryPointLimit The "Victory Point Winning Conditions" group's "Victory Point Limit".
 * @param cityEliminationCount The "City Elimination Count" field.
 * @param oneCityCultureWin The "Culture Value for 1 City" field.
 * @param allCitiesCultureWin The "Culture Value for All Cities" field.
 * @param dominationTerrain The "% Terrain for Domination" field.
 * @param dominationPopulation The "% Population for Domination" field.
 * @param wonderCost The "Victory Points" group's "Wonder * cost" field.
 * @param defeatingOpposingUnitCost The "Defeating Opposing Unit * cost" field.
 * @param advancementCost The "Advancement * cost" field.
 * @param cityConquestPopulation The "City Conquest * population" field.
 * @param victoryPointScoring The "Victory Point Scoring" field.
 * @param capturingSpecialUnit The "Capturing Special Unit" field.
 * @param respawnFlagUnits The "Victory Point Winning Conditions" group's "Respawn Flag Unit on
 *   Capture" checkbox.
 * @param captureAnyFlag The "Allow Anyone to Capture Any Flag" checkbox.
 * @param goldForCapture The "Victory Points" group's "Gold for Capture" field.
 */
data class GameVictoryPointLimits(
    val victoryPointLimit: Int,
    val cityEliminationCount: Int,
    val oneCityCultureWin: Int,
    val allCitiesCultureWin: Int,
    val dominationTerrain: Int,
    val dominationPopulation: Int,
    val wonderCost: Int,
    val defeatingOpposingUnitCost: Int,
    val advancementCost: Int,
    val cityConquestPopulation: Int,
    val victoryPointScoring: Int,
    val capturingSpecialUnit: Int,
    val respawnFlagUnits: Int,
    val captureAnyFlag: Byte,
    val goldForCapture: Int,
)
