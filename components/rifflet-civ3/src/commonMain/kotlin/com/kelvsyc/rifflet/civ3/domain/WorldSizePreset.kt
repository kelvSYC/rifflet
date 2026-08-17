package com.kelvsyc.rifflet.civ3.domain

import okio.ByteString

/**
 * One `WSIZ` world-size preset's map-generation defaults, mutable — the domain-layer counterpart
 * to [com.kelvsyc.rifflet.civ3.WsizEntry].
 *
 * @param height This preset's recommended map height. @param distanceBetweenCivs/[numberOfCivs]/
 *   [width] the same, all recommended values — distinct from a generated map's own live values
 *   (see the domain-layer `WorldMap` type).
 * @param reserved 24 bytes existing reverse-engineering documentation lists as `??? (empty)`;
 *   preserved raw, not validated.
 */
data class WorldSizePreset(
    var name: String,
    var optimalNumberOfCities: Int = 0,
    var techRate: Int = 0,
    var height: Int = 0,
    var distanceBetweenCivs: Int = 0,
    var numberOfCivs: Int = 0,
    var width: Int = 0,
    var reserved: ByteString = ByteString.of(*ByteArray(24)),
)
