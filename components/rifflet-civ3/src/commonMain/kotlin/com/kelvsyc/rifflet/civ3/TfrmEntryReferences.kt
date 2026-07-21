package com.kelvsyc.rifflet.civ3

/**
 * Resolves [TfrmEntry.required] against [techs].
 */
fun TfrmEntry.requiredTech(techs: List<TechEntry>): TechEntry? = techs.getOrNull(required)

/**
 * Resolves [TfrmEntry.requiredResource1] against [goods]. Same treatment applies to
 * [TfrmEntry.requiredResource2].
 */
fun TfrmEntry.requiredResource1Good(goods: List<GoodEntry>): GoodEntry? =
    goods.getOrNull(requiredResource1)

/**
 * Resolves [TfrmEntry.requiredResource2] against [goods].
 */
fun TfrmEntry.requiredResource2Good(goods: List<GoodEntry>): GoodEntry? =
    goods.getOrNull(requiredResource2)
