package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.core.ChunkId

data class MhlsBlock(val methodAddresses: List<UInt>) : T3Block {
    override val chunkId: ChunkId get() = T3BlockIds.MHLS
}
