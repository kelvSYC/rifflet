package com.kelvsyc.rifflet.iff

import okio.ByteString

/**
 * The `LocalChunkParser` interface is used to translate a user-defined chunk type ([LocalChunk]) into a domain object.
 */
interface LocalChunkParser<T> {
    /**
     * Parses the data from the user-defined chunk.
     *
     * @param data The raw binary data of the chunk.
     */
    fun parse(data: ByteString): T
}
