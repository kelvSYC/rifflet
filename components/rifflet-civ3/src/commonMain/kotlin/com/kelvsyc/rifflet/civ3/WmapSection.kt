package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `WMAP` section: the file's world-map generation settings.
 */
data class WmapSection(val entries: List<WmapEntry>) : Civ3Section {
    override val chunkId: ChunkId get() = Civ3SectionIds.WMAP
}
