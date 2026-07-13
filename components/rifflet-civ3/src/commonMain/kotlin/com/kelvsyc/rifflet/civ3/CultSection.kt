package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `CULT` section: the file's set of culture-opinion levels.
 */
data class CultSection(val entries: List<CultEntry>) : Civ3Section {
    override val chunkId: ChunkId get() = Civ3SectionIds.CULT
}
