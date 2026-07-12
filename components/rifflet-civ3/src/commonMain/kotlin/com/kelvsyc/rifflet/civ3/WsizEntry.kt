package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `WSIZ` section: a world-size preset (map dimensions, civ count, tech rate).
 *
 * @param reserved 24 bytes Apolyton documents as `??? (empty)`; preserved raw, not validated.
 */
data class WsizEntry(
    val optimalNumberOfCities: Int,
    val techRate: Int,
    val reserved: ByteString,
    val name: String,
    val height: Int,
    val distanceBetweenCivs: Int,
    val numberOfCivs: Int,
    val width: Int,
) {
    init {
        require(reserved.size == 24) { "WsizEntry.reserved must be exactly 24 bytes, was ${reserved.size}" }
    }
}
