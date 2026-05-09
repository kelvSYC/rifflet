package com.kelvsyc.rifflet.riff

import com.kelvsyc.kotlin.core.collections.ListMultimap
import com.kelvsyc.rifflet.core.ChunkId

/**
 * Translates the sub-chunks of a `RIFF` container into a domain object of type [T].
 */
interface RiffFormChunkParser<T> {
    /**
     * Parses the sub-chunks of a `RIFF` container into a domain object.
     *
     * @param chunks The sub-chunks of the `RIFF` container, keyed by [RiffChunk.chunkId].
     */
    fun parse(chunks: ListMultimap<ChunkId, RiffChunk>): T
}
