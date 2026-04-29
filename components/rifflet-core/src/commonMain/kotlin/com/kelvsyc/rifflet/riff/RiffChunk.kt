package com.kelvsyc.rifflet.riff

import com.kelvsyc.rifflet.core.Chunk
import com.kelvsyc.rifflet.core.ChunkId

/**
 * Base interface for all RIFF chunks.
 *
 * There are two subtypes: [RiffLocalChunk] for data chunks, and [RiffGroupChunk] for container
 * chunks (`RIFF` and `LIST`).
 */
sealed interface RiffChunk : Chunk {
    override val chunkId: ChunkId
}
