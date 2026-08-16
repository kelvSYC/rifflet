package com.kelvsyc.rifflet.civ3.domain

/**
 * One `DIFF` difficulty level's game-balance parameters, mutable — the domain-layer counterpart
 * to [com.kelvsyc.rifflet.civ3.DiffEntry].
 */
data class Difficulty(
    var name: String,
    var numberOfCitizensBornContent: Int = 0,
    var maxGovernmentTransitionTime: Int = 0,
    var numberOfAiDefensiveStartingUnits: Int = 0,
    var numberOfAiOffensiveStartingUnits: Int = 0,
    var extraStartUnit1: Int = 0,
    var extraStartUnit2: Int = 0,
    var additionalFreeSupport: Int = 0,
    var unitSupportBonusForEachSettlement: Int = 0,
    var attackBonusAgainstBarbarians: Int = 0,
    var costFactor: Int = 0,
    var percentageOfOptimalCities: Int = 0,
    var aiToAiTradeRate: Int = 0,
    var corruptionPercentage: Int = 0,
    var militaryLaw: Int = 0,
)
