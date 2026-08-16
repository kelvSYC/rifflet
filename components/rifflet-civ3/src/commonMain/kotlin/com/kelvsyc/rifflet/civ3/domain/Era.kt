package com.kelvsyc.rifflet.civ3.domain

import okio.ByteString

/**
 * One `ERAS` era definition, mutable — the domain-layer counterpart to
 * [com.kelvsyc.rifflet.civ3.ErasEntry].
 *
 * @param researchers This era's researcher-name pool, in entry order. The Rules Editor fills
 *   these positionally — you cannot set researcher 3 without researcher 1 and 2 already set —
 *   so a shorter list always means "not entered", never "entered blank."
 * @param unknown 4 bytes with zero documented behavior; preserved raw, not validated.
 */
data class Era(
    var name: String,
    var civilopediaEntry: String = "",
    var researchers: MutableList<String> = mutableListOf(),
    var unknown: ByteString = ByteString.of(0, 0, 0, 0),
)
