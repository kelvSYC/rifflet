package com.kelvsyc.rifflet.civ3.domain

/**
 * One starting unit allotment within a [Leader]'s roster, mutable — the domain-layer counterpart
 * to [com.kelvsyc.rifflet.civ3.LeadStartUnit].
 *
 * @param quantity How many of [unitType] this player starts with.
 * @param unitType The unit type, if it resolves. `null` when the wire index doesn't resolve.
 */
data class StartUnit(
    var quantity: Int,
    var unitType: Prto? = null,
)
