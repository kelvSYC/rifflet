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
