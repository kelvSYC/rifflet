package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity

/**
 * Flags a [GoodEntry] of type [GoodResourceType.BONUS] whose [GoodEntry.appearanceRatio] or
 * [GoodEntry.disappearanceProbability] isn't `0`. Returns no issues if the `GOOD` section is
 * absent from [file].
 *
 * The Rules Editor disables both fields when Bonus Resource is selected, so a Bonus Resource's
 * appearance/disappearance chance is always zero. Luxury and Strategic entries have both fields
 * enabled and may legitimately be `0`, so no equivalent constraint applies to them.
 */
fun validateGoodBonusResourceDisabledFields(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<GoodSection>().singleOrNull() ?: return emptyList()
    return section.entries.mapIndexedNotNull { index, entry ->
        if (entry.type != GoodResourceType.BONUS) return@mapIndexedNotNull null
        if (entry.appearanceRatio == 0 && entry.disappearanceProbability == 0) return@mapIndexedNotNull null
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.GOOD,
            index,
            "appearanceRatio/disappearanceProbability",
            "a Bonus Resource is expected to have both fields disabled (appearanceRatio=0, " +
                "disappearanceProbability=0), was appearanceRatio=${entry.appearanceRatio}, " +
                "disappearanceProbability=${entry.disappearanceProbability}",
        )
    }
}
