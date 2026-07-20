package com.kelvsyc.rifflet.civ3

import com.kelvsyc.kotlin.core.traits.integral.BitCollection
import com.kelvsyc.kotlin.core.traits.integral.extensionBitFlag
import com.kelvsyc.kotlin.core.traits.integral.int

/**
 * Named accessors for [EspnEntry.missionFlags]'s 2 documented bits, per earlier reverse-engineering
 * documentation of the BIC format.
 */
val EspnEntry.diplomat: Boolean by BitCollection.int.extensionBitFlag({ missionFlags }, 0)
val EspnEntry.spy: Boolean by BitCollection.int.extensionBitFlag({ missionFlags }, 1)
