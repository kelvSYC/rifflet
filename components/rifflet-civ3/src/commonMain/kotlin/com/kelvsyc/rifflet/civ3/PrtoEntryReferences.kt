package com.kelvsyc.rifflet.civ3

/**
 * Resolves [PrtoEntry.required] against [techs].
 */
fun PrtoEntry.requiredTech(techs: List<TechEntry>): TechEntry? = techs.getOrNull(required)

/**
 * Resolves [PrtoEntry.upgradeTo] against [prtos] (the same `PRTO` section this entry came from).
 */
fun PrtoEntry.upgradeToPrto(prtos: List<PrtoEntry>): PrtoEntry? = prtos.getOrNull(upgradeTo)

/**
 * Resolves [PrtoEntry.requiredResource1] against [goods]. Same treatment applies to
 * [PrtoEntry.requiredResource2] and [PrtoEntry.requiredResource3].
 */
fun PrtoEntry.requiredResource1Good(goods: List<GoodEntry>): GoodEntry? =
    goods.getOrNull(requiredResource1)

/**
 * Resolves [PrtoEntry.requiredResource2] against [goods].
 */
fun PrtoEntry.requiredResource2Good(goods: List<GoodEntry>): GoodEntry? =
    goods.getOrNull(requiredResource2)

/**
 * Resolves [PrtoEntry.requiredResource3] against [goods].
 */
fun PrtoEntry.requiredResource3Good(goods: List<GoodEntry>): GoodEntry? =
    goods.getOrNull(requiredResource3)

/**
 * Resolves each id in [PrtoEntry.stealthTargetUnitTypes] against [prtos] (the same `PRTO`
 * section this entry came from), preserving position: the result is the same length as
 * [PrtoEntry.stealthTargetUnitTypes], with `null` at any position whose id doesn't resolve.
 */
fun PrtoEntry.stealthTargetUnitTypesPrto(prtos: List<PrtoEntry>): List<PrtoEntry?> =
    stealthTargetUnitTypes.map { prtos.getOrNull(it) }
