package com.kelvsyc.rifflet.rifx

import com.kelvsyc.collections.ListMultimap
import com.kelvsyc.rifflet.core.ChunkEncoder
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RiffletEncodeException
import com.kelvsyc.rifflet.internal.rifx.writeRifxChunk
import com.kelvsyc.rifflet.internal.rifx.writeRifxGroupChunk
import okio.Buffer

/**
 * Encodes a domain object of type [T] into the sub-chunk content of a `RIFX` container.
 *
 * Does not extend [ChunkEncoder] — a `RIFX` body is not itself a standalone chunk. The outer
 * `RIFX` type ID, big-endian body size, and 4-byte form-type field are written by the caller.
 *
 * Use the companion [invoke] to construct a standard implementation.
 */
interface RifxFormBodyEncoder<T> {
    fun encode(value: T, destination: Buffer)

    companion object {
        /**
         * Creates a [RifxFormBodyEncoder] that disassembles [T] via [disassembler] and dispatches
         * each child chunk through [core].
         *
         * Local chunks and nested `LIST` containers are dispatched. If no encoder is registered
         * for a given type, [RiffletEncodeException] is thrown.
         */
        operator fun <T> invoke(
            core: RifxEncoderCore,
            disassembler: (T) -> ListMultimap<ChunkId, Any>,
        ): RifxFormBodyEncoder<T> = RifxFormBodyEncoderImpl(core, disassembler)
    }
}

@Suppress("UNCHECKED_CAST")
private class RifxFormBodyEncoderImpl<T>(
    private val core: RifxEncoderCore,
    private val disassembler: (T) -> ListMultimap<ChunkId, Any>,
) : RifxFormBodyEncoder<T> {

    override fun encode(value: T, destination: Buffer) {
        for ((key, childValue) in disassembler(value).entries) {
            when {
                key in core.localEncoders -> {
                    val encoder = core.localEncoders.getValue(key) as ChunkEncoder<Any>
                    val body = Buffer()
                    encoder.encode(childValue, body)
                    writeRifxChunk(key, body, destination)
                }
                key in core.listEncoders -> {
                    val encoder = core.listEncoders.getValue(key) as RifxListBodyEncoder<Any>
                    val innerBody = Buffer()
                    encoder.encode(childValue, innerBody)
                    writeRifxGroupChunk(RifxChunkIds.LIST, key, innerBody, destination)
                }
                else -> throw RiffletEncodeException("No encoder registered for chunk type '${key.name}'")
            }
        }
    }
}
