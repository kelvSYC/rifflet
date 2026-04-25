package com.kelvsyc.rifflet.iff

import com.kelvsyc.collections.ListMultimap
import com.kelvsyc.rifflet.core.ChunkId

data class FormChunk(val type: ChunkId, val chunks: ListMultimap<ChunkId, IffChunk>) : GroupChunk {
    override val chunkId: ChunkId get() = type
}
