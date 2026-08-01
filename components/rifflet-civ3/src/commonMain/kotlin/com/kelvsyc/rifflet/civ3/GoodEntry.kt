package com.kelvsyc.rifflet.civ3

/**
 * One entry of the `GOOD` section: a tradeable natural resource and its city-output bonuses.
 *
 * @param type Resource classification: a 3-way choice per the Conquests Rules Editor — Bonus
 *   Resource, Luxury, Strategic Resource, in that order. See [GoodResourceType] for the decoded
 *   form.
 * @param prerequisite A `TECH` section index, per the Conquests Rules Editor (not merely a
 *   naming-based inference from the convention shared with `CtznEntry.prerequisite`/
 *   `GovtEntry.prerequisiteTechnology`).
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
