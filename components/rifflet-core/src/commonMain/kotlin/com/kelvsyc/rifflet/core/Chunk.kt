package com.kelvsyc.rifflet.core

/**
 * Base interface for any chunk in a chunk-based binary format such as IFF or RIFF.
 *
 * Format-specific chunk hierarchies (e.g. [com.kelvsyc.rifflet.iff.IffChunk]) extend this
 * interface. [RawChunk] implements it directly, allowing raw data chunks to participate in any
 * format's chunk hierarchy without wrapping.
 */
interface Chunk {
    /**
     * The key used to identify and route this chunk within its format's parser or encoder.
     */
    val chunkId: ChunkId
}
