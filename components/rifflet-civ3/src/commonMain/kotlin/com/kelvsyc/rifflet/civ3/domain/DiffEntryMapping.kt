package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.DiffEntry

/**
 * Converts a parsed `DIFF` section to its domain-layer form. No `require()` guards, no
 * cross-references: `DIFF` is pure scalar data with no referential-integrity failure mode,
 * matching `CONT`'s mapping shape. `validateDiffCardinality` owns the "6 for VANILLA/PTW, 8+ for
 * CONQUESTS" floor at the wire layer — this mapping imposes no cardinality requirement of its own.
 */
fun List<DiffEntry>.toDomain(): List<Difficulty> = map { entry ->
    Difficulty(
        name = entry.name,
        numberOfCitizensBornContent = entry.numberOfCitizensBornContent,
        maxGovernmentTransitionTime = entry.maxGovernmentTransitionTime,
        numberOfAiDefensiveStartingUnits = entry.numberOfAiDefensiveStartingUnits,
        numberOfAiOffensiveStartingUnits = entry.numberOfAiOffensiveStartingUnits,
        extraStartUnit1 = entry.extraStartUnit1,
        extraStartUnit2 = entry.extraStartUnit2,
        additionalFreeSupport = entry.additionalFreeSupport,
        unitSupportBonusForEachSettlement = entry.unitSupportBonusForEachSettlement,
        attackBonusAgainstBarbarians = entry.attackBonusAgainstBarbarians,
        costFactor = entry.costFactor,
        percentageOfOptimalCities = entry.percentageOfOptimalCities,
        aiToAiTradeRate = entry.aiToAiTradeRate,
        corruptionPercentage = entry.corruptionPercentage,
        militaryLaw = entry.militaryLaw,
    )
}

/**
 * Converts a `DIFF` section's domain-layer form back to wire entries.
 */
fun List<Difficulty>.toWire(): List<DiffEntry> = map { difficulty ->
    DiffEntry(
        name = difficulty.name,
        numberOfCitizensBornContent = difficulty.numberOfCitizensBornContent,
        maxGovernmentTransitionTime = difficulty.maxGovernmentTransitionTime,
        numberOfAiDefensiveStartingUnits = difficulty.numberOfAiDefensiveStartingUnits,
        numberOfAiOffensiveStartingUnits = difficulty.numberOfAiOffensiveStartingUnits,
        extraStartUnit1 = difficulty.extraStartUnit1,
        extraStartUnit2 = difficulty.extraStartUnit2,
        additionalFreeSupport = difficulty.additionalFreeSupport,
        unitSupportBonusForEachSettlement = difficulty.unitSupportBonusForEachSettlement,
        attackBonusAgainstBarbarians = difficulty.attackBonusAgainstBarbarians,
        costFactor = difficulty.costFactor,
        percentageOfOptimalCities = difficulty.percentageOfOptimalCities,
        aiToAiTradeRate = difficulty.aiToAiTradeRate,
        corruptionPercentage = difficulty.corruptionPercentage,
        militaryLaw = difficulty.militaryLaw,
    )
}
