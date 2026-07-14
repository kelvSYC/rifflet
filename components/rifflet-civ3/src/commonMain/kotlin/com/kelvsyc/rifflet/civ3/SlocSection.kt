package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `SLOC` section: the file's set of starting locations.
 */
data class SlocSection(val entries: List<SlocEntry>) : Civ3Section {
    override val chunkId: ChunkId get() = Civ3SectionIds.SLOC
}
