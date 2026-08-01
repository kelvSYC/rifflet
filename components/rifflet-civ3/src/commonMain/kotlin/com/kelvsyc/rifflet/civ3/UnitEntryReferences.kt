package com.kelvsyc.rifflet.civ3

/**
 * Resolves [UnitEntry.ownerType]/[UnitEntry.owner] against [races]. See [Owner] for what each
 * case means.
 */
fun UnitEntry.resolveOwner(races: List<RaceEntry>): Owner = resolveOwner(ownerType, owner, races)

/**
 * Resolves [UnitEntry.experienceLevel] against [experienceLevels].
 */
fun UnitEntry.experienceLevelExpr(experienceLevels: List<ExprEntry>): ExprEntry? =
    experienceLevels.getOrNull(experienceLevel)

/**
 * Resolves [UnitEntry.unitType] against [prtos].
 */
fun UnitEntry.unitTypePrto(prtos: List<PrtoEntry>): PrtoEntry? = prtos.getOrNull(unitType)
