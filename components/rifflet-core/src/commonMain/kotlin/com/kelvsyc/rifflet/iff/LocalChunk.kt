package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RawChunk

/**
 * A `LocalChunk` is a type of [IffChunk] representing a local chunk — any chunk that is not a group chunk or a blank
 * chunk. It is effectively a wrapper around a [RawChunk] meant to comply with the [IffChunk] interface.
 */
@JvmInline
value class LocalChunk(val data: RawChunk) : IffChunk {
    override val chunkId: ChunkId get() = data.type
}
