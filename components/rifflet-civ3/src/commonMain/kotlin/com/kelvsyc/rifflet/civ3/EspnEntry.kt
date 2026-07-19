package com.kelvsyc.rifflet.civ3

/**
 * One entry of the `ESPN` section: a diplomat/spy espionage mission type.
 *
 * @param missionFlags 4 bytes of packed boolean flags; see [diplomat], [spy] for the named
 *   per-bit accessors.
 */
data class EspnEntry(
    val description: String,
    val name: String,
    val civilopediaEntry: String,
    val missionFlags: Int,
    val baseCost: Int,
)
