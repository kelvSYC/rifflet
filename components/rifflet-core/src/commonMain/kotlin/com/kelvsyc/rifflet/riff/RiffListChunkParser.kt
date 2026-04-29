package com.kelvsyc.rifflet.riff

import com.kelvsyc.collections.ListMultimap
import com.kelvsyc.rifflet.core.ChunkId

/**
 * Translates the sub-chunks of a `LIST` container into a domain object of type [T].
 */
interface RiffListChunkParser<T> {
    /**
     * Parses the sub-chunks of a `LIST` container into a domain object.
     *
     * @param chunks The sub-chunks of the `LIST` container, keyed by [RiffChunk.chunkId].
     */
    fun parse(chunks: ListMultimap<ChunkId, RiffChunk>): T
}
