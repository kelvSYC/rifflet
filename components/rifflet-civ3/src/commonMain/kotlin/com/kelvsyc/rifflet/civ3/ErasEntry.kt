package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `ERAS` section: a game era's civilopedia text and researcher-name pool.
 *
 * @param unknown 4 bytes Apolyton documents as `??? (=1)`; preserved raw, not validated.
 */
data class ErasEntry(
    val name: String,
    val civilopediaEntry: String,
    val researcher1: String,
    val researcher2: String,
    val researcher3: String,
    val researcher4: String,
    val researcher5: String,
    val numberOfUsedResearcherNames: Int,
    val unknown: ByteString,
) {
    init {
        require(unknown.size == 4) { "ErasEntry.unknown must be exactly 4 bytes, was ${unknown.size}" }
    }
}
