package com.kelvsyc.rifflet.civ3

/**
 * Resolves [BldgEntry.requiredBuilding] against [buildings] (the same `BLDG` section this entry
 * came from). Likely a `BLDG` section self-reference (naming convention only); not confirmed by
 * either reverse-engineering source.
 */
fun BldgEntry.requiredBuildingBldg(buildings: List<BldgEntry>): BldgEntry? =
    buildings.getOrNull(requiredBuilding)

/**
 * Resolves [BldgEntry.requiredGovernment] against [governments]. Likely a `GOVT` section index
 * (naming convention only); not confirmed by either reverse-engineering source.
 */
fun BldgEntry.requiredGovernmentGovt(governments: List<GovtEntry>): GovtEntry? =
    governments.getOrNull(requiredGovernment)

/**
 * Resolves [BldgEntry.requiredAdvance] against [techs]. Likely a `TECH` section index (naming
 * convention only); not confirmed by either reverse-engineering source.
 */
fun BldgEntry.requiredAdvanceTech(techs: List<TechEntry>): TechEntry? = techs.getOrNull(requiredAdvance)

/**
 * Resolves [BldgEntry.renderedObsoleteBy] against [techs]. Same treatment as
 * [BldgEntry.requiredAdvance] — likely a `TECH` section index (naming convention only); not
 * confirmed by either reverse-engineering source.
 */
fun BldgEntry.renderedObsoleteByTech(techs: List<TechEntry>): TechEntry? =
    techs.getOrNull(renderedObsoleteBy)

/**
 * Resolves [BldgEntry.requiredResource1] against [goods]. Likely a `GOOD` section index (naming
 * convention only); not confirmed by either reverse-engineering source.
 */
fun BldgEntry.requiredResource1Good(goods: List<GoodEntry>): GoodEntry? =
    goods.getOrNull(requiredResource1)

/**
 * Resolves [BldgEntry.requiredResource2] against [goods]. Same treatment as
 * [BldgEntry.requiredResource1] — likely a `GOOD` section index (naming convention only); not
 * confirmed by either reverse-engineering source.
 */
fun BldgEntry.requiredResource2Good(goods: List<GoodEntry>): GoodEntry? =
    goods.getOrNull(requiredResource2)

/**
 * Resolves [BldgEntry.unitProduced] against [prtos]. A `PRTO` section index — explicitly
 * documented by existing reverse-engineering work ("Unit produced (PRTO ref)"), not merely a
 * naming-based inference.
 */
fun BldgEntry.unitProducedPrto(prtos: List<PrtoEntry>): PrtoEntry? = prtos.getOrNull(unitProduced)
