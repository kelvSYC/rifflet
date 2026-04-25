package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `ListChunkParser` interface is used to translate a [ListChunk] into a domain object. A `ListChunkParser` is only
 * required to process a [ListChunk] with a specific type tag.
 */
interface ListChunkParser<T> {
    /**
     * Parses the items of a `LIST` chunk into a domain object.
     *
     * @param chunks The group chunk items of the supplied `LIST` chunk.
     * @param properties Local properties passed in from an outer `LIST` or `CAT ` chunk, keyed by form type.
     * @return The assembled domain object of type [T].
     */
    fun parse(chunks: List<GroupChunk>, properties: Map<ChunkId, List<LocalChunk>> = emptyMap()): T
}
