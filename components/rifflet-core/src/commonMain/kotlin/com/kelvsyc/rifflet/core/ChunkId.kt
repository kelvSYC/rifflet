package com.kelvsyc.rifflet.core

import okio.ByteString
import okio.ByteString.Companion.encodeUtf8

/**
 * A `ChunkId` is a four-byte structure representing the format and purpose of an individual chunk. The chunk ID is
 * commonly represented a four-byte string.
 *
 * @param data  The raw data representation of the chunk ID.
 */
data class ChunkId(val data: ByteString) {
    init {
        check(data.size == 4) { "Chunk IDs must have 4 bytes." }
    }

    /**
     * Creates a `ChunkId` from a [String] representation.
     */
    constructor(name: String) : this(name.encodeUtf8())

    /**
     * Returns the chunk ID as a [String] representation.
     */
    val name: String
        get() = data.utf8()
}
