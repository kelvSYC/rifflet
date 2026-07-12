package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `DIFF` section: the file's set of available difficulty levels.
 */
data class DiffSection(val entries: List<DiffEntry>) : Civ3Section {
    override val chunkId: ChunkId get() = Civ3SectionIds.DIFF
}
