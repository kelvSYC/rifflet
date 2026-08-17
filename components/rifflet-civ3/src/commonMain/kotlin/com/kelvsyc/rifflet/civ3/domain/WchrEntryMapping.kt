package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.WchrEntry

/**
 * Converts a parsed `WCHR` section to its domain-layer form.
 *
 * [worldSizes] is the already domain-converted `WSIZ` list (`WSIZ`'s `Map<WorldSizeSlot,
 * WorldSizePreset>.toOrderedList()`). Flat list, no cardinality guard — no wire-layer cardinality
 * rule exists for `WCHR` to mirror, unlike `WSIZ`/`WMAP`.
 */
fun List<WchrEntry>.toDomain(worldSizes: List<WorldSizePreset>): List<WorldGenerationSettings> = map { entry ->
    WorldGenerationSettings(
        climate = GeneratedChoice(entry.selectedClimate, entry.actualClimate),
        barbarianActivity = GeneratedChoice(entry.selectedBarbarianActivity, entry.actualBarbarianActivity),
        landform = GeneratedChoice(entry.selectedLandform, entry.actualLandform),
        oceanCoverage = GeneratedChoice(entry.selectedOceanCoverage, entry.actualOceanCoverage),
        temperature = GeneratedChoice(entry.selectedTemperature, entry.actualTemperature),
        age = GeneratedChoice(entry.selectedAge, entry.actualAge),
        worldSize = worldSizes.getOrNull(entry.worldSize),
    )
}

/**
 * Converts a `WCHR` section's domain-layer form back to wire entries.
 *
 * Throws [IllegalArgumentException] if [WorldGenerationSettings.worldSize] resolves to an object
 * not present in [worldSizes] — `indexOf`-based, the same accepted structural-equality limitation
 * as every other `toWire()` in this codebase. A `null` [WorldGenerationSettings.worldSize] writes
 * back `-1`, not preserving the original dangling index.
 */
fun List<WorldGenerationSettings>.toWire(worldSizes: List<WorldSizePreset>): List<WchrEntry> = map { settings ->
    val worldSizeIndex = settings.worldSize?.let {
        val index = worldSizes.indexOf(it)
        require(index >= 0) { "WorldGenerationSettings.worldSize references a WorldSizePreset not present in worldSizes" }
        index
    } ?: -1
    WchrEntry(
        selectedClimate = settings.climate.selected,
        actualClimate = settings.climate.actual,
        selectedBarbarianActivity = settings.barbarianActivity.selected,
        actualBarbarianActivity = settings.barbarianActivity.actual,
        selectedLandform = settings.landform.selected,
        actualLandform = settings.landform.actual,
        selectedOceanCoverage = settings.oceanCoverage.selected,
        actualOceanCoverage = settings.oceanCoverage.actual,
        selectedTemperature = settings.temperature.selected,
        actualTemperature = settings.temperature.actual,
        selectedAge = settings.age.selected,
        actualAge = settings.age.actual,
        worldSize = worldSizeIndex,
    )
}
