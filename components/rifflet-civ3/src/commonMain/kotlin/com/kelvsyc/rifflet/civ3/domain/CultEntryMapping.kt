package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.CultEntry

/**
 * Converts a parsed `CULT` section to its domain-layer form. No `require()` guards, no
 * cross-references: `CULT` is pure scalar data with no referential-integrity failure mode,
 * matching `CONT`'s mapping shape.
 */
fun List<CultEntry>.toDomain(): List<CultureLevel> = map {
    CultureLevel(
        name = it.name,
        chanceOfSuccessfulPropaganda = it.chanceOfSuccessfulPropaganda,
        cultureRatioPercentage = it.cultureRatioPercentage,
        cultureRatioDenominator = it.cultureRatioDenominator,
        cultureRatioNumerator = it.cultureRatioNumerator,
        initialResistanceChance = it.initialResistanceChance,
        continuedResistanceChance = it.continuedResistanceChance,
    )
}

/**
 * Converts a `CULT` section's domain-layer form back to wire entries.
 */
fun List<CultureLevel>.toWire(): List<CultEntry> = map {
    CultEntry(
        name = it.name,
        chanceOfSuccessfulPropaganda = it.chanceOfSuccessfulPropaganda,
        cultureRatioPercentage = it.cultureRatioPercentage,
        cultureRatioDenominator = it.cultureRatioDenominator,
        cultureRatioNumerator = it.cultureRatioNumerator,
        initialResistanceChance = it.initialResistanceChance,
        continuedResistanceChance = it.continuedResistanceChance,
    )
}
