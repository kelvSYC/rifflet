package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `GOOD` section: the file's set of tradeable natural resources.
 */
data class GoodSection(val entries: List<GoodEntry>) : Civ3Section {
    override val chunkId: ChunkId get() = Civ3SectionIds.GOOD
}
