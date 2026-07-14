package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `UNIT` section: the file's set of placed unit instances.
 */
data class UnitSection(val entries: List<UnitEntry>) : Civ3Section {
    override val chunkId: ChunkId get() = Civ3SectionIds.UNIT
}
