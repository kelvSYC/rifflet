package com.kelvsyc.rifflet.civ3

/**
 * One entry of the `ESPN` section: a diplomat/spy espionage mission type.
 *
 * @param missionFlags 4 bytes of packed boolean flags (bit 0 = usable by diplomat, bit 1 =
 *   usable by spy), kept opaque rather than decomposed into individual named booleans — see
 *   `QueryCiv3`'s `Espn.cs` for the full bit-accessor breakdown if this is ever revisited, matching
 *   the same treatment already applied to `RaceEntry.bonuses`/`governorSettings`/`buildNever`/
 *   `buildOften`.
 */
data class EspnEntry(
    val description: String,
    val name: String,
    val civilopediaEntry: String,
    val missionFlags: Int,
    val baseCost: Int,
)
