package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity

/**
 * Flags a `LEAD` section whose entry count doesn't match `WMAP`'s declared civ count. Returns no
 * issues if either the `LEAD` or `WMAP` section is absent from [file] — `LEAD` can exist with
 * `WMAP` entirely absent.
 *
 * Corpus-confirmed exact match in every real file containing both sections: the Player
 * Properties dialogue's slot count always matches the world map's declared civ count.
 */
fun validateLeadCountMatchesWmapNumberOfCivs(file: Civ3File): List<ValidationIssue> {
    val leadSection = file.sections.filterIsInstance<LeadSection>().singleOrNull() ?: return emptyList()
    val wmapSection = file.sections.filterIsInstance<WmapSection>().singleOrNull() ?: return emptyList()
    val numberOfCivs = wmapSection.entries.firstOrNull()?.numberOfCivs ?: return emptyList()
    if (leadSection.entries.size == numberOfCivs) return emptyList()
    return listOf(
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.LEAD,
            null,
            "entries",
            "LEAD has ${leadSection.entries.size} entries; WMAP declares $numberOfCivs civs",
        ),
    )
}
