package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `FLAV` section: the file's set of AI flavor groups.
 */
data class FlavSection(val entries: List<FlavGroupEntry>) : Civ3Section {
    override val chunkId: ChunkId get() = Civ3SectionIds.FLAV
}
