package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `TERR` section: the file's set of terrain type definitions.
 */
data class TerrSection(val entries: List<TerrEntry>) : Civ3Section {
    override val chunkId: ChunkId get() = Civ3SectionIds.TERR
}
