package com.kelvsyc.rifflet.civ3

/**
 * Resolves [PrtoEntry.required] against [techs]. Likely a `TECH` section index (naming
 * convention only); not confirmed by either primary source.
 */
fun PrtoEntry.requiredTech(techs: List<TechEntry>): TechEntry? = techs.getOrNull(required)

/**
 * Resolves [PrtoEntry.upgradeTo] against [prtos] (the same `PRTO` section this entry came from).
 * Likely a `PRTO` section self-reference (naming convention only); not confirmed by either
 * primary source.
 */
fun PrtoEntry.upgradeToPrto(prtos: List<PrtoEntry>): PrtoEntry? = prtos.getOrNull(upgradeTo)

/**
 * Resolves [PrtoEntry.requiredResource1] against [goods]. Likely a `GOOD` section index (naming
 * convention only); not confirmed by either primary source.
 */
fun PrtoEntry.requiredResource1Good(goods: List<GoodEntry>): GoodEntry? =
    goods.getOrNull(requiredResource1)

/**
 * Resolves [PrtoEntry.requiredResource2] against [goods]. Same treatment as
 * [PrtoEntry.requiredResource1] — likely a `GOOD` section index (naming convention only); not
 * confirmed by either primary source.
 */
fun PrtoEntry.requiredResource2Good(goods: List<GoodEntry>): GoodEntry? =
    goods.getOrNull(requiredResource2)

/**
 * Resolves [PrtoEntry.requiredResource3] against [goods]. Same treatment as
 * [PrtoEntry.requiredResource1] — likely a `GOOD` section index (naming convention only); not
 * confirmed by either primary source.
 */
fun PrtoEntry.requiredResource3Good(goods: List<GoodEntry>): GoodEntry? =
    goods.getOrNull(requiredResource3)

/**
 * Resolves each id in [PrtoEntry.stealthTargetUnitTypes] against [prtos] (the same `PRTO`
 * section this entry came from), preserving position: the result is the same length as
 * [PrtoEntry.stealthTargetUnitTypes], with `null` at any position whose id doesn't resolve.
 * Likely `PRTO` section self-references (naming convention only); not confirmed by either
 * primary source.
 */
fun PrtoEntry.stealthTargetUnitTypesPrto(prtos: List<PrtoEntry>): List<PrtoEntry?> =
    stealthTargetUnitTypes.map { prtos.getOrNull(it) }
