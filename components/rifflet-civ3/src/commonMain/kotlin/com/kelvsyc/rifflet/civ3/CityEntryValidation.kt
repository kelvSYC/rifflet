package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity

/**
 * Flags a [CityEntry] whose `(x, y)` map location sums to an odd value. Returns no issues if the
 * `CITY` section is absent from [file].
 *
 * Civ3's isometric tile grid (see [WmapEntry.tileIndex]) only assigns real tiles to `(x, y)`
 * pairs where `x` and `y` share parity. `WARNING` rather than `ERROR`: a real official `SLOC`
 * entry (Sengoku's Uesugi starting location) is known to store an odd-parity `x`, and is provably
 * harmless — that `x` is index-equivalent to `x + 1` once combined with the same `y`, since
 * [WmapEntry.tileIndex]'s `x / 2` division discards the low bit.
 */
fun validateCityCoordinateParity(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<CitySection>().singleOrNull() ?: return emptyList()
    return section.entries.mapIndexedNotNull { index, entry ->
        if ((entry.x + entry.y) % 2 == 0) {
            null
        } else {
            ValidationIssue(
                ValidationSeverity.WARNING,
                Civ3SectionIds.CITY,
                index,
                "x/y",
                "x=${entry.x}, y=${entry.y} sum to an odd value; Civ3's isometric tile grid " +
                    "expects x and y to share parity",
            )
        }
    }
}

/**
 * Flags a [CityEntry] whose [CityEntry.hasPalace] disagrees with whether any of its resolved
 * [CityEntry.buildingIds] has [centerOfEmpire] set. Returns no issues if `CITY` or `BLDG` is
 * absent from [file].
 *
 * Every real city's `hasPalace` agrees exactly with whether it has a `centerOfEmpire`-flagged
 * building present (e.g. the literal Palace, or the Forbidden Palace) — confirmed with zero
 * exceptions across the corpus, in either direction.
 */
fun validateCityHasPalaceMatchesCenterOfEmpire(file: Civ3File): List<ValidationIssue> {
    val city = file.sections.filterIsInstance<CitySection>().singleOrNull() ?: return emptyList()
    val bldg = file.sections.filterIsInstance<BldgSection>().singleOrNull() ?: return emptyList()
    return city.entries.mapIndexedNotNull { index, entry ->
        val hasCenter = entry.buildingIds.any { bldg.entries.getOrNull(it)?.centerOfEmpire == true }
        val hasPalace = entry.hasPalace.toInt() != 0
        if (hasPalace == hasCenter) {
            null
        } else {
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.CITY,
                index,
                "hasPalace",
                "hasPalace=$hasPalace but centerOfEmpire building present=$hasCenter; expected to agree",
            )
        }
    }
}

/**
 * Flags a Great Wonder building appearing in more than one [CityEntry.buildingIds], file-wide.
 * Returns no issues if `CITY` or `BLDG` is absent from [file].
 *
 * A Great Wonder is limited to one instance in the entire game; every real official file respects
 * this — confirmed with zero exceptions (the one real violation found in the corpus, a Great
 * Wonder present in 3 cities, is a non-official mod).
 */
fun validateCityGreatWonderUniqueGlobally(file: Civ3File): List<ValidationIssue> {
    val city = file.sections.filterIsInstance<CitySection>().singleOrNull() ?: return emptyList()
    val bldg = file.sections.filterIsInstance<BldgSection>().singleOrNull() ?: return emptyList()
    val citiesByBuildingId = mutableMapOf<Int, MutableList<Int>>()
    city.entries.forEachIndexed { index, entry ->
        entry.buildingIds.forEach { id ->
            if (bldg.entries.getOrNull(id)?.wonder == true) {
                citiesByBuildingId.getOrPut(id) { mutableListOf() }.add(index)
            }
        }
    }
    return citiesByBuildingId.filterValues { it.size > 1 }.map { (id, cities) ->
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.CITY,
            null,
            "buildingIds",
            "Great Wonder ${bldg.entries[id].name} (BLDG index $id) appears in more than one city: $cities",
        )
    }
}

/**
 * Flags a Small Wonder building appearing in more than one [CityEntry.buildingIds] owned by the
 * same nation. Returns no issues if `CITY` or `BLDG` is absent from [file].
 *
 * A Small Wonder is limited to one instance per nation; every real file respects this — confirmed
 * with zero exceptions, checked against both civilization- and player-owned cities.
 */
fun validateCitySmallWonderUniquePerNation(file: Civ3File): List<ValidationIssue> {
    val city = file.sections.filterIsInstance<CitySection>().singleOrNull() ?: return emptyList()
    val bldg = file.sections.filterIsInstance<BldgSection>().singleOrNull() ?: return emptyList()
    val citiesByBuildingIdAndOwner = mutableMapOf<Triple<Int, Int, Int>, MutableList<Int>>()
    city.entries.forEachIndexed { index, entry ->
        if (entry.ownerType != 2 && entry.ownerType != 3) return@forEachIndexed
        entry.buildingIds.forEach { id ->
            if (bldg.entries.getOrNull(id)?.smallWonder == true) {
                citiesByBuildingIdAndOwner.getOrPut(Triple(id, entry.ownerType, entry.owner)) { mutableListOf() }.add(index)
            }
        }
    }
    return citiesByBuildingIdAndOwner.filterValues { it.size > 1 }.map { (key, cities) ->
        val (id, ownerType, owner) = key
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.CITY,
            null,
            "buildingIds",
            "Small Wonder ${bldg.entries[id].name} (BLDG index $id) appears in more than one city " +
                "owned by ownerType=$ownerType/owner=$owner: $cities",
        )
    }
}

/**
 * Flags a [CityEntry] whose [CityEntry.ownerType] falls outside the documented `0..3` range.
 * Returns no issues if the `CITY` section is absent from [file].
 *
 * The real Rules/Scenario editor only ever produces `ownerType` 0-3; every real file's cities are
 * `ownerType` 2 or 3.
 */
fun validateCityOwnerTypeRecognized(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<CitySection>().singleOrNull() ?: return emptyList()
    return section.entries.mapIndexedNotNull { index, entry ->
        if (entry.ownerType in 0..3) return@mapIndexedNotNull null
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.CITY,
            index,
            "ownerType",
            "ownerType=${entry.ownerType} is not a recognized value (0..3)",
        )
    }
}
