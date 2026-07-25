package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity

/**
 * Flags a [GovtEntry] whose [GovtEntry.corruption] doesn't decode into a [GovtCorruption], or
 * whose era never offers [GovtCorruption.OFF] as an option but resolves to it anyway. Returns no
 * issues if the `GOVT` section is absent from [file].
 *
 * Every real official file's governments have a `corruption` value in the documented 0-6 range,
 * with zero exceptions across every era and every degree of `GOVT` pruning observed.
 * [GovtCorruption.OFF] is [Civ3FormatEra.CONQUESTS]-only — see that constant's own KDoc — so a
 * [Civ3FormatEra.VANILLA]/[Civ3FormatEra.PTW] file resolving to it is flagged too, even though `6`
 * is otherwise a structurally valid [GovtCorruption] ordinal.
 */
fun validateGovtCorruption(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<GovtSection>().singleOrNull() ?: return emptyList()
    val era = file.header.formatEra
    return section.entries.mapIndexedNotNull { index, entry ->
        val corruption = entry.corruptionEnum
        when {
            corruption == null -> ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.GOVT,
                index,
                "corruption",
                "corruption=${entry.corruption} is not a valid GovtCorruption index (0..6)",
            )
            corruption == GovtCorruption.OFF && era != Civ3FormatEra.CONQUESTS -> ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.GOVT,
                index,
                "corruption",
                "corruption=6 (OFF) is Conquests-only; $era's Rules Editor never offers it",
            )
            else -> null
        }
    }
}
