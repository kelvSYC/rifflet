package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.core.ChunkId

data class SymdBlock(val entries: List<SymdEntry>) : T3Block {
    override val chunkId: ChunkId get() = T3BlockIds.SYMD
}
