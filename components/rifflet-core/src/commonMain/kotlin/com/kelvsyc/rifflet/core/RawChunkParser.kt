package com.kelvsyc.rifflet.core

import okio.Source

/**
 * A `RawChunkParser` is a base interface that can extract [RawChunk] objects from a specific input source.
 */
interface RawChunkParser {
    /**
     * Parses a [RawChunk] from the specified input source.
     *
     * Implementations should not close the input source after a chunk has been read.
     */
    fun parse(source: Source): RawChunk
}
