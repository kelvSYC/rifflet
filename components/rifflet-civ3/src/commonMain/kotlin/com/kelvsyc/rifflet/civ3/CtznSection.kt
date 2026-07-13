package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `CTZN` section: the file's set of citizen types.
 */
data class CtznSection(val entries: List<CtznEntry>) : Civ3Section {
    override val chunkId: ChunkId get() = Civ3SectionIds.CTZN
}
