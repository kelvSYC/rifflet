package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `CatChunkParser` interface is used to translate a [CatChunk] into a domain object. A `CatChunkParser` is only
 * required to process a [CatChunk] with a specific type hint.
 */
interface CatChunkParser<T> {
    /**
     * Parses the chunks from a `CAT ` chunk into a domain object.
     *
     * @param chunks The sub-chunks of the supplied `CAT ` chunk
     * @param properties Any local properties passed in from outer `LIST` chunks
     */
    fun parse(chunks: List<GroupChunk>, properties: Map<ChunkId, List<LocalChunk>> = emptyMap()): T
}
