package com.kelvsyc.rifflet.civ3

import com.kelvsyc.kotlin.core.traits.integral.BitCollection
import com.kelvsyc.kotlin.core.traits.integral.extensionBitFlag
import com.kelvsyc.kotlin.core.traits.integral.byte
import com.kelvsyc.kotlin.core.traits.integral.int
import okio.ByteString

private fun ByteString.toIntLe(): Int =
    (this[0].toInt() and 0xFF) or
        ((this[1].toInt() and 0xFF) shl 8) or
        ((this[2].toInt() and 0xFF) shl 16) or
        ((this[3].toInt() and 0xFF) shl 24)

/**
 * Named accessors for [TileEntry.overlayFlags]'s 8 documented bits, per existing
 * reverse-engineering documentation of the BIX/BIQ format.
 */
val TileEntry.road: Boolean by BitCollection.byte.extensionBitFlag({ overlayFlags }, 0)
val TileEntry.railroad: Boolean by BitCollection.byte.extensionBitFlag({ overlayFlags }, 1)
val TileEntry.mine: Boolean by BitCollection.byte.extensionBitFlag({ overlayFlags }, 2)
val TileEntry.irrigation: Boolean by BitCollection.byte.extensionBitFlag({ overlayFlags }, 3)
val TileEntry.fortress: Boolean by BitCollection.byte.extensionBitFlag({ overlayFlags }, 4)
val TileEntry.goodyHuts: Boolean by BitCollection.byte.extensionBitFlag({ overlayFlags }, 5)
val TileEntry.pollution: Boolean by BitCollection.byte.extensionBitFlag({ overlayFlags }, 6)
val TileEntry.barbarianCamp: Boolean by BitCollection.byte.extensionBitFlag({ overlayFlags }, 7)

/**
 * Named accessors for [TileEntry.bonusFlags]'s 4 documented bits. The bit positions are
 * non-contiguous (0, 3, 4, 5) exactly as documented — not a transcription error.
 */
val TileEntry.bonusGrassland: Boolean by BitCollection.byte.extensionBitFlag({ bonusFlags }, 0)
val TileEntry.playerStart: Boolean by BitCollection.byte.extensionBitFlag({ bonusFlags }, 3)
val TileEntry.snowCappedMountains: Boolean by BitCollection.byte.extensionBitFlag({ bonusFlags }, 4)
val TileEntry.pineForest: Boolean by BitCollection.byte.extensionBitFlag({ bonusFlags }, 5)

/**
 * Named accessors for [TileEntry.riverConnections]'s 4 documented bits, per existing
 * reverse-engineering documentation of the BIX/BIQ format.
 */
val TileEntry.riverInNorth: Boolean by BitCollection.byte.extensionBitFlag({ riverConnections }, 0)
val TileEntry.riverInWest: Boolean by BitCollection.byte.extensionBitFlag({ riverConnections }, 1)
val TileEntry.riverInEast: Boolean by BitCollection.byte.extensionBitFlag({ riverConnections }, 2)
val TileEntry.riverInSouth: Boolean by BitCollection.byte.extensionBitFlag({ riverConnections }, 3)

/**
 * Named accessors for [TileEntry.riverCrossingFlags]'s 8 documented bits (compass directions).
 * A forum correction confirmed by the original documentation's author swapped this field's
 * meaning with what was originally posted as "river source info" — these names reflect the
 * corrected (final) understanding.
 */
val TileEntry.crossingN: Boolean by BitCollection.byte.extensionBitFlag({ riverCrossingFlags }, 0)
val TileEntry.crossingNe: Boolean by BitCollection.byte.extensionBitFlag({ riverCrossingFlags }, 1)
val TileEntry.crossingE: Boolean by BitCollection.byte.extensionBitFlag({ riverCrossingFlags }, 2)
val TileEntry.crossingSe: Boolean by BitCollection.byte.extensionBitFlag({ riverCrossingFlags }, 3)
val TileEntry.crossingS: Boolean by BitCollection.byte.extensionBitFlag({ riverCrossingFlags }, 4)
val TileEntry.crossingSw: Boolean by BitCollection.byte.extensionBitFlag({ riverCrossingFlags }, 5)
val TileEntry.crossingW: Boolean by BitCollection.byte.extensionBitFlag({ riverCrossingFlags }, 6)
val TileEntry.crossingNw: Boolean by BitCollection.byte.extensionBitFlag({ riverCrossingFlags }, 7)

/**
 * Named accessors for 5 of [TileEntry.c3cOverlays]'s 32 bits (see [TileEntry.c3cOverlays]'s own
 * KDoc). The remaining bits are unexplained.
 */
val TileEntry.craters: Boolean by BitCollection.int.extensionBitFlag({ c3cOverlays.toIntLe() }, 16)
val TileEntry.barricade: Boolean by BitCollection.int.extensionBitFlag({ c3cOverlays.toIntLe() }, 28)
val TileEntry.airfield: Boolean by BitCollection.int.extensionBitFlag({ c3cOverlays.toIntLe() }, 29)
val TileEntry.radarTower: Boolean by BitCollection.int.extensionBitFlag({ c3cOverlays.toIntLe() }, 30)
val TileEntry.outpost: Boolean by BitCollection.int.extensionBitFlag({ c3cOverlays.toIntLe() }, 31)
