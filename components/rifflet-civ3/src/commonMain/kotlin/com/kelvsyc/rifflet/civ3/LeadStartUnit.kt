package com.kelvsyc.rifflet.civ3

/**
 * One starting unit allotment within a `LEAD` entry: a quantity of a given unit type.
 *
 * @param unitType A `PRTO` section index, per the Players tab's "Unit" dropdown.
 */
data class LeadStartUnit(
    val quantity: Int,
    val unitType: Int,
)
