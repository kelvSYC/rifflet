package com.kelvsyc.rifflet.civ3

/**
 * Resolves [UnitEntry.ownerType]/[UnitEntry.owner] against [races]. See [Owner] for what each
 * case means.
 */
fun UnitEntry.resolveOwner(races: List<RaceEntry>): Owner = resolveOwner(ownerType, owner, races)

/**
 * Resolves [UnitEntry.experienceLevel] against [experienceLevels]. Likely an `EXPR` section
 * index (naming convention only); not confirmed by either primary source.
 */
fun UnitEntry.experienceLevelExpr(experienceLevels: List<ExprEntry>): ExprEntry? =
    experienceLevels.getOrNull(experienceLevel)

/**
 * Resolves [UnitEntry.unitType] against [prtos]. A `PRTO#` (unit prototype) reference —
 * explicitly documented by Apolyton's BIX/BIQ format reference, not merely a naming-based
 * inference.
 */
fun UnitEntry.unitTypePrto(prtos: List<PrtoEntry>): PrtoEntry? = prtos.getOrNull(unitType)
