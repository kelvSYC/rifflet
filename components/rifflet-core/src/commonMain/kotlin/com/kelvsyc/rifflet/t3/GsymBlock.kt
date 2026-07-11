package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.core.ChunkId

data class GsymBlock(val entries: List<GsymEntry>) : T3Block {
    override val chunkId: ChunkId get() = T3BlockIds.GSYM
}
