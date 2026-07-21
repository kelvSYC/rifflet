package com.kelvsyc.rifflet.civ3

/**
 * Resolves [RuleEntry.advancedBarbarianUnitType] against [prtos]. Same treatment applies to
 * [RuleEntry.basicBarbarianUnitType], [RuleEntry.barbarianSeaUnitType],
 * [RuleEntry.battleCreatedUnit], [RuleEntry.buildArmyUnit], [RuleEntry.scout],
 * [RuleEntry.slave], [RuleEntry.startUnit1], and [RuleEntry.startUnit2].
 */
fun RuleEntry.advancedBarbarianUnitTypePrto(prtos: List<PrtoEntry>): PrtoEntry? =
    prtos.getOrNull(advancedBarbarianUnitType)

/**
 * Resolves [RuleEntry.basicBarbarianUnitType] against [prtos].
 */
fun RuleEntry.basicBarbarianUnitTypePrto(prtos: List<PrtoEntry>): PrtoEntry? =
    prtos.getOrNull(basicBarbarianUnitType)

/**
 * Resolves [RuleEntry.barbarianSeaUnitType] against [prtos].
 */
fun RuleEntry.barbarianSeaUnitTypePrto(prtos: List<PrtoEntry>): PrtoEntry? =
    prtos.getOrNull(barbarianSeaUnitType)

/**
 * Resolves [RuleEntry.battleCreatedUnit] against [prtos].
 */
fun RuleEntry.battleCreatedUnitPrto(prtos: List<PrtoEntry>): PrtoEntry? =
    prtos.getOrNull(battleCreatedUnit)

/**
 * Resolves [RuleEntry.buildArmyUnit] against [prtos].
 */
fun RuleEntry.buildArmyUnitPrto(prtos: List<PrtoEntry>): PrtoEntry? = prtos.getOrNull(buildArmyUnit)

/**
 * Resolves [RuleEntry.scout] against [prtos].
 */
fun RuleEntry.scoutPrto(prtos: List<PrtoEntry>): PrtoEntry? = prtos.getOrNull(scout)

/**
 * Resolves [RuleEntry.slave] against [prtos].
 */
fun RuleEntry.slavePrto(prtos: List<PrtoEntry>): PrtoEntry? = prtos.getOrNull(slave)

/**
 * Resolves [RuleEntry.startUnit1] against [prtos].
 */
fun RuleEntry.startUnit1Prto(prtos: List<PrtoEntry>): PrtoEntry? = prtos.getOrNull(startUnit1)

/**
 * Resolves [RuleEntry.startUnit2] against [prtos].
 */
fun RuleEntry.startUnit2Prto(prtos: List<PrtoEntry>): PrtoEntry? = prtos.getOrNull(startUnit2)

/**
 * Resolves [RuleEntry.defaultMoneyResource] against [goods].
 */
fun RuleEntry.defaultMoneyResourceGood(goods: List<GoodEntry>): GoodEntry? =
    goods.getOrNull(defaultMoneyResource)

/**
 * Resolves [RuleEntry.flagUnitType] against [prtos]. A `PRTO` section index — explicitly
 * documented by existing reverse-engineering work ("flag unit (PRTO ref)"), not merely a
 * naming-based inference.
 */
fun RuleEntry.flagUnitTypePrto(prtos: List<PrtoEntry>): PrtoEntry? = prtos.getOrNull(flagUnitType)
