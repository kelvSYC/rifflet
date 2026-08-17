package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.kotlin.core.traits.integral.BitCollection
import com.kelvsyc.kotlin.core.traits.integral.int
import com.kelvsyc.kotlin.core.traits.integral.mutableExtensionBitFlag

/**
 * Settable counterparts to [com.kelvsyc.rifflet.civ3.xWrapping]/
 * [com.kelvsyc.rifflet.civ3.yWrapping]/[com.kelvsyc.rifflet.civ3.polarIceCaps] — see that file's
 * KDoc for what each bit means.
 */
var WorldMap.xWrapping: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 0)
var WorldMap.yWrapping: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 1)
var WorldMap.polarIceCaps: Boolean by BitCollection.int.mutableExtensionBitFlag({ flags }, { flags = it }, 2)
