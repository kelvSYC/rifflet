package com.kelvsyc.rifflet.civ3

/**
 * Resolves [BldgEntry.requiredBuilding] against [buildings] (the same `BLDG` section this entry
 * came from).
 */
fun BldgEntry.requiredBuildingBldg(buildings: List<BldgEntry>): BldgEntry? =
    buildings.getOrNull(requiredBuilding)

/**
 * Resolves [BldgEntry.requiredGovernment] against [governments].
 */
fun BldgEntry.requiredGovernmentGovt(governments: List<GovtEntry>): GovtEntry? =
    governments.getOrNull(requiredGovernment)

/**
 * Resolves [BldgEntry.requiredAdvance] against [techs]. Same treatment applies to
 * [BldgEntry.renderedObsoleteBy].
 */
fun BldgEntry.requiredAdvanceTech(techs: List<TechEntry>): TechEntry? = techs.getOrNull(requiredAdvance)

/**
 * Resolves [BldgEntry.renderedObsoleteBy] against [techs].
 */
fun BldgEntry.renderedObsoleteByTech(techs: List<TechEntry>): TechEntry? =
    techs.getOrNull(renderedObsoleteBy)

/**
 * Resolves [BldgEntry.requiredResource1] against [goods]. Same treatment applies to
 * [BldgEntry.requiredResource2].
 */
fun BldgEntry.requiredResource1Good(goods: List<GoodEntry>): GoodEntry? =
    goods.getOrNull(requiredResource1)

/**
 * Resolves [BldgEntry.requiredResource2] against [goods].
 */
fun BldgEntry.requiredResource2Good(goods: List<GoodEntry>): GoodEntry? =
    goods.getOrNull(requiredResource2)

/**
 * Resolves [BldgEntry.unitProduced] against [prtos]. A `PRTO` section index — explicitly
 * documented by existing reverse-engineering work ("Unit produced (PRTO ref)"), not merely a
 * naming-based inference.
 */
fun BldgEntry.unitProducedPrto(prtos: List<PrtoEntry>): PrtoEntry? = prtos.getOrNull(unitProduced)
