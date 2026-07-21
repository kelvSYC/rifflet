package com.kelvsyc.rifflet.civ3

/**
 * Resolves [TfrmEntry.required] against [techs]. Likely a `TECH` section index (naming
 * convention shared with `CtznEntry.prerequisite`); not confirmed by either cross-referenced
 * source.
 */
fun TfrmEntry.requiredTech(techs: List<TechEntry>): TechEntry? = techs.getOrNull(required)

/**
 * Resolves [TfrmEntry.requiredResource1] against [goods]. Likely a `GOOD` section index (naming
 * convention shared with `PRTO`'s `RequiredResource1`..`3` fields); not confirmed by either
 * reverse-engineering source.
 */
fun TfrmEntry.requiredResource1Good(goods: List<GoodEntry>): GoodEntry? =
    goods.getOrNull(requiredResource1)

/**
 * Resolves [TfrmEntry.requiredResource2] against [goods]. Same treatment as
 * [TfrmEntry.requiredResource1] — likely a `GOOD` section index; not confirmed by either
 * reverse-engineering source.
 */
fun TfrmEntry.requiredResource2Good(goods: List<GoodEntry>): GoodEntry? =
    goods.getOrNull(requiredResource2)
