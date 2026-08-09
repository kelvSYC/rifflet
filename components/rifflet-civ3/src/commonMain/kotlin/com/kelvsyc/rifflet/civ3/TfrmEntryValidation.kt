package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity

/**
 * Flags a [TfrmEntry] whose [TfrmEntry.requiredResource1]/[TfrmEntry.requiredResource2] resolves
 * to a Bonus-type `GOOD` entry. Returns no issues if the `GOOD` or `TFRM` section is absent from
 * [file].
 *
 * The Worker Jobs page's Required Resource dropdowns exclude Bonus resources — every real
 * vanilla, PTW, and Conquests ruleset respects this.
 */
fun validateTfrmRequiredResourceNotBonus(file: Civ3File): List<ValidationIssue> {
    val goods = file.sections.filterIsInstance<GoodSection>().singleOrNull()?.entries ?: return emptyList()
    val section = file.sections.filterIsInstance<TfrmSection>().singleOrNull() ?: return emptyList()
    return section.entries.mapIndexedNotNull { index, entry ->
        val bonusFields = listOfNotNull(
            "requiredResource1".takeIf {
                goods.getOrNull(entry.requiredResource1)?.type == GoodResourceType.BONUS
            },
            "requiredResource2".takeIf {
                goods.getOrNull(entry.requiredResource2)?.type == GoodResourceType.BONUS
            },
        )
        if (bonusFields.isEmpty()) return@mapIndexedNotNull null
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.TFRM,
            index,
            bonusFields.joinToString(", "),
            "a required resource must be Luxury or Strategic, not Bonus (${bonusFields.joinToString(", ")})",
        )
    }
}
