package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.TerrainSlot.DESERT
import com.kelvsyc.rifflet.civ3.TerrainSlot.FOREST
import com.kelvsyc.rifflet.civ3.TerrainSlot.GRASSLAND
import com.kelvsyc.rifflet.civ3.TerrainSlot.HILLS
import com.kelvsyc.rifflet.civ3.TerrainSlot.MOUNTAINS
import com.kelvsyc.rifflet.civ3.TerrainSlot.PLAINS
import com.kelvsyc.rifflet.civ3.TerrainSlot.SEA
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

// Forest's TERR index is stable across every era (see TerrainSlot); computed rather than
// hardcoded so a future era addition can't silently desync this from the real mapping.
private val FOREST_TERR_INDEX = FOREST.index(Civ3FormatEra.CONQUESTS)!!
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

/**
 * Flags a [TerrEntry] whose [TerrEntry.curedBySanitation] is set without
 * [TerrEntry.causesDisease]. Returns no issues if the `TERR` section is absent from [file].
 *
 * The Terrain editor tab's "Cured by Sanitation" checkbox is only enabled once "Causes Disease"
 * is checked — every real vanilla, PTW, and Conquests ruleset respects this.
 */
fun validateCuredBySanitationRequiresCausesDisease(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<TerrSection>().singleOrNull() ?: return emptyList()
    return section.entries.mapIndexedNotNull { index, entry ->
        if (!entry.curedBySanitation || entry.causesDisease) {
            null
        } else {
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TERR,
                index,
                "curedBySanitation",
                "curedBySanitation is set but causesDisease isn't; the Rules Editor only allows enabling " +
                    "Cured by Sanitation when Causes Disease is also checked",
            )
        }
    }
}

// This rule only ever evaluates entries with a non-null `landmark` (Conquests-only), so resolving
// against CONQUESTS is always the correct era here.
private val LANDMARK_TERR_INDICES =
    setOf(DESERT, PLAINS, GRASSLAND, HILLS, MOUNTAINS, FOREST, SEA).mapNotNull { it.index(Civ3FormatEra.CONQUESTS) }.toSet()

/**
 * Flags a [TerrEntry] whose [TerrLandmark.landmarkEnabled] is set on a `TERR` index other than
 * Desert(0)/Plains(1)/Grassland(2)/Hills(5)/Mountains(6)/Forest(7)/Sea(12). Returns no issues if
 * the `TERR` section is absent from [file], or for an entry whose [TerrEntry.landmark] is `null`
 * (a [Civ3FormatEra.VANILLA]/[Civ3FormatEra.PTW] file, which predates landmarks entirely).
 *
 * These 7 fixed-position terrain types are the only ones the Terrain editor tab offers landmark
 * information for — every other terrain type's landmark data is present in the file (the whole
 * `Landmark Information` panel is always read once a file is `CONQUESTS`-era) but structurally
 * disabled.
 */
fun validateLandmarkEnabledOnlyOnSupportedTerrainTypes(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<TerrSection>().singleOrNull() ?: return emptyList()
    return section.entries.mapIndexedNotNull { index, entry ->
        val landmark = entry.landmark ?: return@mapIndexedNotNull null
        if (index in LANDMARK_TERR_INDICES || landmark.landmarkEnabled.toInt() == 0) {
            null
        } else {
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TERR,
                index,
                "landmark",
                "TERR[$index] (${entry.name}) has landmark.landmarkEnabled set, but the Rules Editor only " +
                    "offers landmark information for Desert/Plains/Grassland/Hills/Mountains/Forest/Sea",
            )
        }
    }
}
