package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity

/**
 * Flags [TerrEntry.pollutionEffect] values that resolve to neither the `None` sentinel (`-1`),
 * the `BaseTerrainType` sentinel (the `TERR` section's own entry count), nor a valid `TERR`
 * index — see [TerrPollutionEffect] for what each case means. Returns no issues if the `TERR`
 * section is absent from [file].
 */
fun validatePollutionEffect(file: Civ3File): List<ValidationIssue> {
    val terrSection = file.sections.filterIsInstance<TerrSection>().singleOrNull()
        ?: return emptyList()

    return terrSection.entries.mapIndexedNotNull { index, entry ->
        val resolved = entry.pollutionEffectResolved(terrSection.entries)
        if (resolved is TerrPollutionEffect.Terrain && resolved.terrain == null) {
            ValidationIssue(
                ValidationSeverity.ERROR,
                terrSection.chunkId,
                index,
                "pollutionEffect",
                "pollutionEffect=${entry.pollutionEffect} is not -1, not the base-terrain " +
                    "sentinel (${terrSection.entries.size}), and not a valid TERR index " +
                    "(0..<${terrSection.entries.size})",
            )
        } else {
            null
        }
    }
}

private const val FOREST_TERR_INDEX = 7
private const val CLEAR_FOREST_TFRM_INDEX = 6

/**
 * Flags a `TERR` entry other than the Forest terrain type whose [TerrEntry.workerJobAllowed]
 * points at the Clear Forest worker job. Returns no issues if the `TERR` section is absent from
 * [file].
 *
 * "Clear Forest" only functions correctly on the Forest terrain type; every real vanilla, PTW,
 * and Conquests ruleset assigns it exclusively there. Both terrain type and worker job are
 * Rename-only, fixed-position slots in the Rules Editor — Forest is always `TERR` index 7 and
 * Clear Forest is always `TFRM` index 6, regardless of era or in-file renaming (e.g. Mesoamerica's
 * "Rain Forest").
 */
fun validateClearForestExclusiveToForest(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<TerrSection>().singleOrNull() ?: return emptyList()
    return section.entries.mapIndexedNotNull { index, entry ->
        if (index == FOREST_TERR_INDEX || entry.workerJobAllowed != CLEAR_FOREST_TFRM_INDEX) {
            null
        } else {
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TERR,
                index,
                "workerJobAllowed",
                "TERR[$index] (${entry.name}) has workerJobAllowed=$CLEAR_FOREST_TFRM_INDEX (Clear Forest), " +
                    "which only functions on the Forest terrain type (TERR[$FOREST_TERR_INDEX])",
            )
        }
    }
}
