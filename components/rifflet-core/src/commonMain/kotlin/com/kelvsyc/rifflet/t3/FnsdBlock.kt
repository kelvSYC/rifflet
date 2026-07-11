package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.core.ChunkId

data class FnsdBlock(val functionSets: List<String>) : T3Block {
    override val chunkId: ChunkId get() = T3BlockIds.FNSD
}
