package com.kelvsyc.rifflet.iff

import com.kelvsyc.collections.ListMultimap
import com.kelvsyc.rifflet.core.ChunkEncoder
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RiffletEncodeException
import com.kelvsyc.rifflet.internal.iff.writeIffChunk
import okio.Buffer

/**
 * Encodes a domain object of type [T] into the local-chunk content of a `PROP` chunk.
 *
 * This interface does not extend [ChunkEncoder] because a `PROP` body is not itself a standalone
 * chunk — it is the content written after the outer `PROP` type and size header and the 4-byte
 * form-type field. [ChunkEncoder] models a complete chunk (header + body); [PropBodyEncoder] models
 * only the body. The outer `PROP` header and the form-type field are written by the caller.
 *
 * A `PROP` body may contain only local (non-group) chunks. Any key returned by the disassembler
 * that has no registered local encoder will cause a [RiffletEncodeException] to be thrown;
 * group encoders registered in the same core are not consulted.
 *
 * **Format grammar is the caller's responsibility.** The IFF spec defines the mechanism — PROP
 * chunks supply default local chunks for all enclosed `FORM` chunks of the matching form-type — but
 * it does not dictate which chunk types a given format places in a `PROP` versus in each individual
 * `FORM`. Callers must respect the grammar of the specific IFF-based format being written.
 *
 * Use the companion [invoke] to construct a standard implementation backed by a disassembler lambda.
 */
interface PropBodyEncoder<T> {
    /**
     * Encodes [value] into [destination] as the local-chunk content of a `PROP` chunk.
     *
     * Writes all local child chunks with their IFF headers into [destination]. The caller is
     * responsible for writing the outer `PROP` type ID, the body-size header, and the 4-byte
     * form-type field.
     */
    fun encode(value: T, destination: Buffer)

    companion object {
        /**
         * Creates a [PropBodyEncoder] that disassembles a domain object using [disassembler] and
         * dispatches each local chunk through [core].
         *
         * Only [core]'s local encoders are consulted; form, list, and cat encoders registered in
         * [core] are ignored. If the disassembler returns a key with no corresponding local encoder,
         * a [RiffletEncodeException] is thrown.
         *
         * @param core Private encoder core used to dispatch local chunks.
         * @param disassembler Breaks a domain object of type [T] into a multimap of `(typeId, value)`
         *   pairs for the local child chunks.
         */
        operator fun <T> invoke(
            core: IffEncoderCore,
            disassembler: (T) -> ListMultimap<ChunkId, Any>,
        ): PropBodyEncoder<T> = PropBodyEncoderImpl(core, disassembler)
    }
}

@Suppress("UNCHECKED_CAST")
private class PropBodyEncoderImpl<T>(
    private val core: IffEncoderCore,
    private val disassembler: (T) -> ListMultimap<ChunkId, Any>,
) : PropBodyEncoder<T> {

    override fun encode(value: T, destination: Buffer) {
        for ((key, childValue) in disassembler(value).entries) {
            val encoder = core.localEncoders[key] as? ChunkEncoder<Any>
                ?: throw RiffletEncodeException("No local encoder registered for chunk type '${key.name}' in PROP body")
            val body = Buffer()
            encoder.encode(childValue, body)
            writeIffChunk(key, body, destination)
        }
    }
}
