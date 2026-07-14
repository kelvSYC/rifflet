package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `TFRM` section: the file's set of worker jobs.
 */
data class TfrmSection(val entries: List<TfrmEntry>) : Civ3Section {
    override val chunkId: ChunkId get() = Civ3SectionIds.TFRM
}
