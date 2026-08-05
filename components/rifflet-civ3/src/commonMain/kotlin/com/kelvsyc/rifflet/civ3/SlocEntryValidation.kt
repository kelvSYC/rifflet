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

/**
 * Flags a [SlocEntry] whose [SlocEntry.ownerType] falls outside the documented `0..3` range.
 * Returns no issues if the `SLOC` section is absent from [file].
 *
 * The real Rules/Scenario editor only ever produces `ownerType` 0-3; every real starting location
 * in the corpus is 0, 2, or 3.
 */
fun validateSlocOwnerTypeRecognized(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<SlocSection>().singleOrNull() ?: return emptyList()
    return section.entries.mapIndexedNotNull { index, entry ->
        if (entry.ownerType in 0..3) return@mapIndexedNotNull null
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.SLOC,
            index,
            "ownerType",
            "ownerType=${entry.ownerType} is not a recognized value (0..3)",
        )
    }
}

/**
 * Flags a [SlocEntry] with [SlocEntry.ownerType] `== 1` (Barbarian). Returns no issues if the
 * `SLOC` section is absent from [file].
 *
 * The real Rules/Scenario editor refuses to let a starting location be assigned to Barbarians
 * ("Barbarians cannot have starting locations. Please select an active civilization or player
 * before placing a starting location.") — confirmed with zero exceptions across the corpus.
 */
fun validateSlocOwnerNotBarbarian(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<SlocSection>().singleOrNull() ?: return emptyList()
    return section.entries.mapIndexedNotNull { index, entry ->
        if (entry.ownerType != 1) return@mapIndexedNotNull null
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.SLOC,
            index,
            "ownerType",
            "ownerType=1 (Barbarian) is not allowed for SLOC entries; the Rules/Scenario editor " +
                "refuses to assign a starting location to Barbarians",
        )
    }
}

/**
 * Flags more than one [SlocEntry] sharing the same `(ownerType, owner)` pair among
 * Civilization-owned (`ownerType=2`) or Player-owned (`ownerType=3`) entries, within [file].
 * Returns no issues if the `SLOC` section is absent from [file].
 *
 * Each civilization/player gets at most one starting location — confirmed with zero exceptions
 * across the corpus. `None`-owned (`ownerType=0`) entries are exempt: many reserved `None` slots
 * commonly coexist in the same file.
 */
fun validateSlocUniqueOwner(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<SlocSection>().singleOrNull() ?: return emptyList()
    val entriesByOwner = mutableMapOf<Pair<Int, Int>, MutableList<Int>>()
    section.entries.forEachIndexed { index, entry ->
        if (entry.ownerType != 2 && entry.ownerType != 3) return@forEachIndexed
        entriesByOwner.getOrPut(entry.ownerType to entry.owner) { mutableListOf() }.add(index)
    }
    return entriesByOwner.filterValues { it.size > 1 }.map { (key, indices) ->
        val (ownerType, owner) = key
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.SLOC,
            null,
            "owner",
            "ownerType=$ownerType/owner=$owner has more than one starting location: $indices",
        )
    }
}

/**
 * Flags a [SlocEntry] whose owner is [SlocEntry.ownerType] `2` (Civilization) pointing at `RACE`
 * index `0`, the barbarian placeholder civilization. Returns no issues if the `SLOC` section is
 * absent from [file].
 *
 * The real Rules/Scenario editor does not allow a starting location to be assigned the barbarian
 * placeholder civ as its owner. Confirmed with zero exceptions across the corpus.
 */
fun validateSlocOwnerNotBarbarianPlaceholderCiv(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<SlocSection>().singleOrNull() ?: return emptyList()
    return section.entries.mapIndexedNotNull { index, entry ->
        if (!(entry.ownerType == 2 && entry.owner == 0)) return@mapIndexedNotNull null
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.SLOC,
            index,
            "owner",
            "owner=0 with ownerType=2 (Civilization) is not allowed for SLOC entries; RACE index " +
                "0 is the barbarian placeholder civilization, which the Rules/Scenario editor " +
                "does not allow as a starting-location owner",
        )
    }
}
