package com.kelvsyc.rifflet.civ3

/**
 * One entry of the `GOOD` section: a tradeable natural resource and its city-output bonuses.
 *
 * @param prerequisite A `TECH` section index, per the Conquests Rules Editor.
 */
data class GoodEntry(
    val name: String,
    val civilopediaEntry: String,
    val type: GoodResourceType,
    val appearanceRatio: Int,
    val disappearanceProbability: Int,
    val icon: Int,
    val prerequisite: Int,
    val foodBonus: Int,
    val shieldsBonus: Int,
    val commerceBonus: Int,
)
