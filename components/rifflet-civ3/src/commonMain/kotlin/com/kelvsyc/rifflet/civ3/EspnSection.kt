package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `ESPN` section: the file's set of diplomat/spy espionage mission types.
 */
data class EspnSection(val entries: List<EspnEntry>) : Civ3Section {
    override val chunkId: ChunkId get() = Civ3SectionIds.ESPN
}
