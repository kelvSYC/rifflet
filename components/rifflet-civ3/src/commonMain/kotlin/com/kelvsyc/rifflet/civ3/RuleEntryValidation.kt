package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity

/**
 * Flags a [RuleEntry] whose [RuleEntry.maximumLevel2CitySize] isn't strictly greater than
 * [RuleEntry.maximumLevel1CitySize]. Returns no issues if the `RULE` section is absent from
 * [file].
 *
 * If the Level 2 threshold doesn't exceed the Level 1 one, a city can't grow past Level 2 before
 * it would already have reached Level 1, so buildings gated on reaching City Size Level 2 (e.g.
 * Hospital) may never become buildable. [RuleEntry.maximumLevel1CitySize] of `0` is a legitimate
 * value on its own — it just means new cities start at Level 2 directly — only the ordering
 * between the two thresholds is invalid.
 */
fun validateCitySizeLevelThresholds(file: Civ3File): List<ValidationIssue> {
    val entry = file.sections.filterIsInstance<RuleSection>().singleOrNull()?.entries?.singleOrNull()
        ?: return emptyList()
    if (entry.citySizeLevels.maximumLevel2CitySize > entry.citySizeLevels.maximumLevel1CitySize) return emptyList()
    return listOf(
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.RULE,
            0,
            "maximumLevel2CitySize",
            "maximumLevel2CitySize=${entry.citySizeLevels.maximumLevel2CitySize} must be strictly greater than " +
                "maximumLevel1CitySize=${entry.citySizeLevels.maximumLevel1CitySize}, or a city cannot grow past the " +
                "Level 2 threshold before reaching the Level 1 one, and Level-2-gated buildings " +
                "(e.g. Hospital) may never become buildable",
        ),
    )
}

/**
 * Flags [RuleEntry.basicBarbarianUnitType]/[RuleEntry.advancedBarbarianUnitType] if either
 * resolves to a [PrtoEntry] that isn't [PrtoDomain.LAND], or [RuleEntry.barbarianSeaUnitType] if
 * it doesn't resolve to [PrtoDomain.SEA]. Returns no issues for a field set to `-1` (no unit
 * configured) or one that doesn't resolve to any `PRTO` entry, and no issues if `RULE` or `PRTO`
 * is absent from [file].
 *
 * Basic and advanced barbarians raid by land; the sea barbarian raids by sea. Every real
 * vanilla/PTW/Conquests file matches this, except one real Conquests scenario that leaves
 * `barbarianSeaUnitType` unset entirely rather than assigning it a mismatched unit.
 */
fun validateBarbarianUnitDomains(file: Civ3File): List<ValidationIssue> {
    val entry = file.sections.filterIsInstance<RuleSection>().singleOrNull()?.entries?.singleOrNull()
        ?: return emptyList()
    val prtos = file.sections.filterIsInstance<PrtoSection>().singleOrNull()?.entries ?: return emptyList()

    fun check(field: String, index: Int, expected: PrtoDomain): ValidationIssue? {
        if (index == -1) return null
        val unit = prtos.getOrNull(index) ?: return null
        if (unit.domainEnum == expected) return null
        val expectedName = expected.name.lowercase().replaceFirstChar { it.uppercase() }
        return ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.RULE,
            0,
            field,
            "$field=$index (${unit.name}) has type=${unit.type}, expected a $expectedName unit " +
                "(type=${expected.ordinal})",
        )
    }

    return listOfNotNull(
        check("basicBarbarianUnitType", entry.defaultUnits.basicBarbarianUnitType, PrtoDomain.LAND),
        check("advancedBarbarianUnitType", entry.defaultUnits.advancedBarbarianUnitType, PrtoDomain.LAND),
        check("barbarianSeaUnitType", entry.defaultUnits.barbarianSeaUnitType, PrtoDomain.SEA),
    )
}
