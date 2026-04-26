package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkEncoder
import com.kelvsyc.rifflet.core.ChunkId

/**
 * Translates a domain object of type [T] into the ordered children of a `LIST` chunk.
 *
 * Unlike [ChunkEncoder], which writes raw bytes into a buffer, a `ListChunkEncoder` produces a structured
 * ordered list of typed child values. Each entry is a [Pair] of a [ChunkId] and a domain value; the
 * [ChunkId] is used by [IffEncoderCore] to look up the registered encoder for that child, and must
 * correspond to an encoder registered in the core (form, list, or cat).
 *
 * PROP extraction is not part of this interface. No round-trip guarantee for PROP data is provided.
 */
interface ListChunkEncoder<T> {
    /**
     * Disassembles [value] into an ordered list of `(chunkId, value)` pairs for child group chunks.
     *
     * Each [ChunkId] identifies which registered encoder the core should dispatch the paired value to.
     */
    fun encode(value: T): List<Pair<ChunkId, Any>>
}
