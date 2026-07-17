package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `BLDG` section: the file's set of building and wonder definitions.
 */
data class BldgSection(val entries: List<BldgEntry>) : Civ3Section {
    override val chunkId: ChunkId get() = Civ3SectionIds.BLDG
}
