package com.kelvsyc.rifflet.civ3

import com.kelvsyc.kotlin.core.traits.integral.BitCollection
import com.kelvsyc.kotlin.core.traits.integral.extensionBitFlag
import com.kelvsyc.kotlin.core.traits.integral.int

/**
 * Named accessors for [WmapEntry.flags]'s 3 documented bits, per Apolyton's "Civilization III
 * BIC file format (2nd thread)" (confirmed unchanged and complete by the later BIX/BIQ-era
 * documentation too).
 */
val WmapEntry.xWrapping: Boolean by BitCollection.int.extensionBitFlag({ flags }, 0)
val WmapEntry.yWrapping: Boolean by BitCollection.int.extensionBitFlag({ flags }, 1)
val WmapEntry.polarIceCaps: Boolean by BitCollection.int.extensionBitFlag({ flags }, 2)
