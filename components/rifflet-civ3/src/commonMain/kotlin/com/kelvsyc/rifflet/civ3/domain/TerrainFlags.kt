package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.kotlin.core.traits.integral.BitCollection
import com.kelvsyc.kotlin.core.traits.integral.int
import com.kelvsyc.kotlin.core.traits.integral.mutableExtensionBitFlag

/**
 * Settable counterparts to [com.kelvsyc.rifflet.civ3.causesDisease]/
 * [com.kelvsyc.rifflet.civ3.curedBySanitation] — see that file's KDoc for what each bit means.
 */
var Terrain.causesDisease: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ terrainFlags }, { terrainFlags = it }, 2)
var Terrain.curedBySanitation: Boolean by
    BitCollection.int.mutableExtensionBitFlag({ terrainFlags }, { terrainFlags = it }, 3)
