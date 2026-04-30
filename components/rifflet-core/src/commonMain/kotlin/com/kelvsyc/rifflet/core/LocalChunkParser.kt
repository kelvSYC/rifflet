package com.kelvsyc.rifflet.core

import okio.ByteString

/**
 * Parses the raw binary body of a local (non-group) chunk into a domain object of type [T].
 *
 * Shared by all format-specific local chunk parsing pipelines (IFF, RIFF, etc.).
 */
interface LocalChunkParser<T> {
    /**
     * Parses the data from a local chunk.
     *
     * @param data The raw binary data of the chunk body.
     */
    fun parse(data: ByteString): T
}
