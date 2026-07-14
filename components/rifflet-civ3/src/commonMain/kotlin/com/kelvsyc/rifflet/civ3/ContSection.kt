package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `CONT` section: the file's set of continents and water bodies.
 */
data class ContSection(val entries: List<ContEntry>) : Civ3Section {
    override val chunkId: ChunkId get() = Civ3SectionIds.CONT
}
