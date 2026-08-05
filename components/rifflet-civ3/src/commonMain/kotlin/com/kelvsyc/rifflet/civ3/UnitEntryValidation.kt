package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity

/**
 * Flags a [UnitEntry] whose `(x, y)` map location sums to an odd value. Returns no issues if the
 * `UNIT` section is absent from [file].
 *
 * Civ3's isometric tile grid (see [WmapEntry.tileIndex]) only assigns real tiles to `(x, y)`
 * pairs where `x` and `y` share parity. `WARNING` rather than `ERROR`: a real official `SLOC`
 * entry (Sengoku's Uesugi starting location) is known to store an odd-parity `x`, and is provably
 * harmless — that `x` is index-equivalent to `x + 1` once combined with the same `y`, since
 * [WmapEntry.tileIndex]'s `x / 2` division discards the low bit.
 */
fun validateUnitCoordinateParity(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<UnitSection>().singleOrNull() ?: return emptyList()
    return section.entries.mapIndexedNotNull { index, entry ->
        if ((entry.x + entry.y) % 2 == 0) {
            null
        } else {
            ValidationIssue(
                ValidationSeverity.WARNING,
                Civ3SectionIds.UNIT,
                index,
                "x/y",
                "x=${entry.x}, y=${entry.y} sum to an odd value; Civ3's isometric tile grid " +
                    "expects x and y to share parity",
            )
        }
    }
}

/**
 * Flags a [UnitEntry] whose [UnitEntry.ownerType] is `0` (None). Returns no issues if the `UNIT`
 * section is absent from [file].
 *
 * The real Rules/Scenario editor does not allow placing a unit while the active owner is "None" —
 * every unit must belong to a real civilization, player, or barbarians. Confirmed with zero
 * exceptions across the corpus.
 */
fun validateUnitOwnerNotNone(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<UnitSection>().singleOrNull() ?: return emptyList()
    return section.entries.mapIndexedNotNull { index, entry ->
        if (entry.ownerType != 0) return@mapIndexedNotNull null
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.UNIT,
            index,
            "ownerType",
            "ownerType=0 (None) is not allowed for UNIT entries; the Rules/Scenario editor " +
                "requires every unit to belong to a real civilization, player, or barbarians",
        )
    }
}

/**
 * Flags a [UnitEntry] whose owner is [UnitEntry.ownerType] `2` (Civilization) pointing at `RACE`
 * index `0`, the barbarian placeholder civilization. Returns no issues if the `UNIT` section is
 * absent from [file].
 *
 * The real Rules/Scenario editor does not allow the barbarian placeholder civ to own a unit
 * directly — barbarian units use `ownerType=1` instead. Confirmed with zero exceptions across the
 * corpus.
 */
fun validateUnitOwnerNotBarbarianPlaceholderCiv(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<UnitSection>().singleOrNull() ?: return emptyList()
    return section.entries.mapIndexedNotNull { index, entry ->
        if (!(entry.ownerType == 2 && entry.owner == 0)) return@mapIndexedNotNull null
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.UNIT,
            index,
            "owner",
            "owner=0 with ownerType=2 (Civilization) is not allowed for UNIT entries; RACE index " +
                "0 is the barbarian placeholder civilization, which the Rules/Scenario editor " +
                "does not allow as a unit owner",
        )
    }
}
