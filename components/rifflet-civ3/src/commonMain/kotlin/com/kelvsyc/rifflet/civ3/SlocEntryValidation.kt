package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity

/**
 * Flags a [SlocEntry] whose `(x, y)` map location sums to an odd value. Returns no issues if the
 * `SLOC` section is absent from [file].
 *
 * Civ3's isometric tile grid (see [WmapEntry.tileIndex]) only assigns real tiles to `(x, y)`
 * pairs where `x` and `y` share parity. `WARNING` rather than `ERROR`: a real official `SLOC`
 * entry (Sengoku's Uesugi starting location, stored as `x=52, y=19` though the Rules Editor
 * displays `x=53`) is known to violate this, and is provably harmless — that `x` is
 * index-equivalent to `x + 1` once combined with the same `y`, since [WmapEntry.tileIndex]'s
 * `x / 2` division discards the low bit.
 */
fun validateSlocCoordinateParity(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<SlocSection>().singleOrNull() ?: return emptyList()
    return section.entries.mapIndexedNotNull { index, entry ->
        if ((entry.x + entry.y) % 2 == 0) {
            null
        } else {
            ValidationIssue(
                ValidationSeverity.WARNING,
                Civ3SectionIds.SLOC,
                index,
                "x/y",
                "x=${entry.x}, y=${entry.y} sum to an odd value; Civ3's isometric tile grid " +
                    "expects x and y to share parity",
            )
        }
    }
}
