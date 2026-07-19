package com.kelvsyc.rifflet.civ3

/**
 * Named accessors for [WmapEntry.flags]'s 3 documented bits, per Apolyton's "Civilization III
 * BIC file format (2nd thread)" (confirmed unchanged and complete by the later BIX/BIQ-era
 * documentation too).
 */
val WmapEntry.xWrapping: Boolean get() = flags and (1 shl 0) != 0
val WmapEntry.yWrapping: Boolean get() = flags and (1 shl 1) != 0
val WmapEntry.polarIceCaps: Boolean get() = flags and (1 shl 2) != 0
