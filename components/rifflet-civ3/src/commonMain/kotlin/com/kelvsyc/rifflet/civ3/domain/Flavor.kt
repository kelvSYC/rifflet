package com.kelvsyc.rifflet.civ3.domain

import okio.ByteString

/**
 * One `FLAV` flavor's identity, mutable — the domain-layer counterpart to
 * [com.kelvsyc.rifflet.civ3.FlavorEntry], minus its relation data (see [FlavorRelations]).
 *
 * @param unknown 4 bytes with zero documented behavior; preserved raw, not validated.
 */
data class Flavor(
    var name: String,
    var unknown: ByteString = ByteString.of(0, 0, 0, 0),
)
