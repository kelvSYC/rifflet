package com.kelvsyc.rifflet.core

import okio.ByteString

/**
 * A raw chunk of binary data consisting of a type identifier and a byte payload.
 *
 * Implements [Chunk] directly; [chunkId] delegates to [type].
 */
data class RawChunk(val type: ChunkId, val data: ByteString) : Chunk {
    override val chunkId: ChunkId get() = type
}
