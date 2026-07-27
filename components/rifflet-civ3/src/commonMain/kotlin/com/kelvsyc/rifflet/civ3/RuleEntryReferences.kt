package com.kelvsyc.rifflet.civ3

/**
 * Resolves [RuleDefaultUnits.advancedBarbarianUnitType] against [prtos]. Same treatment applies
 * to [RuleDefaultUnits.basicBarbarianUnitType], [RuleDefaultUnits.barbarianSeaUnitType],
 * [RuleDefaultUnits.battleCreatedUnit], [RuleDefaultUnits.buildArmyUnit],
 * [RuleDefaultUnits.scout], [RuleDefaultUnits.slave], [RuleDefaultUnits.startUnit1], and
 * [RuleDefaultUnits.startUnit2].
 */
fun RuleEntry.advancedBarbarianUnitTypePrto(prtos: List<PrtoEntry>): PrtoEntry? =
    prtos.getOrNull(defaultUnits.advancedBarbarianUnitType)

/**
 * Resolves [RuleDefaultUnits.basicBarbarianUnitType] against [prtos].
 */
fun RuleEntry.basicBarbarianUnitTypePrto(prtos: List<PrtoEntry>): PrtoEntry? =
    prtos.getOrNull(defaultUnits.basicBarbarianUnitType)

/**
 * Resolves [RuleDefaultUnits.barbarianSeaUnitType] against [prtos].
 */
fun RuleEntry.barbarianSeaUnitTypePrto(prtos: List<PrtoEntry>): PrtoEntry? =
    prtos.getOrNull(defaultUnits.barbarianSeaUnitType)

/**
 * Resolves [RuleDefaultUnits.battleCreatedUnit] against [prtos].
 */
fun RuleEntry.battleCreatedUnitPrto(prtos: List<PrtoEntry>): PrtoEntry? =
    prtos.getOrNull(defaultUnits.battleCreatedUnit)

/**
 * Resolves [RuleDefaultUnits.buildArmyUnit] against [prtos].
 */
fun RuleEntry.buildArmyUnitPrto(prtos: List<PrtoEntry>): PrtoEntry? =
    prtos.getOrNull(defaultUnits.buildArmyUnit)

/**
 * Resolves [RuleDefaultUnits.scout] against [prtos].
 */
fun RuleEntry.scoutPrto(prtos: List<PrtoEntry>): PrtoEntry? = prtos.getOrNull(defaultUnits.scout)

/**
 * Resolves [RuleDefaultUnits.slave] against [prtos].
 */
fun RuleEntry.slavePrto(prtos: List<PrtoEntry>): PrtoEntry? = prtos.getOrNull(defaultUnits.slave)

/**
 * Resolves [RuleDefaultUnits.startUnit1] against [prtos].
 */
fun RuleEntry.startUnit1Prto(prtos: List<PrtoEntry>): PrtoEntry? =
    prtos.getOrNull(defaultUnits.startUnit1)

/**
 * Resolves [RuleDefaultUnits.startUnit2] against [prtos].
 */
fun RuleEntry.startUnit2Prto(prtos: List<PrtoEntry>): PrtoEntry? =
    prtos.getOrNull(defaultUnits.startUnit2)

/**
 * Resolves [RuleEntry.defaultMoneyResource] against [goods].
 */
fun RuleEntry.defaultMoneyResourceGood(goods: List<GoodEntry>): GoodEntry? =
    goods.getOrNull(defaultMoneyResource)

/**
 * Resolves [RuleDefaultUnits.flagUnitType] against [prtos]. A `PRTO` section index — explicitly
 * documented by existing reverse-engineering work ("flag unit (PRTO ref)"), not merely a
 * naming-based inference. Returns `null` outright if [RuleDefaultUnits.flagUnitType] itself is
 * `null` (a VANILLA-era file was never capable of specifying a flag unit at all) — distinct from
 * the previous flat-field behavior, which defaulted the absent field to `0` and could accidentally
 * resolve against real `PRTO` index 0.
 */
fun RuleEntry.flagUnitTypePrto(prtos: List<PrtoEntry>): PrtoEntry? =
    defaultUnits.flagUnitType?.let { prtos.getOrNull(it) }
