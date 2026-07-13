package com.kelvsyc.rifflet.civ3

/**
 * One entry of the `GOOD` section: a tradeable natural resource and its city-output bonuses.
 *
 * @param type Resource classification (e.g. bonus/luxury/strategic); undocumented semantics in
 *   both cross-referenced sources.
 * @param prerequisite Likely an index into the `TECH` section (inferred from the naming
 *   convention shared with `CtznEntry.prerequisite`/`GovtEntry.prerequisiteTechnology`); not
 *   confirmed by either cross-referenced source.
 */
data class GoodEntry(
    val name: String,
    val civilopediaEntry: String,
    val type: Int,
    val appearanceRatio: Int,
    val disappearanceProbability: Int,
    val icon: Int,
    val prerequisite: Int,
    val foodBonus: Int,
    val shieldsBonus: Int,
    val commerceBonus: Int,
)
