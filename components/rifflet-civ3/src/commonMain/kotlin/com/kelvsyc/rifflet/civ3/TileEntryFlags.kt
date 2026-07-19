package com.kelvsyc.rifflet.civ3

/**
 * Named accessors for [TileEntry.overlayFlags]'s 8 documented bits, per Apolyton's
 * "Civilization III BIX/BIQ file format" thread.
 */
val TileEntry.road: Boolean get() = overlayFlags.toInt() and (1 shl 0) != 0
val TileEntry.railroad: Boolean get() = overlayFlags.toInt() and (1 shl 1) != 0
val TileEntry.mine: Boolean get() = overlayFlags.toInt() and (1 shl 2) != 0
val TileEntry.irrigation: Boolean get() = overlayFlags.toInt() and (1 shl 3) != 0
val TileEntry.fortress: Boolean get() = overlayFlags.toInt() and (1 shl 4) != 0
val TileEntry.goodyHuts: Boolean get() = overlayFlags.toInt() and (1 shl 5) != 0
val TileEntry.pollution: Boolean get() = overlayFlags.toInt() and (1 shl 6) != 0
val TileEntry.barbarianCamp: Boolean get() = overlayFlags.toInt() and (1 shl 7) != 0

/**
 * Named accessors for [TileEntry.bonusFlags]'s 4 documented bits. The bit positions are
 * non-contiguous (0, 3, 4, 5) exactly as documented — not a transcription error.
 */
val TileEntry.bonusGrassland: Boolean get() = bonusFlags.toInt() and (1 shl 0) != 0
val TileEntry.playerStart: Boolean get() = bonusFlags.toInt() and (1 shl 3) != 0
val TileEntry.snowCappedMountains: Boolean get() = bonusFlags.toInt() and (1 shl 4) != 0
val TileEntry.pineForest: Boolean get() = bonusFlags.toInt() and (1 shl 5) != 0

/**
 * Named accessors for [TileEntry.riverConnections]'s 4 documented bits, per Apolyton's
 * "Civilization III BIX/BIQ file format" thread.
 */
val TileEntry.riverInNorth: Boolean get() = riverConnections.toInt() and (1 shl 0) != 0
val TileEntry.riverInWest: Boolean get() = riverConnections.toInt() and (1 shl 1) != 0
val TileEntry.riverInEast: Boolean get() = riverConnections.toInt() and (1 shl 2) != 0
val TileEntry.riverInSouth: Boolean get() = riverConnections.toInt() and (1 shl 3) != 0

/**
 * Named accessors for [TileEntry.riverCrossingFlags]'s 8 documented bits (compass directions).
 * A 2004 forum correction confirmed by the original thread author swapped this field's meaning
 * with what was originally posted as "river source info" — these names reflect the corrected
 * (final) understanding.
 */
val TileEntry.crossingN: Boolean get() = riverCrossingFlags.toInt() and (1 shl 0) != 0
val TileEntry.crossingNe: Boolean get() = riverCrossingFlags.toInt() and (1 shl 1) != 0
val TileEntry.crossingE: Boolean get() = riverCrossingFlags.toInt() and (1 shl 2) != 0
val TileEntry.crossingSe: Boolean get() = riverCrossingFlags.toInt() and (1 shl 3) != 0
val TileEntry.crossingS: Boolean get() = riverCrossingFlags.toInt() and (1 shl 4) != 0
val TileEntry.crossingSw: Boolean get() = riverCrossingFlags.toInt() and (1 shl 5) != 0
val TileEntry.crossingW: Boolean get() = riverCrossingFlags.toInt() and (1 shl 6) != 0
val TileEntry.crossingNw: Boolean get() = riverCrossingFlags.toInt() and (1 shl 7) != 0
