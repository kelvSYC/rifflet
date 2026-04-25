package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkId

/**
 * Generic representation of an IFF Chunk. There are two subtypes of IFF Chunks used by Rifflet: the
 * implementation-specific [LocalChunk], and the [StandardIffChunk] specified by the Interchange File Format standard.
 */
sealed interface IffChunk {
    /**
     * The key used to identify and route this chunk. For most chunk types this is the IFF type identifier; for
     * [CatChunk] it is the [hint][CatChunk.hint] field, consistent with how CAT chunks are dispatched to parsers.
     */
    val chunkId: ChunkId
}
