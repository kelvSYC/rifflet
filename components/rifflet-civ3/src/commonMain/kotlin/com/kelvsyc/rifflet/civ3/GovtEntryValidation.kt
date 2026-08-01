package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity

/**
 * Flags a [GovtEntry] whose era never offers [GovtCorruption.OFF] as an option but resolves to it
 * anyway. Returns no issues if the `GOVT` section is absent from [file].
 *
 * [GovtCorruption.OFF] is [Civ3FormatEra.CONQUESTS]-only — see that constant's own KDoc — so a
 * [Civ3FormatEra.VANILLA]/[Civ3FormatEra.PTW] file resolving to it is flagged.
 */
fun validateGovtCorruption(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<GovtSection>().singleOrNull() ?: return emptyList()
    val era = file.header.formatEra
    return section.entries.mapIndexedNotNull { index, entry ->
        if (entry.corruption == GovtCorruption.OFF && era != Civ3FormatEra.CONQUESTS) {
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.GOVT,
                index,
                "corruption",
                "corruption=6 (OFF) is Conquests-only; $era's Rules Editor never offers it",
            )
        } else {
            null
        }
    }
}

/**
 * Flags a `GOVT` section with more than one [GovtEntry.defaultType] entry (ERROR), or none at all
 * (WARNING). Returns no issues if the `GOVT` section is absent from [file].
 *
 * Every real official file has at most one Default government (Despotism), with zero exceptions.
 * A small number of real multiplayer scenarios have none at all (Despotism removed entirely), so
 * a missing Default is only a [ValidationSeverity.WARNING], not an [ValidationSeverity.ERROR].
 */
fun validateGovtDefaultCardinality(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<GovtSection>().singleOrNull() ?: return emptyList()
    val count = section.entries.count { it.defaultType != 0 }
    return when {
        count > 1 -> listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.GOVT,
                null,
                "defaultType",
                "$count entries have defaultType set; at most one is expected",
            ),
        )
        count == 0 -> listOf(
            ValidationIssue(
                ValidationSeverity.WARNING,
                Civ3SectionIds.GOVT,
                null,
                "defaultType",
                "no entry has defaultType set; a Default government is usually expected",
            ),
        )
        else -> emptyList()
    }
}

/**
 * Flags a `GOVT` section without exactly one [GovtEntry.transitionType] entry. Returns no issues
 * if the `GOVT` section is absent from [file].
 *
 * Every real official file has exactly one Transition government (Anarchy), with zero exceptions
 * — even the handful of real multiplayer scenarios missing a Default government still carry one.
 */
fun validateGovtTransitionCardinality(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<GovtSection>().singleOrNull() ?: return emptyList()
    val count = section.entries.count { it.transitionType != 0 }
    if (count == 1) return emptyList()
    return listOf(
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.GOVT,
            null,
            "transitionType",
            "$count entries have transitionType set; exactly one is expected",
        ),
    )
}

/**
 * Flags a [GovtEntry.defaultType] entry whose [GovtEntry.prerequisiteTechnology] isn't `-1`.
 * Returns no issues if the `GOVT` section is absent from [file].
 *
 * Every real official file's Default government has no prerequisite technology, matching the
 * Governments editor's own behavior of hiding the Prerequisite dropdown for it.
 */
fun validateGovtDefaultHasNoPrerequisite(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<GovtSection>().singleOrNull() ?: return emptyList()
    return section.entries.mapIndexedNotNull { index, entry ->
        if (entry.defaultType == 0 || entry.prerequisiteTechnology == -1) {
            null
        } else {
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.GOVT,
                index,
                "prerequisiteTechnology",
                "defaultType is set but prerequisiteTechnology=${entry.prerequisiteTechnology}; -1 is expected",
            )
        }
    }
}

/**
 * Flags a [GovtEntry.transitionType] entry whose [GovtEntry.prerequisiteTechnology] isn't `-1`.
 * Returns no issues if the `GOVT` section is absent from [file].
 *
 * Every real official file's Transition government has no prerequisite technology, matching the
 * Governments editor's own behavior of hiding the Prerequisite dropdown for it.
 */
fun validateGovtTransitionHasNoPrerequisite(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<GovtSection>().singleOrNull() ?: return emptyList()
    return section.entries.mapIndexedNotNull { index, entry ->
        if (entry.transitionType == 0 || entry.prerequisiteTechnology == -1) {
            null
        } else {
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.GOVT,
                index,
                "prerequisiteTechnology",
                "transitionType is set but prerequisiteTechnology=${entry.prerequisiteTechnology}; -1 is expected",
            )
        }
    }
}
