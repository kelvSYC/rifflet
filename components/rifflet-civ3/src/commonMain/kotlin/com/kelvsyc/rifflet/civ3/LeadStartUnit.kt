package com.kelvsyc.rifflet.civ3

/**
 * One starting unit allotment within a `LEAD` entry: a quantity of a given unit type.
 *
 * @param unitType Likely a `PRTO` section index (naming convention only); not confirmed by
 *   either cross-referenced source.
 */
data class LeadStartUnit(
    val quantity: Int,
    val unitType: Int,
)
