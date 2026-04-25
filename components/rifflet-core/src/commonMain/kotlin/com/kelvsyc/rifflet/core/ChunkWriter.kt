package com.kelvsyc.rifflet.core

import okio.Sink

/**
 * `ChunkWriter` represents objects that can write a chunk to a [Sink].
 */
interface ChunkWriter {
    /**
     * Writes a [RawChunk] to a destination [Sink].
     */
    fun writeChunk(chunk: RawChunk, destination: Sink)

    /**
     * Writes a [RawChunk] to a destination [Sink], If the size of the chunk's [binary data][RawChunk.data] is below
     * the specified size, padding bytes are added so that the chunk size matches the size reported.
     */
    fun writeChunk(chunk: RawChunk, size: Int, destination: Sink)
}
