package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.core.ChunkId

data class McldBlock(val entries: List<McldEntry>) : T3Block {
    override val chunkId: ChunkId get() = T3BlockIds.MCLD

    fun entryForIndex(index: Int): McldEntry? = entries.getOrNull(index)

    fun indexOf(name: String): Int? = entries.indexOfFirst { it.name == name }.takeIf { it >= 0 }
}
