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
fun WmapEntry.tileIndex(x: Int, y: Int): Int =
    (y / 2) * width + (if (y % 2 == 1) width / 2 else 0) + x / 2
