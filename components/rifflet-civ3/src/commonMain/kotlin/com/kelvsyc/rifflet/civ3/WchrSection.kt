package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `WCHR` section: the file's world-generation settings.
 */
data class WchrSection(val entries: List<WchrEntry>) : Civ3Section {
    override val chunkId: ChunkId get() = Civ3SectionIds.WCHR
}
