package com.kelvsyc.rifflet.civ3

/**
 * Resolves [LeadStartUnit.unitType] against [prtos].
 */
fun LeadStartUnit.unitTypePrto(prtos: List<PrtoEntry>): PrtoEntry? = prtos.getOrNull(unitType)
