package com.kelvsyc.rifflet.riff

import com.kelvsyc.collections.ListMultimap
import com.kelvsyc.rifflet.core.ChunkId

/**
 * A top-level `RIFF` container chunk.
 *
 * The [type] field (the form-type, e.g. `WAVE`, `AVI `) identifies the format and is used as the
 * dispatch key for registered parsers. [outerChunkId] is always [RiffChunkIds.RIFF] for standard
 * files. Sub-chunks are stored in [chunks] keyed by [RiffChunk.chunkId].
 */
data class RiffFormChunk(
    val outerChunkId: ChunkId,
    val type: ChunkId,
    val chunks: ListMultimap<ChunkId, RiffChunk>,
) : RiffGroupChunk {
    override val chunkId: ChunkId get() = type
}
