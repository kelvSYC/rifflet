package com.kelvsyc.rifflet.iff

import okio.ByteString

/**
 * The `LocalChunkParser` interface is used to translate a local chunk ([LocalChunk]) into a domain object.
 */
interface LocalChunkParser<T> {
    /**
     * Parses the data from a local chunk.
     *
     * @param data The raw binary data of the chunk.
     */
    fun parse(data: ByteString): T
}
