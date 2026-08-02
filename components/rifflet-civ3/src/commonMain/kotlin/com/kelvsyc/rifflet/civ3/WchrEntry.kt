package com.kelvsyc.rifflet.civ3

/**
 * One entry of the `WCHR` section: the scenario's world-generation settings.
 *
 * Each setting is recorded as both a "selected" value (what was requested) and an "actual" value
 * (what was rolled — only relevant when the selected value is a "Random" sentinel).
 *
 * @param selectedClimate The "Climate" dropdown's requested value.
 * @param actualClimate The "Climate" dropdown's rolled value.
 * @param selectedBarbarianActivity The "Barbarian Activity" dropdown's requested value.
 * @param actualBarbarianActivity The "Barbarian Activity" dropdown's rolled value.
 * @param selectedLandform The "Landform" dropdown's requested value.
 * @param actualLandform The "Landform" dropdown's rolled value.
 * @param selectedOceanCoverage The "% Water Coverage" dropdown's requested value.
 * @param actualOceanCoverage The "% Water Coverage" dropdown's rolled value.
 * @param selectedTemperature The "Temperature" dropdown's requested value.
 * @param actualTemperature The "Temperature" dropdown's rolled value.
 * @param selectedAge The "Age" dropdown's requested value.
 * @param actualAge The "Age" dropdown's rolled value.
 * @param worldSize The "World Size" dropdown's requested value, a `WSIZ` section index — see
 *   [worldSizeWsiz]. `WSIZ` always has exactly 5 entries in a real file (Tiny/Small/Standard/
 *   Large/Huge); declared world size is independent of a map's actual painted dimensions (the map
 *   editor's own "Generate a Map" dialog allows setting width/height directly, separately from
 *   this dropdown). Two real files (both bare map exports, no `WSIZ` section of their own) have an
 *   out-of-range value here; loading one of them and opening "Generate a Map" in the real editor
 *   shows "Select World Size" as blank, matching none of the 5 named presets — real but limited
 *   confirmation this is a genuine "no valid selection" state, not just this codebase's `getOrNull`
 *   producing `null` on any unrecognized index. Unlike every other `WCHR` field, there is no
 *   separate "actual"/rolled counterpart for this one.
 */
data class WchrEntry(
    val selectedClimate: Climate,
    val actualClimate: Climate,
    val selectedBarbarianActivity: BarbarianActivity,
    val actualBarbarianActivity: BarbarianActivity,
    val selectedLandform: Landform,
    val actualLandform: Landform,
    val selectedOceanCoverage: OceanCoverage,
    val actualOceanCoverage: OceanCoverage,
    val selectedTemperature: Temperature,
    val actualTemperature: Temperature,
    val selectedAge: Age,
    val actualAge: Age,
    val worldSize: Int,
)
