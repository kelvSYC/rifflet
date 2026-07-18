package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `TILE` section: the file's per-map-tile terrain, overlay, and ownership data — one entry
 * per map tile. There are `width × height / 2` entries, matching Civ3's isometric internal map
 * storage (confirmed against real `WMAP` dimensions in the same files: a 140×140 `WMAP` produces
 * exactly 9800 `TILE` entries, a 100×100 `WMAP` produces exactly 5000).
 */
data class TileSection(val entries: List<TileEntry>) : Civ3Section {
    override val chunkId: ChunkId get() = Civ3SectionIds.TILE
}
