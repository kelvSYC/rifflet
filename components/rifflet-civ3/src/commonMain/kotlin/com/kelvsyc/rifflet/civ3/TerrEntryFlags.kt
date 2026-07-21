package com.kelvsyc.rifflet.civ3

import com.kelvsyc.kotlin.core.traits.integral.BitCollection
import com.kelvsyc.kotlin.core.traits.integral.extensionBitFlag
import com.kelvsyc.kotlin.core.traits.integral.int

/**
 * Named accessors for 2 of [TerrEntry.terrainFlags]'s otherwise-opaque low bits: bit 2 is
 * "Causes Disease", bit 3 is "Cured by Sanitation". See [TerrEntry.terrainFlags]'s own KDoc for
 * the remaining unexplained low bits.
 */
val TerrEntry.causesDisease: Boolean by BitCollection.int.extensionBitFlag({ terrainFlags }, 2)
val TerrEntry.curedBySanitation: Boolean by BitCollection.int.extensionBitFlag({ terrainFlags }, 3)
