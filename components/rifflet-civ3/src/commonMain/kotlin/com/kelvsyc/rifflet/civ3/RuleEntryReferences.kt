package com.kelvsyc.rifflet.civ3

/**
 * Resolves [RuleEntry.advancedBarbarianUnitType] against [prtos]. Likely a `PRTO` section index
 * (naming convention only); not confirmed by either reverse-engineering source.
 */
fun RuleEntry.advancedBarbarianUnitTypePrto(prtos: List<PrtoEntry>): PrtoEntry? =
    prtos.getOrNull(advancedBarbarianUnitType)

/**
 * Resolves [RuleEntry.basicBarbarianUnitType] against [prtos]. Same treatment as
 * [RuleEntry.advancedBarbarianUnitType] — likely a `PRTO` section index (naming convention
 * only); not confirmed by either reverse-engineering source.
 */
fun RuleEntry.basicBarbarianUnitTypePrto(prtos: List<PrtoEntry>): PrtoEntry? =
    prtos.getOrNull(basicBarbarianUnitType)

/**
 * Resolves [RuleEntry.barbarianSeaUnitType] against [prtos]. Same treatment as
 * [RuleEntry.advancedBarbarianUnitType] — likely a `PRTO` section index (naming convention
 * only); not confirmed by either reverse-engineering source.
 */
fun RuleEntry.barbarianSeaUnitTypePrto(prtos: List<PrtoEntry>): PrtoEntry? =
    prtos.getOrNull(barbarianSeaUnitType)

/**
 * Resolves [RuleEntry.battleCreatedUnit] against [prtos]. Same treatment as
 * [RuleEntry.advancedBarbarianUnitType] — likely a `PRTO` section index (naming convention
 * only); not confirmed by either reverse-engineering source.
 */
fun RuleEntry.battleCreatedUnitPrto(prtos: List<PrtoEntry>): PrtoEntry? =
    prtos.getOrNull(battleCreatedUnit)

/**
 * Resolves [RuleEntry.buildArmyUnit] against [prtos]. Same treatment as
 * [RuleEntry.advancedBarbarianUnitType] — likely a `PRTO` section index (naming convention
 * only); not confirmed by either reverse-engineering source.
 */
fun RuleEntry.buildArmyUnitPrto(prtos: List<PrtoEntry>): PrtoEntry? = prtos.getOrNull(buildArmyUnit)

/**
 * Resolves [RuleEntry.scout] against [prtos]. Same treatment as
 * [RuleEntry.advancedBarbarianUnitType] — likely a `PRTO` section index (naming convention
 * only); not confirmed by either reverse-engineering source.
 */
fun RuleEntry.scoutPrto(prtos: List<PrtoEntry>): PrtoEntry? = prtos.getOrNull(scout)

/**
 * Resolves [RuleEntry.slave] against [prtos]. Same treatment as
 * [RuleEntry.advancedBarbarianUnitType] — likely a `PRTO` section index (naming convention
 * only); not confirmed by either reverse-engineering source.
 */
fun RuleEntry.slavePrto(prtos: List<PrtoEntry>): PrtoEntry? = prtos.getOrNull(slave)

/**
 * Resolves [RuleEntry.startUnit1] against [prtos]. Same treatment as
 * [RuleEntry.advancedBarbarianUnitType] — likely a `PRTO` section index (naming convention
 * only); not confirmed by either reverse-engineering source.
 */
fun RuleEntry.startUnit1Prto(prtos: List<PrtoEntry>): PrtoEntry? = prtos.getOrNull(startUnit1)

/**
 * Resolves [RuleEntry.startUnit2] against [prtos]. Same treatment as
 * [RuleEntry.advancedBarbarianUnitType] — likely a `PRTO` section index (naming convention
 * only); not confirmed by either reverse-engineering source.
 */
fun RuleEntry.startUnit2Prto(prtos: List<PrtoEntry>): PrtoEntry? = prtos.getOrNull(startUnit2)

/**
 * Resolves [RuleEntry.defaultMoneyResource] against [goods]. Likely a `GOOD` section index
 * (naming convention only); not confirmed by either reverse-engineering source.
 */
fun RuleEntry.defaultMoneyResourceGood(goods: List<GoodEntry>): GoodEntry? =
    goods.getOrNull(defaultMoneyResource)

/**
 * Resolves [RuleEntry.flagUnitType] against [prtos]. A `PRTO` section index — explicitly
 * documented by existing reverse-engineering work ("flag unit (PRTO ref)"), not merely a
 * naming-based inference.
 */
fun RuleEntry.flagUnitTypePrto(prtos: List<PrtoEntry>): PrtoEntry? = prtos.getOrNull(flagUnitType)
