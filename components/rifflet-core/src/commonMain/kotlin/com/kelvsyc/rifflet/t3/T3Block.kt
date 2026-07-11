package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.core.Chunk
import com.kelvsyc.rifflet.core.ChunkId

/**
 * Base interface for all blocks in a T3 VM image file.
 */
sealed interface T3Block : Chunk {
    override val chunkId: ChunkId
}
