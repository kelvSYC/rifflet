package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `ERAS` section: the file's set of game eras.
 */
data class ErasSection(val entries: List<ErasEntry>) : Civ3Section {
    override val chunkId: ChunkId get() = Civ3SectionIds.ERAS
}
