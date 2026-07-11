package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.core.ChunkId
import okio.ByteString

/**
 * Fallback representation for any T3 block type not yet modeled with a dedicated type.
 *
 * @param flags The block header's raw flags field (bit 0 = mandatory-to-recognize).
 */
data class T3RawBlock(val type: ChunkId, val flags: Int, val data: ByteString) : T3Block {
    override val chunkId: ChunkId get() = type
}
