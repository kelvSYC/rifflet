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
    ValidationRule { file -> validateClearForestExclusiveToForest(file) },
    ValidationRule { file -> validateWsizCardinality(file) },
    ValidationRule { file -> validateWmapCardinality(file) },
    ValidationRule { file -> validateExprCardinality(file) },
    ValidationRule { file -> validateErasCardinality(file) },
    ValidationRule { file -> validateDiffCardinality(file) },
    ValidationRule { file -> validateTileCardinality(file) },
    ValidationRule { file -> validateCityCoordinateParity(file) },
    ValidationRule { file -> validateClnyCoordinateParity(file) },
    ValidationRule { file -> validateSlocCoordinateParity(file) },
    ValidationRule { file -> validateUnitCoordinateParity(file) },
    ValidationRule { file -> validateCityTileBackReference(file) },
    ValidationRule { file -> validateClnyTileBackReference(file) },
    ValidationRule { file -> validateCtznDefaultCount(file) },
    ValidationRule { file -> validateCtznDefaultPrerequisite(file) },
    ValidationRule { file -> validateBldgSpaceshipPartBounds(file) },
    ValidationRule { file -> validateGovtCorruption(file) },
    ValidationRule { file -> validatePrtoDomain(file) },
    ValidationRule { file -> validatePrtoArmyStrategyConsistency(file) },
    ValidationRule { file -> validatePrtoKingStrategyConsistency(file) },
    ValidationRule { file -> validatePrtoOtherStrategyBounds(file) },
    ValidationRule { file -> validatePrtoAvailableToBounds(file) },
    ValidationRule { file -> validatePrtoLandStrategyPrerequisites(file) },
    ValidationRule { file -> validatePrtoSeaStrategyPrerequisites(file) },
    ValidationRule { file -> validatePrtoAirStrategyPrerequisites(file) },
    ValidationRule { file -> validateTerrCardinality(file) },
    ValidationRule { file -> validateTfrmCardinality(file) },
    ValidationRule { file -> validateCitySizeLevelThresholds(file) },
    ValidationRule { file -> validateBarbarianUnitDomains(file) },
    ValidationRule { file -> validateBldgSingleCenterOfEmpire(file) },
    ValidationRule { file -> validateBldgImprovementHasNoWonderEffects(file) },
    ValidationRule { file -> validateBldgSmallWonderHasNoHappinessReference(file) },
    ValidationRule { file -> validateBldgSpaceshipPartInvariants(file) },
    ValidationRule { file -> validateBldgSpaceshipPartConventionalStats(file) },
    ValidationRule { file -> validateGovtDefaultCardinality(file) },
    ValidationRule { file -> validateGovtTransitionCardinality(file) },
    ValidationRule { file -> validateGovtDefaultHasNoPrerequisite(file) },
    ValidationRule { file -> validateGovtTransitionHasNoPrerequisite(file) },
    ValidationRule { file -> validateRaceBarbarianPlaceholder(file) },
    ValidationRule { file -> validateRaceCultureGroup(file) },
    ValidationRule { file -> validateRaceMaxCount(file) },
    ValidationRule { file -> validateRaceMinCount(file) },
)

/**
 * Flags a `WSIZ` section whose entry count isn't exactly 5. Returns no issues if the `WSIZ`
 * section is absent from [file].
 *
 * Every real official Conquests and PTW file has exactly 5 world sizes, matching the Rules
 * Editor's World Sizes tab, which offers only a Rename control — no Add or Delete.
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
 * Flags a `WMAP` section whose entry count isn't exactly 1. Returns no issues if the `WMAP`
 * section is absent from [file].
 *
 * Unlike most sections, `WMAP` has no corresponding Rules/Scenario Editor tab at all — it isn't
 * something a user adds to or deletes from directly, only a byproduct of the map's own generation
 * settings. Every real official file has exactly one `WMAP` entry, matching existing
 * reverse-engineering documentation's own claim that the section never holds more than one.
 */
fun validateWmapCardinality(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<WmapSection>().singleOrNull() ?: return emptyList()
    if (section.entries.size == 1) return emptyList()
    return listOf(
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.WMAP,
            null,
            "entries",
            "WMAP has ${section.entries.size} entries; every real official file has exactly 1",
        ),
    )
}

/**
 * Flags an `EXPR` section whose entry count isn't exactly 4. Returns no issues if the `EXPR`
 * section is absent from [file].
 *
 * Every real official file has exactly 4 combat experience levels, matching the Rules Editor's
 * Combat Experience tab, which offers only a Rename control.
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
 * Flags an `ERAS` section whose entry count isn't exactly 4. Returns no issues if the `ERAS`
 * section is absent from [file].
 *
 * Every real official file has exactly 4 eras, matching the Rules Editor's Eras tab, which offers
 * only a Rename control.
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
 * Flags a `DIFF` section whose entry count doesn't match its format era's baseline: exactly 6 for
 * [Civ3FormatEra.PTW]/[Civ3FormatEra.VANILLA], or at least 8 for [Civ3FormatEra.CONQUESTS].
 * Returns no issues if the `DIFF` section is absent from [file].
 *
 * PTW's Difficulty Levels tab has only a Rename control, and its 6 difficulty levels run
 * Chieftain through Deity. Conquests additionally shipped Demigod and Sid, and its tab adds Add
 * but not Delete, so a scenario may only grow past the baseline, never shrink below it.
 * [Civ3FormatEra.VANILLA] is assumed to match PTW — no real vanilla-era `DIFF` sample was
 * available to check directly.
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
 * Flags a `TILE` section whose entry count doesn't match `width × height / 2` implied by the
 * file's `WMAP` entry — Civ3's isometric internal map storage, per [TileSection]'s own KDoc.
 * Returns no issues if either `WMAP` or `TILE` is absent from [file] (a rules-only or
 * player-data-only export has neither).
 *
 * Every real official Conquests campaign scenario (varied non-square dimensions, e.g. 90×84,
 * 78×140, 62×88) and both vanilla-era root scenarios match this exactly.
 */
fun validateTileCardinality(file: Civ3File): List<ValidationIssue> {
    val wmap = file.sections.filterIsInstance<WmapSection>().singleOrNull()?.entries?.singleOrNull()
        ?: return emptyList()
    val tile = file.sections.filterIsInstance<TileSection>().singleOrNull() ?: return emptyList()
    val expected = wmap.width * wmap.height / 2
    if (tile.entries.size == expected) return emptyList()
    return listOf(
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.TILE,
            null,
            "entries",
            "TILE has ${tile.entries.size} entries; WMAP width=${wmap.width}, height=${wmap.height} " +
                "implies exactly $expected",
        ),
    )
}

/**
 * Flags a [CityEntry] whose `(x, y)` doesn't resolve, via [WmapEntry.tileIndex], to a `TILE`
 * entry whose own [TileEntry.city] back-reference points to that [CityEntry]'s own index. Returns
 * no issues if `WMAP`, `TILE`, or `CITY` is absent from [file].
 *
 * Holds without exception across every placed city in every real official Conquests campaign
 * scenario.
 */
fun validateCityTileBackReference(file: Civ3File): List<ValidationIssue> {
    val wmap = file.sections.filterIsInstance<WmapSection>().singleOrNull()?.entries?.singleOrNull()
        ?: return emptyList()
    val tile = file.sections.filterIsInstance<TileSection>().singleOrNull() ?: return emptyList()
    val city = file.sections.filterIsInstance<CitySection>().singleOrNull() ?: return emptyList()
    return city.entries.mapIndexedNotNull { index, entry ->
        val expectedIndex = wmap.tileIndex(entry.x, entry.y)
        val actual = tile.entries.getOrNull(expectedIndex)?.city?.toInt()
        if (actual == index) {
            null
        } else {
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.CITY,
                index,
                "x/y",
                "CityEntry at (${entry.x}, ${entry.y}) resolves to TILE[$expectedIndex], whose city " +
                    "back-reference is $actual, not $index",
            )
        }
    }
}

/**
 * Flags a [ClnyEntry] whose `(x, y)` doesn't resolve, via [WmapEntry.tileIndex], to a `TILE`
 * entry whose own [TileEntry.colony] back-reference points to that [ClnyEntry]'s own index.
 * Returns no issues if `WMAP`, `TILE`, or `CLNY` is absent from [file].
 *
 * Holds without exception across every placed colony in every real official Conquests campaign
 * scenario.
 */
fun validateClnyTileBackReference(file: Civ3File): List<ValidationIssue> {
    val wmap = file.sections.filterIsInstance<WmapSection>().singleOrNull()?.entries?.singleOrNull()
        ?: return emptyList()
    val tile = file.sections.filterIsInstance<TileSection>().singleOrNull() ?: return emptyList()
    val clny = file.sections.filterIsInstance<ClnySection>().singleOrNull() ?: return emptyList()
    return clny.entries.mapIndexedNotNull { index, entry ->
        val expectedIndex = wmap.tileIndex(entry.x, entry.y)
        val actual = tile.entries.getOrNull(expectedIndex)?.colony?.toInt()
        if (actual == index) {
            null
        } else {
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.CLNY,
                index,
                "x/y",
                "ClnyEntry at (${entry.x}, ${entry.y}) resolves to TILE[$expectedIndex], whose colony " +
                    "back-reference is $actual, not $index",
            )
        }
    }
}

/**
 * Flags a `CTZN` section that doesn't have exactly one entry with `defaultCitizen` set. Returns
 * no issues if the `CTZN` section is absent from [file].
 *
 * Every real official file has exactly one default citizen type, matching the Rules Editor's
 * Citizens tab, which designates exactly one citizen type as default.
 */
fun validateCtznDefaultCount(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<CtznSection>().singleOrNull() ?: return emptyList()
    val count = section.entries.count { it.defaultCitizen != 0 }
    if (count == 1) return emptyList()
    return listOf(
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.CTZN,
            null,
            "defaultCitizen",
            "CTZN has $count entries with defaultCitizen set; exactly 1 is expected",
        ),
    )
}

/**
 * Flags the default `CtznEntry` (the one with `defaultCitizen` set) if it has a `prerequisite`.
 * Returns no issues if the `CTZN` section is absent from [file], or if [file] doesn't have
 * exactly one default entry (see [validateCtznDefaultCount] for that case).
 *
 * The default citizen type needs no prerequisite technology, matching the Rules Editor's
 * Citizens tab, where the Prerequisite dropdown is disabled for the default citizen type.
 */
fun validateCtznDefaultPrerequisite(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<CtznSection>().singleOrNull() ?: return emptyList()
    val (index, entry) = section.entries.withIndex().singleOrNull { (_, e) -> e.defaultCitizen != 0 }
        ?: return emptyList()
    if (entry.prerequisite == -1) return emptyList()
    return listOf(
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.CTZN,
            index,
            "prerequisite",
            "the default citizen type has prerequisite=${entry.prerequisite}; it should need no prerequisite (-1)",
        ),
    )
}

/**
 * Flags a `TERR` section whose entry count doesn't match its format era's fixed count. Returns no
 * issues if the `TERR` section is absent from [file].
 *
 * The Rules Editor's Terrain tab offers only a Rename control in every era — no Add, no Delete —
 * so the count is exactly 12 for [Civ3FormatEra.VANILLA]/[Civ3FormatEra.PTW], or exactly 14 for
 * [Civ3FormatEra.CONQUESTS], with no room to grow past the baseline the way `DIFF` can.
 */
fun validateTerrCardinality(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<TerrSection>().singleOrNull() ?: return emptyList()
    val count = section.entries.size
    val era = file.header.formatEra
    val expected = when (era) {
        Civ3FormatEra.VANILLA, Civ3FormatEra.PTW -> 12
        Civ3FormatEra.CONQUESTS -> 14
    }
    if (count == expected) return emptyList()
    return listOf(
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.TERR,
            null,
            "entries",
            "TERR has $count entries; $era requires exactly $expected",
        ),
    )
}

/**
 * Flags a `TFRM` section whose entry count doesn't match its format era's fixed count. Returns no
 * issues if the `TFRM` section is absent from [file].
 *
 * The Rules Editor's Worker Jobs tab offers only a Rename control in every era — no Add, no
 * Delete — so the count is exactly 9 for [Civ3FormatEra.VANILLA], exactly 12 for
 * [Civ3FormatEra.PTW], or exactly 13 for [Civ3FormatEra.CONQUESTS].
 */
fun validateTfrmCardinality(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<TfrmSection>().singleOrNull() ?: return emptyList()
    val count = section.entries.size
    val era = file.header.formatEra
    val expected = when (era) {
        Civ3FormatEra.VANILLA -> 9
        Civ3FormatEra.PTW -> 12
        Civ3FormatEra.CONQUESTS -> 13
    }
    if (count == expected) return emptyList()
    return listOf(
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.TFRM,
            null,
            "entries",
            "TFRM has $count entries; $era requires exactly $expected",
        ),
    )
}

/**
 * Flags a [BldgEntry] whose [BldgEntry.spaceshipPart] isn't `-1`, isn't a valid index into
 * [RuleEntry.spaceshipPartQuantities], or duplicates another [BldgEntry]'s [BldgEntry.spaceshipPart].
 * Returns no issues if `BLDG` or `RULE` is absent from [file].
 *
 * Every real official file with spaceship-part-producing buildings assigns each of its declared
 * parts to exactly one distinct building, never out of bounds.
 */
fun validateBldgSpaceshipPartBounds(file: Civ3File): List<ValidationIssue> {
    val bldg = file.sections.filterIsInstance<BldgSection>().singleOrNull() ?: return emptyList()
    val rule = file.sections.filterIsInstance<RuleSection>().singleOrNull()?.entries?.singleOrNull()
        ?: return emptyList()
    val count = rule.spaceshipPartQuantities.size
    val seen = mutableSetOf<Int>()
    return bldg.entries.mapIndexedNotNull { index, entry ->
        val part = entry.spaceshipPart
        when {
            part == -1 -> null
            part !in 0 until count -> ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.BLDG,
                index,
                "spaceshipPart",
                "spaceshipPart=$part is not -1 and not a valid RULE spaceshipPartQuantities index (0..<$count)",
            )
            !seen.add(part) -> ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.BLDG,
                index,
                "spaceshipPart",
                "spaceshipPart=$part is already assigned to an earlier BLDG entry",
            )
            else -> null
        }
    }
}

/**
 * Checks this file against every editor-confirmed constraint this library knows about. Never
 * throws; a rule whose required section(s) are absent from [Civ3File.sections] simply
 * contributes no issues.
 */
fun Civ3File.validate(): List<ValidationIssue> = civ3ValidationRules.flatMap { it.validate(this) }
