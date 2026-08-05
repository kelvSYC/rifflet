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
    ValidationRule { file -> validateWorldSizeResolves(file) },
    ValidationRule { file -> validateExprCardinality(file) },
    ValidationRule { file -> validateErasCardinality(file) },
    ValidationRule { file -> validateDiffCardinality(file) },
    ValidationRule { file -> validateTileCardinality(file) },
    ValidationRule { file -> validateCityCoordinateParity(file) },
    ValidationRule { file -> validateClnyCoordinateParity(file) },
    ValidationRule { file -> validateSlocCoordinateParity(file) },
    ValidationRule { file -> validateUnitCoordinateParity(file) },
    ValidationRule { file -> validateCityTileBackReference(file) },
    ValidationRule { file -> validateCityTerrainAllowsCities(file) },
    ValidationRule { file -> validateColonyTerrainAllowsImprovementType(file) },
    ValidationRule { file -> validateFortressTerrainAllowsForts(file) },
    ValidationRule { file -> validateUnitNotOnImpassableTerrain(file) },
    ValidationRule { file -> validateWheeledUnitNotOnImpassableByWheeledTerrain(file) },
    ValidationRule { file -> validateLandNotDirectlyAdjacentToSeaOrOcean(file) },
    ValidationRule { file -> validateClnyTileBackReference(file) },
    ValidationRule { file -> validateCtznDefaultCount(file) },
    ValidationRule { file -> validateCtznDefaultPrerequisite(file) },
    ValidationRule { file -> validateBldgSpaceshipPartBounds(file) },
    ValidationRule { file -> validateGovtCorruption(file) },
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
    ValidationRule { file -> validateRaceMaxCount(file) },
    ValidationRule { file -> validateRaceMinCount(file) },
    ValidationRule { file -> validateGoodBonusResourceDisabledFields(file) },
    ValidationRule { file -> validateCuredBySanitationRequiresCausesDisease(file) },
    ValidationRule { file -> validateLandmarkEnabledOnlyOnSupportedTerrainTypes(file) },
    ValidationRule { file -> validateTileContinentResolves(file) },
    ValidationRule { file -> validateContCardinality(file) },
    ValidationRule { file -> validateContTypeNotMixed(file) },
    ValidationRule { file -> validateAdjacentLandTilesShareContinent(file) },
    ValidationRule { file -> validateContinentContiguous(file) },
    ValidationRule { file -> validateWaterContinentTouchesLand(file) },
    ValidationRule { file -> validateTechPrerequisitesSameEra(file) },
    ValidationRule { file -> validateTechPrerequisitesAcyclic(file) },
    ValidationRule { file -> validateBldgGainInEveryCityNotWonder(file) },
    ValidationRule { file -> validateBldgGainInEveryCityOnContinentNotWonder(file) },
    ValidationRule { file -> validateBldgRequiredBuildingAcyclic(file) },
    ValidationRule { file -> validateBldgWonderEffectsAcyclic(file) },
    ValidationRule { file -> validateBldgNotBothWonderAndSmallWonder(file) },
    ValidationRule { file -> validatePrtoUpgradeToAcyclic(file) },
    ValidationRule { file -> validatePrtoEnslaveResultsRequiresEnslave(file) },
    ValidationRule { file -> validatePrtoAiStrategiesSingleBit(file) },
    ValidationRule { file -> validateCityHasPalaceMatchesCenterOfEmpire(file) },
    ValidationRule { file -> validateCityGreatWonderUniqueGlobally(file) },
    ValidationRule { file -> validateCitySmallWonderUniquePerNation(file) },
    ValidationRule { file -> validateCityOwnerTypeRecognized(file) },
    ValidationRule { file -> validateSlocOwnerTypeRecognized(file) },
    ValidationRule { file -> validateSlocOwnerNotBarbarian(file) },
    ValidationRule { file -> validateSlocUniqueOwner(file) },
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
 * Flags a [WchrEntry.worldSize] outside `0..<5` (WARNING, not ERROR). Returns no issues if `WCHR`
 * is absent from [file].
 *
 * `5` is the confirmed size of a real `WSIZ` section (see [validateWsizCardinality]) — checked
 * directly, not by resolving against [file]'s own `WSIZ` section (via [WchrEntry.worldSizeWsiz]),
 * since a bare map export can omit `WSIZ` entirely and still be built on a default ruleset whose
 * own `WSIZ` has the usual 5 entries. A small number of real files (bare map exports with no
 * `WSIZ` section of their own) have an out-of-range value here — see [WchrEntry.worldSize]'s own
 * KDoc for what's actually confirmed about it. That's real, if unusual, so this is only a
 * [ValidationSeverity.WARNING].
 */
fun validateWorldSizeResolves(file: Civ3File): List<ValidationIssue> {
    val wchr = file.sections.filterIsInstance<WchrSection>().singleOrNull() ?: return emptyList()
    return wchr.entries.mapIndexedNotNull { index, entry ->
        if (entry.worldSize in 0 until 5) return@mapIndexedNotNull null
        ValidationIssue(
            ValidationSeverity.WARNING,
            Civ3SectionIds.WCHR,
            index,
            "worldSize",
            "WCHR[$index] has worldSize=${entry.worldSize}, which is not a valid WSIZ index (0..<5)",
        )
    }
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
 * [Civ3FormatEra.VANILLA] matches PTW's baseline of 6.
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
 * Flags a [CityEntry] whose tile's base terrain type disallows cities
 * ([TerrAllowances.allowCities] is `0`). Returns no issues if `WMAP`, `TERR`, `TILE`, or `CITY`
 * is absent from [file].
 */
fun validateCityTerrainAllowsCities(file: Civ3File): List<ValidationIssue> {
    val wmap = file.sections.filterIsInstance<WmapSection>().singleOrNull()?.entries?.singleOrNull()
        ?: return emptyList()
    val terr = file.sections.filterIsInstance<TerrSection>().singleOrNull() ?: return emptyList()
    val tile = file.sections.filterIsInstance<TileSection>().singleOrNull() ?: return emptyList()
    val city = file.sections.filterIsInstance<CitySection>().singleOrNull() ?: return emptyList()
    val era = file.header.formatEra
    return city.entries.mapIndexedNotNull { index, entry ->
        val tileEntry = tile.entries.getOrNull(wmap.tileIndex(entry.x, entry.y)) ?: return@mapIndexedNotNull null
        val terrEntry = tileEntry.baseTerrain(terr.entries, era) ?: return@mapIndexedNotNull null
        if (terrEntry.allowances.allowCities.toInt() != 0) {
            null
        } else {
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.CITY,
                index,
                "x/y",
                "CityEntry at (${entry.x}, ${entry.y}) sits on ${terrEntry.name} terrain, which disallows cities",
            )
        }
    }
}

/**
 * Flags a [ClnyEntry] whose tile's base terrain type disallows its own [ClnyEntry.improvementType]
 * (e.g. an `AIRFIELD` colony sitting on terrain whose [TerrAllowances.allowAirfields] is `0`).
 * Returns no issues if `WMAP`, `TERR`, `TILE`, or `CLNY` is absent from [file], or for a
 * [Civ3FormatEra.VANILLA]/[Civ3FormatEra.PTW] file whose terrain predates the relevant allowance
 * field entirely.
 */
fun validateColonyTerrainAllowsImprovementType(file: Civ3File): List<ValidationIssue> {
    val wmap = file.sections.filterIsInstance<WmapSection>().singleOrNull()?.entries?.singleOrNull()
        ?: return emptyList()
    val terr = file.sections.filterIsInstance<TerrSection>().singleOrNull() ?: return emptyList()
    val tile = file.sections.filterIsInstance<TileSection>().singleOrNull() ?: return emptyList()
    val clny = file.sections.filterIsInstance<ClnySection>().singleOrNull() ?: return emptyList()
    val era = file.header.formatEra
    return clny.entries.mapIndexedNotNull { index, entry ->
        val tileEntry = tile.entries.getOrNull(wmap.tileIndex(entry.x, entry.y)) ?: return@mapIndexedNotNull null
        val terrEntry = tileEntry.baseTerrain(terr.entries, era) ?: return@mapIndexedNotNull null
        val allowed = when (entry.improvementType) {
            ClnyImprovementType.COLONY -> terrEntry.allowances.allowColonies
            ClnyImprovementType.AIRFIELD -> terrEntry.allowances.allowAirfields
            ClnyImprovementType.RADAR_TOWER -> terrEntry.allowances.allowRadarTowers
            ClnyImprovementType.OUTPOST -> terrEntry.allowances.allowOutposts
        }
        if (allowed == null || allowed.toInt() != 0) {
            null
        } else {
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.CLNY,
                index,
                "x/y",
                "ClnyEntry (${entry.improvementType}) at (${entry.x}, ${entry.y}) sits on ${terrEntry.name} " +
                    "terrain, which disallows it",
            )
        }
    }
}

/**
 * Flags a [TileEntry.fortress] (resolved for the file's era — see [TileEntry.fortress]'s
 * era-resolved overload) built on a tile whose base terrain type disallows forts
 * ([TerrAllowances.allowForts] is `0`). Returns no issues if `TERR` or `TILE` is absent from
 * [file], or for a [Civ3FormatEra.VANILLA]/[Civ3FormatEra.PTW] file, which predates the
 * `allowForts` field entirely.
 */
fun validateFortressTerrainAllowsForts(file: Civ3File): List<ValidationIssue> {
    val terr = file.sections.filterIsInstance<TerrSection>().singleOrNull() ?: return emptyList()
    val tile = file.sections.filterIsInstance<TileSection>().singleOrNull() ?: return emptyList()
    val era = file.header.formatEra
    return tile.entries.mapIndexedNotNull { index, entry ->
        if (!entry.fortress(era)) return@mapIndexedNotNull null
        val terrEntry = entry.baseTerrain(terr.entries, era) ?: return@mapIndexedNotNull null
        val allowed = terrEntry.allowances.allowForts
        if (allowed == null || allowed.toInt() != 0) {
            null
        } else {
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TILE,
                index,
                "fortress",
                "TILE[$index] has a Fortress built, but sits on ${terrEntry.name} terrain, which disallows Forts",
            )
        }
    }
}

/**
 * Flags a [UnitEntry] sitting on a tile whose base terrain type is Impassable
 * ([TerrAllowances.impassable] is nonzero). Returns no issues if `WMAP`, `TERR`, `TILE`, or `UNIT`
 * is absent from [file], or for a [Civ3FormatEra.VANILLA]/[Civ3FormatEra.PTW] file, which
 * predates the `impassable` field entirely.
 */
fun validateUnitNotOnImpassableTerrain(file: Civ3File): List<ValidationIssue> {
    val wmap = file.sections.filterIsInstance<WmapSection>().singleOrNull()?.entries?.singleOrNull()
        ?: return emptyList()
    val terr = file.sections.filterIsInstance<TerrSection>().singleOrNull() ?: return emptyList()
    val tile = file.sections.filterIsInstance<TileSection>().singleOrNull() ?: return emptyList()
    val unit = file.sections.filterIsInstance<UnitSection>().singleOrNull() ?: return emptyList()
    val era = file.header.formatEra
    return unit.entries.mapIndexedNotNull { index, entry ->
        val tileEntry = tile.entries.getOrNull(wmap.tileIndex(entry.x, entry.y)) ?: return@mapIndexedNotNull null
        val terrEntry = tileEntry.baseTerrain(terr.entries, era) ?: return@mapIndexedNotNull null
        val impassable = terrEntry.allowances.impassable
        if (impassable == null || impassable.toInt() == 0) {
            null
        } else {
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.UNIT,
                index,
                "x/y",
                "UnitEntry at (${entry.x}, ${entry.y}) sits on ${terrEntry.name} terrain, which is Impassable",
            )
        }
    }
}

/**
 * Flags a [UnitEntry] whose [PrtoEntry.wheeledAbility] resolves `true` sitting on a tile whose
 * base terrain type is Impassable by Wheeled Units ([TerrAllowances.impassableByWheeled] is
 * nonzero). Returns no issues if `WMAP`, `TERR`, `TILE`, `UNIT`, or `PRTO` is absent from [file],
 * or for a [Civ3FormatEra.VANILLA]/[Civ3FormatEra.PTW] file, which predates the
 * `impassableByWheeled` field entirely.
 */
fun validateWheeledUnitNotOnImpassableByWheeledTerrain(file: Civ3File): List<ValidationIssue> {
    val wmap = file.sections.filterIsInstance<WmapSection>().singleOrNull()?.entries?.singleOrNull()
        ?: return emptyList()
    val terr = file.sections.filterIsInstance<TerrSection>().singleOrNull() ?: return emptyList()
    val tile = file.sections.filterIsInstance<TileSection>().singleOrNull() ?: return emptyList()
    val unit = file.sections.filterIsInstance<UnitSection>().singleOrNull() ?: return emptyList()
    val prto = file.sections.filterIsInstance<PrtoSection>().singleOrNull()?.entries ?: return emptyList()
    val era = file.header.formatEra
    return unit.entries.mapIndexedNotNull { index, entry ->
        val unitType = entry.unitTypePrto(prto) ?: return@mapIndexedNotNull null
        if (!unitType.wheeledAbility) return@mapIndexedNotNull null
        val tileEntry = tile.entries.getOrNull(wmap.tileIndex(entry.x, entry.y)) ?: return@mapIndexedNotNull null
        val terrEntry = tileEntry.baseTerrain(terr.entries, era) ?: return@mapIndexedNotNull null
        val impassableByWheeled = terrEntry.allowances.impassableByWheeled
        if (impassableByWheeled == null || impassableByWheeled.toInt() == 0) {
            null
        } else {
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.UNIT,
                index,
                "x/y",
                "UnitEntry at (${entry.x}, ${entry.y}) is a wheeled unit sitting on ${terrEntry.name} terrain, " +
                    "which is Impassable by Wheeled Units",
            )
        }
    }
}

/**
 * Flags a land tile immediately adjacent — including diagonally, and across either map-wrap edge
 * (see [WmapEntry.neighborTileIndices]) — to a Sea or Ocean tile with no Coast tile in between.
 * The Scenario/Rules Editor's terrain-painting tool never allows this: land only ever borders open
 * water via an intervening Coast tile. Returns no issues if `WMAP`, `TERR`, or `TILE` is absent
 * from [file].
 *
 * Terrain type is identified by fixed positional `TERR` index, not name (the Rules Editor's
 * Terrain tab only allows Rename, never Add/Delete/reorder): the last 3 `TERR` entries are always
 * Coast, Sea, and Ocean in that order, confirmed across every real file surveyed in all 3 format
 * eras. Every other `TERR` entry — including the Conquests-only Marsh and Volcano — counts as land
 * for this rule.
 */
fun validateLandNotDirectlyAdjacentToSeaOrOcean(file: Civ3File): List<ValidationIssue> {
    val wmap = file.sections.filterIsInstance<WmapSection>().singleOrNull()?.entries?.singleOrNull()
        ?: return emptyList()
    val terr = file.sections.filterIsInstance<TerrSection>().singleOrNull() ?: return emptyList()
    val tile = file.sections.filterIsInstance<TileSection>().singleOrNull() ?: return emptyList()
    val era = file.header.formatEra
    val terrCount = terr.entries.size
    if (terrCount < 3) return emptyList()
    val coastIndex = terrCount - 3
    val seaIndex = terrCount - 2
    val oceanIndex = terrCount - 1

    return tile.entries.indices.mapNotNull { index ->
        val terrIndex = tile.entries[index].baseTerrainIndex(era) ?: return@mapNotNull null
        if (terrIndex >= coastIndex) return@mapNotNull null

        val (x, y) = wmap.tileCoordinates(index)
        val adjacentToOpenWater = wmap.neighborTileIndices(x, y).any { neighborIndex ->
            val neighborTerrIndex = tile.entries.getOrNull(neighborIndex)?.baseTerrainIndex(era)
            neighborTerrIndex == seaIndex || neighborTerrIndex == oceanIndex
        }
        if (!adjacentToOpenWater) return@mapNotNull null

        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.TILE,
            index,
            "terrain",
            "TILE[$index] at ($x, $y) has land terrain directly adjacent to Sea or Ocean, with no Coast in between",
        )
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
 * Flags a [TileEntry.continent] that doesn't resolve, via [TileEntry.continentCont], to a `CONT`
 * entry. Returns no issues if `CONT` or `TILE` is absent from [file].
 */
fun validateTileContinentResolves(file: Civ3File): List<ValidationIssue> {
    val cont = file.sections.filterIsInstance<ContSection>().singleOrNull()?.entries ?: return emptyList()
    val tile = file.sections.filterIsInstance<TileSection>().singleOrNull() ?: return emptyList()
    return tile.entries.mapIndexedNotNull { index, entry ->
        if (entry.continentCont(cont) != null) return@mapIndexedNotNull null
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.TILE,
            index,
            "continent",
            "TILE[$index] has continent=${entry.continent}, which is not a valid CONT index (0..<${cont.size})",
        )
    }
}

/**
 * Flags a mismatch between the sum of every [ContEntry.numberOfTiles] and the `TILE` section's
 * total entry count — see [ContEntry]'s own KDoc for the partition guarantee this checks. Returns
 * no issues if `CONT` or `TILE` is absent from [file].
 */
fun validateContCardinality(file: Civ3File): List<ValidationIssue> {
    val cont = file.sections.filterIsInstance<ContSection>().singleOrNull() ?: return emptyList()
    val tile = file.sections.filterIsInstance<TileSection>().singleOrNull() ?: return emptyList()
    val sum = cont.entries.sumOf { it.numberOfTiles }
    val tileCount = tile.entries.size
    if (sum == tileCount) return emptyList()
    return listOf(
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.CONT,
            null,
            "numberOfTiles",
            "sum(CONT.numberOfTiles)=$sum does not match TILE's $tileCount entries",
        ),
    )
}

/**
 * Flags a `CONT` id used by both a land tile and a water tile — see [ContEntry]'s own KDoc for why
 * this never happens in a real file. Returns no issues if `TERR`, `TILE`, or `CONT` is absent from
 * [file].
 */
fun validateContTypeNotMixed(file: Civ3File): List<ValidationIssue> {
    val terr = file.sections.filterIsInstance<TerrSection>().singleOrNull() ?: return emptyList()
    val tile = file.sections.filterIsInstance<TileSection>().singleOrNull() ?: return emptyList()
    val cont = file.sections.filterIsInstance<ContSection>().singleOrNull()?.entries ?: return emptyList()
    val era = file.header.formatEra
    val terrCount = terr.entries.size
    if (terrCount < 3) return emptyList()
    val coastIndex = terrCount - 3

    val landIds = mutableSetOf<Int>()
    val waterIds = mutableSetOf<Int>()
    for (entry in tile.entries) {
        val id = entry.continent.toInt()
        val terrIndex = entry.baseTerrainIndex(era) ?: continue
        if (terrIndex < coastIndex) landIds += id else waterIds += id
    }
    return landIds.intersect(waterIds).sorted().map { id ->
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.CONT,
            id,
            "type",
            "CONT[$id] (${cont.getOrNull(id)?.type}) is used by both a land tile and a water tile",
        )
    }
}

/**
 * Flags two directly-adjacent land tiles — including diagonally, and across either map-wrap edge
 * (see [WmapEntry.neighborTileIndices]) — with different [TileEntry.continent] values. A land
 * continent is always exactly its physically-connected chunk of land — see [ContEntry]'s own
 * KDoc. Returns no issues if `WMAP`, `TERR`, or `TILE` is absent from [file].
 */
fun validateAdjacentLandTilesShareContinent(file: Civ3File): List<ValidationIssue> {
    val wmap = file.sections.filterIsInstance<WmapSection>().singleOrNull()?.entries?.singleOrNull()
        ?: return emptyList()
    val terr = file.sections.filterIsInstance<TerrSection>().singleOrNull() ?: return emptyList()
    val tile = file.sections.filterIsInstance<TileSection>().singleOrNull() ?: return emptyList()
    val era = file.header.formatEra
    val terrCount = terr.entries.size
    if (terrCount < 3) return emptyList()
    val coastIndex = terrCount - 3

    return tile.entries.indices.mapNotNull { index ->
        val entry = tile.entries[index]
        val terrIndex = entry.baseTerrainIndex(era) ?: return@mapNotNull null
        if (terrIndex >= coastIndex) return@mapNotNull null
        val (x, y) = wmap.tileCoordinates(index)
        val ownContinent = entry.continent.toInt()
        val mismatchedNeighbor = wmap.neighborTileIndices(x, y).firstOrNull { neighborIndex ->
            val neighbor = tile.entries.getOrNull(neighborIndex) ?: return@firstOrNull false
            val neighborTerrIndex = neighbor.baseTerrainIndex(era) ?: return@firstOrNull false
            neighborTerrIndex < coastIndex && neighbor.continent.toInt() != ownContinent
        } ?: return@mapNotNull null

        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.TILE,
            index,
            "continent",
            "TILE[$index] at ($x, $y) has continent=$ownContinent, but its adjacent land TILE[$mismatchedNeighbor] " +
                "has continent=${tile.entries[mismatchedNeighbor].continent}",
        )
    }
}

/**
 * Flags a `CONT` id whose tiles form more than one physically-disconnected group — including
 * diagonally, and across either map-wrap edge (see [WmapEntry.neighborTileIndices]). Every real
 * continent id, land or water, is always internally contiguous — see [ContEntry]'s own KDoc.
 * Returns no issues if `WMAP` or `TILE` is absent from [file].
 */
fun validateContinentContiguous(file: Civ3File): List<ValidationIssue> {
    val wmap = file.sections.filterIsInstance<WmapSection>().singleOrNull()?.entries?.singleOrNull()
        ?: return emptyList()
    val tile = file.sections.filterIsInstance<TileSection>().singleOrNull() ?: return emptyList()

    val tilesByContinent = tile.entries.indices.groupBy { tile.entries[it].continent.toInt() }
    val fragmentedIds = mutableListOf<Int>()
    for ((continentId, indices) in tilesByContinent) {
        val remaining = indices.toHashSet()
        val queue = ArrayDeque<Int>()
        val start = indices.first()
        remaining.remove(start)
        queue.add(start)
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            val (x, y) = wmap.tileCoordinates(current)
            for (neighborIndex in wmap.neighborTileIndices(x, y)) {
                if (remaining.remove(neighborIndex)) queue.add(neighborIndex)
            }
        }
        if (remaining.isNotEmpty()) fragmentedIds += continentId
    }
    return fragmentedIds.sorted().map { id ->
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.CONT,
            id,
            "type",
            "CONT[$id]'s tiles are split into more than one physically-disconnected group",
        )
    }
}

/**
 * Flags a water `CONT` id whose tiles never directly touch a land tile — including diagonally,
 * and across either map-wrap edge (see [WmapEntry.neighborTileIndices]). Every real water
 * continent touches land somewhere — see [ContEntry]'s own KDoc. Returns no issues if `WMAP`,
 * `TERR`, or `TILE` is absent from [file].
 */
fun validateWaterContinentTouchesLand(file: Civ3File): List<ValidationIssue> {
    val wmap = file.sections.filterIsInstance<WmapSection>().singleOrNull()?.entries?.singleOrNull()
        ?: return emptyList()
    val terr = file.sections.filterIsInstance<TerrSection>().singleOrNull() ?: return emptyList()
    val tile = file.sections.filterIsInstance<TileSection>().singleOrNull() ?: return emptyList()
    val era = file.header.formatEra
    val terrCount = terr.entries.size
    if (terrCount < 3) return emptyList()
    val coastIndex = terrCount - 3

    val waterIdsSeen = mutableSetOf<Int>()
    val waterIdsTouchingLand = mutableSetOf<Int>()
    for (index in tile.entries.indices) {
        val entry = tile.entries[index]
        val terrIndex = entry.baseTerrainIndex(era) ?: continue
        if (terrIndex < coastIndex) continue
        val id = entry.continent.toInt()
        waterIdsSeen += id
        val (x, y) = wmap.tileCoordinates(index)
        val touchesLand = wmap.neighborTileIndices(x, y).any { neighborIndex ->
            val neighbor = tile.entries.getOrNull(neighborIndex) ?: return@any false
            val neighborTerrIndex = neighbor.baseTerrainIndex(era) ?: return@any false
            neighborTerrIndex < coastIndex
        }
        if (touchesLand) waterIdsTouchingLand += id
    }
    return (waterIdsSeen - waterIdsTouchingLand).sorted().map { id ->
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.CONT,
            id,
            "type",
            "CONT[$id] (Water) never directly touches a land tile",
        )
    }
}

/**
 * Checks this file against every editor-confirmed constraint this library knows about. Never
 * throws; a rule whose required section(s) are absent from [Civ3File.sections] simply
 * contributes no issues.
 */
fun Civ3File.validate(): List<ValidationIssue> = civ3ValidationRules.flatMap { it.validate(this) }
