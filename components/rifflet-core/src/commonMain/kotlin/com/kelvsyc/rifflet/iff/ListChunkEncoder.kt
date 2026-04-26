package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkEncoder

/**
 * Translates a domain object of type [T] into the ordered children of a `LIST` chunk.
 *
 * Unlike [ChunkEncoder], which writes raw bytes into a buffer, a `ListChunkEncoder` produces a structured
 * ordered list of typed child values. The encoder infrastructure ([IffEncoderCore]) is responsible for
 * serializing each child using its registered encoder and assembling the `LIST` chunk body, including
 * the format-specific framing (chunk headers, byte order, even-alignment padding).
 *
 * PROP extraction is not part of this interface. No round-trip guarantee for PROP data is provided.
 */
interface ListChunkEncoder<T> {
    /**
     * Disassembles [value] into an ordered list of typed child group chunks.
     *
     * Each entry is dispatched by [IffEncoderCore] to the appropriate registered encoder.
     */
    fun encode(value: T): List<Any>
}
