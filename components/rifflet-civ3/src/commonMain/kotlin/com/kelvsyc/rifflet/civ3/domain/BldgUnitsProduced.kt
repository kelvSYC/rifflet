package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.PrtoEntry

/**
 * The unit a building produces each turn, if any — the domain-layer counterpart to
 * [com.kelvsyc.rifflet.civ3.BldgUnitsProduced].
 *
 * @param unitProduced The produced unit. References the wire `PrtoEntry` — `PRTO` doesn't have
 *   its own domain type yet.
 * @param unitFrequency How many turns between each production of [unitProduced].
 */
data class BldgUnitsProduced(
    var unitProduced: PrtoEntry? = null,
    var unitFrequency: Int = 0,
)
