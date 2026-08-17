package com.kelvsyc.rifflet.civ3.domain

/**
 * One `CULT` culture-opinion level's propaganda and resistance parameters, mutable — the
 * domain-layer counterpart to [com.kelvsyc.rifflet.civ3.CultEntry].
 */
data class CultureLevel(
    var name: String,
    var chanceOfSuccessfulPropaganda: Int = 0,
    var cultureRatioPercentage: Int = 0,
    var cultureRatioDenominator: Int = 0,
    var cultureRatioNumerator: Int = 0,
    var initialResistanceChance: Int = 0,
    var continuedResistanceChance: Int = 0,
)
