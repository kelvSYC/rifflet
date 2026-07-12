package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `WSIZ` section: the file's set of available world-size presets.
 */
data class WsizSection(val entries: List<WsizEntry>) : Civ3Section {
    override val chunkId: ChunkId get() = Civ3SectionIds.WSIZ
}
