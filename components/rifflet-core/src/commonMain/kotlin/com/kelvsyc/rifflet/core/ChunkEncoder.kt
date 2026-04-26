package com.kelvsyc.rifflet.core

import okio.Buffer

/**
 * A `ChunkEncoder` is used to encode binary data into a specific binary format.
 */
interface ChunkEncoder<T> {
    /**
     * The type of chunk that will result from using this encoder.
     */
    val chunkId: ChunkId

    /**
     * Encodes the given value into the destination buffer. The destination buffer is guaranteed to be empty when
     * called.
     */
    fun encode(value: T, destination: Buffer)
}
