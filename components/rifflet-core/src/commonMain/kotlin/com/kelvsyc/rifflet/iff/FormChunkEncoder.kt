package com.kelvsyc.rifflet.iff

import com.kelvsyc.collections.ListMultimap
import com.kelvsyc.rifflet.core.ChunkEncoder
import com.kelvsyc.rifflet.core.ChunkId

/**
 * Translates a domain object of type [T] into the typed children of a `FORM` chunk.
 *
 * Unlike [ChunkEncoder], which writes raw bytes into a buffer, a `FormChunkEncoder` produces a structured
 * multimap of typed child values. The encoder infrastructure ([IffEncoderCore]) is responsible for
 * serializing each child using its registered encoder and assembling the `FORM` chunk body, including
 * the format-specific framing (chunk headers, byte order, even-alignment padding).
 */
interface FormChunkEncoder<T> {
    /**
     * Disassembles [value] into a multimap of typed child chunks, keyed by [ChunkId].
     *
     * Each entry is dispatched by [IffEncoderCore] to the appropriate registered encoder.
     */
    fun encode(value: T): ListMultimap<ChunkId, Any>
}
