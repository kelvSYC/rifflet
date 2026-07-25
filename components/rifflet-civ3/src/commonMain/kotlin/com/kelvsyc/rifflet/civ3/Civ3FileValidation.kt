package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationRule
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity

/**
 * Every registered [ValidationRule], applied by [Civ3File.validate]. Grows by one entry per
 * confirmed rule; not a generic registry/auto-discovery mechanism — see the design spec for why
 * that's deferred.
 */
private val civ3ValidationRules: List<ValidationRule> = listOf(
    ValidationRule { file -> validatePollutionEffect(file) },
    ValidationRule { file -> validateWsizCardinality(file) },
    ValidationRule { file -> validateExprCardinality(file) },
    ValidationRule { file -> validateErasCardinality(file) },
    ValidationRule { file -> validateDiffCardinality(file) },
)

/**
 * Flags a `WSIZ` section whose entry count isn't exactly 5. Confirmed against every real
 * official sample available (all 21 shipped Conquests scenarios, both the Conquests and PTW
 * base rulesets, and the vanilla-era root scenarios): every one has exactly 5 world sizes, and
 * the Rules Editor's World Sizes tab offers only a Rename control, no Add or Delete. Returns no
 * issues if the `WSIZ` section is absent from [file].
 */
fun validateWsizCardinality(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<WsizSection>().singleOrNull() ?: return emptyList()
    if (section.entries.size == 5) return emptyList()
    return listOf(
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.WSIZ,
            null,
            "entries",
            "WSIZ has ${section.entries.size} entries; the Rules Editor always produces exactly 5",
        ),
    )
}

/**
 * Flags an `EXPR` section whose entry count isn't exactly 4. Confirmed against the same real
 * official sample set as [validateWsizCardinality]: every file has exactly 4 combat experience
 * levels, and the Rules Editor's Combat Experience tab offers only a Rename control. Returns no
 * issues if the `EXPR` section is absent from [file].
 */
fun validateExprCardinality(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<ExprSection>().singleOrNull() ?: return emptyList()
    if (section.entries.size == 4) return emptyList()
    return listOf(
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.EXPR,
            null,
            "entries",
            "EXPR has ${section.entries.size} entries; the Rules Editor always produces exactly 4",
        ),
    )
}

/**
 * Flags an `ERAS` section whose entry count isn't exactly 4. Confirmed against the same real
 * official sample set as [validateWsizCardinality]: every file has exactly 4 eras, and the Rules
 * Editor's Eras tab offers only a Rename control. Returns no issues if the `ERAS` section is
 * absent from [file].
 */
fun validateErasCardinality(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<ErasSection>().singleOrNull() ?: return emptyList()
    if (section.entries.size == 4) return emptyList()
    return listOf(
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.ERAS,
            null,
            "entries",
            "ERAS has ${section.entries.size} entries; the Rules Editor always produces exactly 4",
        ),
    )
}

/**
 * Flags a `DIFF` section whose entry count doesn't match its format era's baseline. Confirmed
 * against every real official sample available: exactly 6 for [Civ3FormatEra.PTW] (Chieftain
 * through Deity; PTW's Difficulty Levels tab offers only Rename, no Add) or at least 8 for
 * [Civ3FormatEra.CONQUESTS] (Conquests additionally shipped Demigod and Sid; its Difficulty
 * Levels tab offers Rename and Add but no Delete, so a scenario may only grow past the baseline,
 * never shrink below it). [Civ3FormatEra.VANILLA] is assumed to match PTW here — no real
 * vanilla-era sample with a `DIFF` section was available to confirm this directly. Returns no
 * issues if the `DIFF` section is absent from [file].
 */
fun validateDiffCardinality(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<DiffSection>().singleOrNull() ?: return emptyList()
    val count = section.entries.size
    val era = file.header.formatEra
    val valid = when (era) {
        Civ3FormatEra.CONQUESTS -> count >= 8
        Civ3FormatEra.VANILLA, Civ3FormatEra.PTW -> count == 6
    }
    if (valid) return emptyList()
    val requirement = if (era == Civ3FormatEra.CONQUESTS) "at least 8" else "exactly 6"
    return listOf(
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.DIFF,
            null,
            "entries",
            "DIFF has $count entries; $era requires $requirement",
        ),
    )
}

/**
 * Checks this file against every editor-confirmed constraint this library knows about. Never
 * throws; a rule whose required section(s) are absent from [Civ3File.sections] simply
 * contributes no issues.
 */
fun Civ3File.validate(): List<ValidationIssue> = civ3ValidationRules.flatMap { it.validate(this) }
