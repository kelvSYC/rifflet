package com.kelvsyc.rifflet.riff

import com.kelvsyc.kotlin.core.collections.ListMultimap
import com.kelvsyc.rifflet.core.ChunkId

/**
 * A `LIST` container chunk.
 *
 * Unlike IFF `LIST`, a RIFF `LIST` may contain any sub-chunk types — both local chunks and nested
 * `LIST` containers. [type] identifies the list type and is used as the dispatch key for registered
 * parsers. Sub-chunks are stored in [chunks] keyed by [RiffChunk.chunkId].
 */
data class RiffListChunk(
    val outerChunkId: ChunkId,
    val type: ChunkId,
    val chunks: ListMultimap<ChunkId, RiffChunk>,
) : RiffGroupChunk {
    override val chunkId: ChunkId get() = type
}
