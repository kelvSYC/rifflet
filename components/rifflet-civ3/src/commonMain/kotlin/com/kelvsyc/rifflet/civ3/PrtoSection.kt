package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `PRTO` section: the file's set of unit type (prototype) definitions.
 */
data class PrtoSection(val entries: List<PrtoEntry>) : Civ3Section {
    override val chunkId: ChunkId get() = Civ3SectionIds.PRTO
}
