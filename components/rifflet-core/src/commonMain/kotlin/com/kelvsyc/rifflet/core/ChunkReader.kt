package com.kelvsyc.rifflet.core

import okio.Source

/**
 * A `ChunkReader` is an object that can read a single [RawChunk] from a given [Source].
 */
interface ChunkReader {
    /**
     * Reads a chunk from the specified source.
     */
    fun read(source: Source): RawChunk
}
