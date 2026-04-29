package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.Chunk
import com.kelvsyc.rifflet.core.ChunkId

/**
 * Base interface for all IFF chunks. There are two subtypes: the implementation-specific [LocalChunk], and the
 * [StandardIffChunk] specified by the Interchange File Format standard.
 */
sealed interface IffChunk : Chunk {
    /**
     * The key used to identify and route this chunk. For most chunk types this is the IFF type identifier; for
     * [CatChunk] it is the [hint][CatChunk.hint] field, consistent with how CAT chunks are dispatched to parsers.
     */
    override val chunkId: ChunkId
}
