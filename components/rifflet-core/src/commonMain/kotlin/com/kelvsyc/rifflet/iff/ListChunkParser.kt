package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `ListChunkParser` interface is used to translate a [ListChunk] into a domain object. A `ListChunkParser` is only
 * required to process a chunk based on a specific [ListChunk] based on a specific type tag.
 */
interface ListChunkParser<T> {
    /**
     * Parses the chunks from a `LIST` chunk into a domain object.
     *
     * @param chunks The sub-chunks of the supplied `LIST` chunk
     * @param properties Any local properties passed in from outer `LIST` chunks
     */
    fun parse(chunks: List<GroupChunk>, properties: Map<ChunkId, List<LocalChunk>> = emptyMap()): List<T>
}
