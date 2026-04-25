package com.kelvsyc.rifflet.iff

import com.kelvsyc.collections.ListMultimap
import com.kelvsyc.collections.emptyListMultimap
import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `FormChunkParser` interface is used to translate a [FormChunk] into a domain object.
 */
interface FormChunkParser<T> {
    /**
     * Parses the chunks from a `FORM` chunk into a domain object.
     *
     * @param chunks The sub-chunks of the supplied `FORM` chunk, keyed by [IffChunk.chunkId].
     * @param properties Any local properties passed in from outer `LIST` chunks, keyed by sub-chunk type.
     */
    fun parse(chunks: ListMultimap<ChunkId, IffChunk>, properties: ListMultimap<ChunkId, LocalChunk> = emptyListMultimap()): T
}
