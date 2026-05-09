package com.kelvsyc.rifflet.riff

import com.kelvsyc.kotlin.core.collections.ListMultimap
import com.kelvsyc.rifflet.core.ChunkEncoder
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RiffletEncodeException
import com.kelvsyc.rifflet.internal.riff.writeRiffChunk
import com.kelvsyc.rifflet.internal.riff.writeRiffGroupChunk
import okio.Buffer

/**
 * Encodes a domain object of type [T] into the sub-chunk content of a `RIFF` container.
 *
 * Does not extend [ChunkEncoder] — a `RIFF` body is not itself a standalone chunk. The outer
 * `RIFF` type ID, little-endian body size, and 4-byte form-type field are written by the caller.
 *
 * Use the companion [invoke] to construct a standard implementation.
 */
interface RiffFormBodyEncoder<T> {
    fun encode(value: T, destination: Buffer)

    companion object {
        /**
         * Creates a [RiffFormBodyEncoder] that disassembles [T] via [disassembler] and dispatches
         * each child chunk through [core].
         *
         * Local chunks, nested `LIST` containers, and nested `RIFF` containers are all dispatched
         * (in that order). If no encoder is registered for a given type, [RiffletEncodeException]
         * is thrown.
         */
        operator fun <T> invoke(
            core: RiffEncoderCore,
            disassembler: (T) -> ListMultimap<ChunkId, Any>,
        ): RiffFormBodyEncoder<T> = RiffFormBodyEncoderImpl(core, disassembler)
    }
}

@Suppress("UNCHECKED_CAST")
private class RiffFormBodyEncoderImpl<T>(
    private val core: RiffEncoderCore,
    private val disassembler: (T) -> ListMultimap<ChunkId, Any>,
) : RiffFormBodyEncoder<T> {

    override fun encode(value: T, destination: Buffer) {
        for ((key, childValue) in disassembler(value).entries) {
            when {
                key in core.localEncoders -> {
                    val encoder = core.localEncoders.getValue(key) as ChunkEncoder<Any>
                    val body = Buffer()
                    encoder.encode(childValue, body)
                    writeRiffChunk(key, body, destination)
                }
                key in core.listEncoders -> {
                    val encoder = core.listEncoders.getValue(key) as RiffListBodyEncoder<Any>
                    val innerBody = Buffer()
                    encoder.encode(childValue, innerBody)
                    writeRiffGroupChunk(RiffChunkIds.LIST, key, innerBody, destination)
                }
                else -> throw RiffletEncodeException("No encoder registered for chunk type '${key.name}'")
            }
        }
    }
}
