package com.kelvsyc.rifflet.civ3

/**
 * Resolves each id in [WmapEntry.resourceIds] against [goods], preserving position: the result
 * is the same length as [WmapEntry.resourceIds], with `null` at any position whose id doesn't
 * resolve. Likely `GOOD` section indices identifying which resources are available on this map —
 * inferred from a separate reverse-engineered reference implementation's comment; not confirmed
 * by existing reverse-engineering documentation.
 */
fun WmapEntry.resourceIdsGood(goods: List<GoodEntry>): List<GoodEntry?> =
    resourceIds.map { goods.getOrNull(it) }

/**
 * The `TILE` section index for the tile at `(x, y)` on this world map — Civ3's isometric internal
 * map storage, per [TileSection]'s own KDoc (`width × height / 2` entries total).
 *
 * `x` and `y` must share parity for this to identify the tile's own canonical entry; verified
 * against real map-editor coordinates for placed cities and colonies (see `TileEntryParser`'s
 * KDoc for the `city`/`colony` byte-order this was originally confirmed alongside). Real official
 * content is not fully exempt from odd-parity `(x, y)` pairs — see the coordinate-parity
 * validation rules for the one known, harmless exception — but this formula still resolves such a
 * pair to *some* tile (the one immediately adjacent, since the low bit of `x` is discarded by the
 * division below), never out of bounds for a valid `x`/`y` within the map.
 */
fun WmapEntry.tileIndex(x: Int, y: Int): Int = isometricTileIndex(width, x, y)

/**
 * The `(x, y)` map coordinate of the tile at [index] within [TileSection.entries] — the inverse of
 * [tileIndex].
 */
fun WmapEntry.tileCoordinates(index: Int): Pair<Int, Int> = isometricTileCoordinates(width, index)

/**
 * The `TILE` section indices of the tile at `(x, y)`'s up to 8 isometric neighbors, per
 * [xWrapping]/[yWrapping]: a neighbor past a wrapping edge is computed modulo
 * [WmapEntry.width]/[WmapEntry.height]; a
 * neighbor past a non-wrapping edge is omitted (there is no tile there).
 */
fun WmapEntry.neighborTileIndices(x: Int, y: Int): List<Int> = ISOMETRIC_NEIGHBOR_DELTAS.mapNotNull { (dx, dy) ->
    val nx = if (xWrapping) (x + dx).mod(width) else x + dx
    val ny = if (yWrapping) (y + dy).mod(height) else y + dy
    if (nx !in 0 until width || ny !in 0 until height) null else tileIndex(nx, ny)
}
