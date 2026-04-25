package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkId

/**
 * Class representing a blank chunk, with a type ID of `    ` (4 spaces). According to the IFF standard, the data within
 * a blank chunk is to be entirely disregarded.
 *
 * Because the IFF standard requires a padding byte if a chunk is of odd length, an odd-[size] blank chunk is
 * functionally identical to a blank chunk of 1 additional byte.
 */
data class BlankChunk(val size: UInt) : StandardIffChunk {
    override val chunkId: ChunkId get() = IffChunkIds.blank
}
