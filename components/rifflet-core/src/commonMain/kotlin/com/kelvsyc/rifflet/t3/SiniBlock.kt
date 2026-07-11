package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.core.ChunkId

data class SiniBlock(
    val staticCodePoolOffset: UInt,
    val entries: List<SiniEntry>,
) : T3Block {
    override val chunkId: ChunkId get() = T3BlockIds.SINI
}
