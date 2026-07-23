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
