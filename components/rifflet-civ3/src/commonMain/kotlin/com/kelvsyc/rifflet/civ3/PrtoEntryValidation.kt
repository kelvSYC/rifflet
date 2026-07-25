package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity

/**
 * Flags a [PrtoEntry] whose [PrtoEntry.type] doesn't decode into a [PrtoDomain]. Returns no
 * issues if the `PRTO` section is absent from [file].
 *
 * Every real official file's units have a `type` value in the documented 0-2 range, with zero
 * exceptions across every era and every degree of `PRTO` pruning observed.
 */
fun validatePrtoDomain(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<PrtoSection>().singleOrNull() ?: return emptyList()
    return section.entries.mapIndexedNotNull { index, entry ->
        if (entry.domainEnum != null) {
            null
        } else {
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.PRTO,
                index,
                "type",
                "type=${entry.type} is not a valid PrtoDomain index (0..2)",
            )
        }
    }
}
