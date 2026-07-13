package com.kelvsyc.rifflet.civ3

/**
 * One entry of the `CULT` section: a culture-opinion level's propaganda and resistance
 * parameters.
 */
data class CultEntry(
    val name: String,
    val chanceOfSuccessfulPropaganda: Int,
    val cultureRatioPercentage: Int,
    val cultureRatioDenominator: Int,
    val cultureRatioNumerator: Int,
    val initialResistanceChance: Int,
    val continuedResistanceChance: Int,
)
