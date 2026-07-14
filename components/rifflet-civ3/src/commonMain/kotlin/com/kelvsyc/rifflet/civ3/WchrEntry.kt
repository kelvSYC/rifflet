package com.kelvsyc.rifflet.civ3

/**
 * One entry of the `WCHR` section: the scenario's world-generation settings, each recorded as
 * both a "selected" value (what was requested) and an "actual" value (what was rolled — only
 * relevant when the selected value is a "Random" sentinel).
 */
data class WchrEntry(
    val selectedClimate: Int,
    val actualClimate: Int,
    val selectedBarbarianActivity: Int,
    val actualBarbarianActivity: Int,
    val selectedLandform: Int,
    val actualLandform: Int,
    val selectedOceanCoverage: Int,
    val actualOceanCoverage: Int,
    val selectedTemperature: Int,
    val actualTemperature: Int,
    val selectedAge: Int,
    val actualAge: Int,
    /** Duplicates the world-size setting also recorded in the file's `WSIZ` section — not an
     * index (unlike `SlocEntry.owner`/`ClnyEntry.owner`); `WSIZ` has exactly one entry per
     * file, so indexing into it wouldn't be a meaningful operation here. */
    val worldSize: Int,
)
