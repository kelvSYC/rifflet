package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `CLNY` section: the file's set of colonies.
 */
data class ClnySection(val entries: List<ClnyEntry>) : Civ3Section {
    override val chunkId: ChunkId get() = Civ3SectionIds.CLNY
}
