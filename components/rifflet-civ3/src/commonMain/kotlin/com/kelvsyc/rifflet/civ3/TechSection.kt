package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `TECH` section: the file's set of civilization advances.
 */
data class TechSection(val entries: List<TechEntry>) : Civ3Section {
    override val chunkId: ChunkId get() = Civ3SectionIds.TECH
}
