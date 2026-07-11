package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.core.ChunkId

data class MacrBlock(val entries: List<MacrEntry>) : T3Block {
    override val chunkId: ChunkId get() = T3BlockIds.MACR
}
