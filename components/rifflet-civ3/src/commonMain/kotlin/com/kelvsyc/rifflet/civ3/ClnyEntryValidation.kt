package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity

/**
 * Flags a [ClnyEntry] whose `(x, y)` map location sums to an odd value. Returns no issues if the
 * `CLNY` section is absent from [file].
 *
 * Civ3's isometric tile grid (see [WmapEntry.tileIndex]) only assigns real tiles to `(x, y)`
 * pairs where `x` and `y` share parity. `WARNING` rather than `ERROR`: a real official `SLOC`
 * entry (Sengoku's Uesugi starting location) is known to store an odd-parity `x`, and is provably
 * harmless — that `x` is index-equivalent to `x + 1` once combined with the same `y`, since
 * [WmapEntry.tileIndex]'s `x / 2` division discards the low bit.
 */
fun validateClnyCoordinateParity(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<ClnySection>().singleOrNull() ?: return emptyList()
    return section.entries.mapIndexedNotNull { index, entry ->
        if ((entry.x + entry.y) % 2 == 0) {
            null
        } else {
            ValidationIssue(
                ValidationSeverity.WARNING,
                Civ3SectionIds.CLNY,
                index,
                "x/y",
                "x=${entry.x}, y=${entry.y} sum to an odd value; Civ3's isometric tile grid " +
                    "expects x and y to share parity",
            )
        }
    }
}

/**
 * Flags a [ClnyEntry] whose [ClnyEntry.ownerType] is `0` (None) or `1` (Barbarian). Returns no
 * issues if the `CLNY` section is absent from [file].
 *
 * The real Rules/Scenario editor does not allow a placed colony, airfield, radar tower, or
 * outpost to be assigned "None" or "Barbarian" ownership — every one of these must belong to a
 * real civilization or player. Confirmed with zero exceptions across the corpus (every real CLNY
 * entry is `ownerType` 2 or 3), uniformly across all 4 [ClnyImprovementType] values.
 */
fun validateClnyOwnerRequiresRealNation(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<ClnySection>().singleOrNull() ?: return emptyList()
    return section.entries.mapIndexedNotNull { index, entry ->
        if (entry.ownerType != 0 && entry.ownerType != 1) return@mapIndexedNotNull null
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.CLNY,
            index,
            "ownerType",
            "ownerType=${entry.ownerType} is not allowed for CLNY entries; the Rules/Scenario " +
                "editor requires every colony/airfield/radar tower/outpost to belong to a real " +
                "civilization or player",
        )
    }
}

/**
 * Flags a [ClnyEntry] whose owner is [ClnyEntry.ownerType] `2` (Civilization) pointing at `RACE`
 * index `0`, the barbarian placeholder civilization. Returns no issues if the `CLNY` section is
 * absent from [file].
 *
 * The real Rules/Scenario editor does not allow the barbarian placeholder civ to own a colony,
 * airfield, radar tower, or outpost. Confirmed with zero exceptions across the corpus.
 */
fun validateClnyOwnerNotBarbarianPlaceholderCiv(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<ClnySection>().singleOrNull() ?: return emptyList()
    return section.entries.mapIndexedNotNull { index, entry ->
        if (!(entry.ownerType == 2 && entry.owner == 0)) return@mapIndexedNotNull null
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.CLNY,
            index,
            "owner",
            "owner=0 with ownerType=2 (Civilization) is not allowed for CLNY entries; RACE index " +
                "0 is the barbarian placeholder civilization, which the Rules/Scenario editor " +
                "does not allow as an owner",
        )
    }
}
