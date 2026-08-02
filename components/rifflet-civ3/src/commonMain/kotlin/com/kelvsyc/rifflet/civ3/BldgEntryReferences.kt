package com.kelvsyc.rifflet.civ3

/**
 * Resolves [BldgEntry.doublesHappiness] against [buildings] (the same `BLDG` section this entry
 * came from).
 */
fun BldgEntry.doublesHappinessBldg(buildings: List<BldgEntry>): BldgEntry? =
    buildings.getOrNull(doublesHappiness)

/**
 * Resolves [BldgEntry.gainInEveryCity] against [buildings] (the same `BLDG` section this entry
 * came from).
 */
fun BldgEntry.gainInEveryCityBldg(buildings: List<BldgEntry>): BldgEntry? =
    buildings.getOrNull(gainInEveryCity)

/**
 * Resolves [BldgEntry.gainInEveryCityOnContinent] against [buildings] (the same `BLDG` section
 * this entry came from).
 */
fun BldgEntry.gainInEveryCityOnContinentBldg(buildings: List<BldgEntry>): BldgEntry? =
    buildings.getOrNull(gainInEveryCityOnContinent)

/**
 * Resolves [BldgRequirements.requiredBuilding] against [buildings] (the same `BLDG` section this
 * entry came from).
 */
fun BldgEntry.requiredBuildingBldg(buildings: List<BldgEntry>): BldgEntry? =
    buildings.getOrNull(requirements.requiredBuilding)

/**
 * Resolves [BldgRequirements.requiredGovernment] against [governments].
 */
fun BldgEntry.requiredGovernmentGovt(governments: List<GovtEntry>): GovtEntry? =
    governments.getOrNull(requirements.requiredGovernment)

/**
 * Resolves [BldgRequirements.requiredAdvance] against [techs]. Same treatment applies to
 * [BldgEntry.renderedObsoleteBy].
 */
fun BldgEntry.requiredAdvanceTech(techs: List<TechEntry>): TechEntry? = techs.getOrNull(requirements.requiredAdvance)

/**
 * Resolves [BldgEntry.renderedObsoleteBy] against [techs].
 */
fun BldgEntry.renderedObsoleteByTech(techs: List<TechEntry>): TechEntry? =
    techs.getOrNull(renderedObsoleteBy)

/**
 * Resolves [BldgRequiredResources.requiredResource1] against [goods]. Same treatment applies to
 * [BldgRequiredResources.requiredResource2].
 */
fun BldgEntry.requiredResource1Good(goods: List<GoodEntry>): GoodEntry? =
    goods.getOrNull(requiredResources.requiredResource1)

/**
 * Resolves [BldgRequiredResources.requiredResource2] against [goods].
 */
fun BldgEntry.requiredResource2Good(goods: List<GoodEntry>): GoodEntry? =
    goods.getOrNull(requiredResources.requiredResource2)

/**
 * Resolves [BldgUnitsProduced.unitProduced] against [prtos]. Returns `null` outright if
 * [BldgEntry.unitsProduced] itself is `null` (a VANILLA/PTW building was never capable of
 * specifying a produced unit at all) — distinct from the previous flat-field behavior, which
 * defaulted the absent field to `0` and could accidentally resolve against index 0.
 */
fun BldgEntry.unitProducedPrto(prtos: List<PrtoEntry>): PrtoEntry? =
    unitsProduced?.let { prtos.getOrNull(it.unitProduced) }

/**
 * [BldgEntry.requiredGoodsMustBeInCityRadius] resolved for [era]: reads
 * [BldgEntry.ptwRequiredGoodsMustBeInCityRadius] (`smallWonders` bit 9) for
 * [Civ3FormatEra.VANILLA]/[Civ3FormatEra.PTW] files, or the [Civ3FormatEra.CONQUESTS]-tier
 * [BldgEntry.requiredGoodsMustBeInCityRadius] (`improvements` bit 31) otherwise.
 */
fun BldgEntry.requiredGoodsMustBeInCityRadius(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.CONQUESTS) requiredGoodsMustBeInCityRadius else ptwRequiredGoodsMustBeInCityRadius
