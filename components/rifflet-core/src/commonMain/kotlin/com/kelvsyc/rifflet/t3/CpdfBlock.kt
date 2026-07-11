package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.core.ChunkId

data class CpdfBlock(val pools: List<CpdfPoolEntry>) : T3Block {
    override val chunkId: ChunkId get() = T3BlockIds.CPDF
}
