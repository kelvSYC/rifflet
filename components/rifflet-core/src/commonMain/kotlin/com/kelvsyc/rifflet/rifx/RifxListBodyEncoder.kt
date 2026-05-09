package com.kelvsyc.rifflet.rifx

import com.kelvsyc.kotlin.core.collections.ListMultimap
import com.kelvsyc.rifflet.core.ChunkEncoder
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RiffletEncodeException
import com.kelvsyc.rifflet.internal.rifx.writeRifxChunk
import com.kelvsyc.rifflet.internal.rifx.writeRifxGroupChunk
import okio.Buffer

/**
 * Encodes a domain object of type [T] into the sub-chunk content of a `LIST` container.
 *
 * Does not extend [ChunkEncoder] — a `LIST` body is not itself a standalone chunk. The outer
 * `LIST` type ID, big-endian body size, and 4-byte list-type field are written by the caller.
 *
 * Unlike IFF `LIST`, a RIFX `LIST` may contain both local chunks and nested `LIST` containers.
 *
 * Use the companion [invoke] to construct a standard implementation.
 */
interface RifxListBodyEncoder<T> {
    fun encode(value: T, destination: Buffer)

    companion object {
        /**
         * Creates a [RifxListBodyEncoder] that disassembles [T] via [disassembler] and dispatches
         * each child chunk through [core].
         *
         * Local chunks and nested `LIST` containers are dispatched. If no encoder is registered
         * for a given type, [RiffletEncodeException] is thrown.
         */
        operator fun <T> invoke(
            core: RifxEncoderCore,
            disassembler: (T) -> ListMultimap<ChunkId, Any>,
        ): RifxListBodyEncoder<T> = RifxListBodyEncoderImpl(core, disassembler)
    }
}

@Suppress("UNCHECKED_CAST")
private class RifxListBodyEncoderImpl<T>(
    private val core: RifxEncoderCore,
    private val disassembler: (T) -> ListMultimap<ChunkId, Any>,
) : RifxListBodyEncoder<T> {

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
