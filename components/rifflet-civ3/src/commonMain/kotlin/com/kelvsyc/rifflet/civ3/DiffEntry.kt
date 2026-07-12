package com.kelvsyc.rifflet.civ3

/**
 * One entry of the `DIFF` section: a difficulty level's game-balance parameters.
 */
data class DiffEntry(
    val name: String,
    val numberOfCitizensBornContent: Int,
    val maxGovernmentTransitionTime: Int,
    val numberOfAiDefensiveStartingUnits: Int,
    val numberOfAiOffensiveStartingUnits: Int,
    val extraStartUnit1: Int,
    val extraStartUnit2: Int,
    val additionalFreeSupport: Int,
    val unitSupportBonusForEachSettlement: Int,
    val attackBonusAgainstBarbarians: Int,
    val costFactor: Int,
    val percentageOfOptimalCities: Int,
    val aiToAiTradeRate: Int,
    val corruptionPercentage: Int,
    val militaryLaw: Int,
)
