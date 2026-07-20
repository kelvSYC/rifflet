package com.kelvsyc.rifflet.civ3

/**
 * Resolves [LeadStartUnit.unitType] against [prtos]. Likely a `PRTO` section index (naming
 * convention only); not confirmed by either primary source.
 */
fun LeadStartUnit.unitTypePrto(prtos: List<PrtoEntry>): PrtoEntry? = prtos.getOrNull(unitType)
