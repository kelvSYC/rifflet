package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `GOVT` section: the file's set of available government types.
 */
data class GovtSection(val entries: List<GovtEntry>) : Civ3Section {
    override val chunkId: ChunkId get() = Civ3SectionIds.GOVT
}
