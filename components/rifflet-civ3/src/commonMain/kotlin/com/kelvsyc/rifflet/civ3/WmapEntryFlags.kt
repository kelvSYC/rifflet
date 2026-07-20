package com.kelvsyc.rifflet.civ3

import com.kelvsyc.kotlin.core.traits.integral.BitCollection
import com.kelvsyc.kotlin.core.traits.integral.extensionBitFlag
import com.kelvsyc.kotlin.core.traits.integral.int

/**
 * Named accessors for [WmapEntry.flags]'s 3 documented bits, per earlier reverse-engineering
 * documentation of the BIC format (confirmed unchanged and complete by later BIX/BIQ-era
 * reverse-engineering documentation too).
 */
val WmapEntry.xWrapping: Boolean by BitCollection.int.extensionBitFlag({ flags }, 0)
val WmapEntry.yWrapping: Boolean by BitCollection.int.extensionBitFlag({ flags }, 1)
val WmapEntry.polarIceCaps: Boolean by BitCollection.int.extensionBitFlag({ flags }, 2)
