package com.kelvsyc.rifflet.civ3

/**
 * One entry of the `CONT` section: a continuous body of water or land and its tile count.
 *
 * @param type 0=Water, 1=Land.
 */
data class ContEntry(
    val type: Int,
    val numberOfTiles: Int,
)
