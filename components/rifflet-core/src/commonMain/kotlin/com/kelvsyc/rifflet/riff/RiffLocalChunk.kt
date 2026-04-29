package com.kelvsyc.rifflet.riff

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RawChunk

/**
 * A [RiffChunk] representing a local (data) chunk — any chunk that is not a [RiffGroupChunk].
 * It is a wrapper around a [RawChunk] that satisfies the [RiffChunk] interface.
 */
@JvmInline
value class RiffLocalChunk(val data: RawChunk) : RiffChunk {
    override val chunkId: ChunkId get() = data.type
}
