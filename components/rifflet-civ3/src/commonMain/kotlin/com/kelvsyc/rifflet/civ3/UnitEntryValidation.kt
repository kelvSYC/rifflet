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

/**
 * Flags a [UnitEntry] whose [UnitEntry.aiStrategy] is not `-1` and not a bit set in its resolved
 * prototype's [PrtoEntry.aiStrategies]. Returns no issues if the `UNIT` or `PRTO` section is
 * absent from [file], or if [UnitEntry.unitType] doesn't resolve to a real prototype.
 *
 * `-1` means no specific strategy is selected — the real Rules/Scenario editor's "Random" option,
 * only offered when a prototype has 2+ `aiStrategies` bits set; with exactly one bit set, the
 * editor disables the selector entirely. Every other observed value is a bit index into the
 * resolved prototype's `aiStrategies` — confirmed with a 97.4% corpus match rate, with every
 * exception traced to non-official mod content.
 */
fun validateUnitAiStrategyMatchesPrtoStrategy(file: Civ3File): List<ValidationIssue> {
    val unit = file.sections.filterIsInstance<UnitSection>().singleOrNull() ?: return emptyList()
    val prto = file.sections.filterIsInstance<PrtoSection>().singleOrNull() ?: return emptyList()
    return unit.entries.mapIndexedNotNull { index, entry ->
        if (entry.aiStrategy == -1) return@mapIndexedNotNull null
        val proto = prto.entries.getOrNull(entry.unitType) ?: return@mapIndexedNotNull null
        if ((proto.aiStrategies shr entry.aiStrategy) and 1 == 1) {
            null
        } else {
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.UNIT,
                index,
                "aiStrategy",
                "aiStrategy=${entry.aiStrategy} is not a bit set in prototype ${entry.unitType}'s " +
                    "aiStrategies (${proto.aiStrategies})",
            )
        }
    }
}
