package com.kelvsyc.rifflet.iff

import com.kelvsyc.collections.ListMultimap
import com.kelvsyc.rifflet.core.ChunkEncoder
import com.kelvsyc.rifflet.core.RiffletEncodeException
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.internal.iff.writeGroupChunk
import com.kelvsyc.rifflet.internal.iff.writeIffChunk
import okio.Buffer

/**
 * Encodes a domain object of type [T] into the child content of a `FORM` chunk.
 *
 * This interface does not extend [ChunkEncoder] because a `FORM` body is not itself a chunk —
 * it is the content written after the outer `FORM` type and size header. [ChunkEncoder] models a
 * complete chunk (header + body); [FormBodyEncoder] models only the body.
 *
 * The [encode] method writes the child chunks of the `FORM` body — IFF-framed and appended in order —
 * directly into [destination]. The outer `FORM` header (outer type ID and body size) and the 4-byte
 * form-type field are written by the caller.
 *
 * PROP extraction is not part of this interface. No round-trip guarantee for PROP data is provided.
 *
 * Use the companion [invoke] to construct a standard implementation backed by a disassembler lambda.
 */
interface FormBodyEncoder<T> {
    /**
     * Encodes [value] into [destination] as the child content of a `FORM` chunk.
     *
     * Writes all child chunks with their IFF headers into [destination]. The caller is responsible
     * for writing the outer `FORM` type ID, the body-size header, and the 4-byte form-type field.
     */
    fun encode(value: T, destination: Buffer)

    companion object {
        /**
         * Creates a [FormBodyEncoder] that disassembles a domain object using [disassembler] and
         * dispatches each child through [core].
         *
         * Child dispatch checks [core]'s local, form, list, and cat encoder maps (in that order).
         * If no encoder is registered for a chunk type, a [RiffletEncodeException] is thrown.
         *
         * @param core Private encoder core used to dispatch child chunks.
         * @param disassembler Breaks a domain object of type [T] into a multimap of `(typeId, value)`
         *   pairs for the child chunks.
         */
        operator fun <T> invoke(
            core: IffEncoderCore,
            disassembler: (T) -> ListMultimap<ChunkId, Any>,
        ): FormBodyEncoder<T> = FormBodyEncoderImpl(core, disassembler)
    }
}

@Suppress("UNCHECKED_CAST")
private class FormBodyEncoderImpl<T>(
    private val core: IffEncoderCore,
    private val disassembler: (T) -> ListMultimap<ChunkId, Any>,
) : FormBodyEncoder<T> {

    override fun encode(value: T, destination: Buffer) {
        for ((key, childValue) in disassembler(value).entries) {
            when {
                key in core.localEncoders -> {
                    val encoder = core.localEncoders.getValue(key) as ChunkEncoder<Any>
                    val body = Buffer()
                    encoder.encode(childValue, body)
                    writeIffChunk(key, body, destination)
                }
                key in core.formEncoders -> {
                    val encoder = core.formEncoders.getValue(key) as FormBodyEncoder<Any>
                    val innerBody = Buffer()
                    encoder.encode(childValue, innerBody)
                    writeGroupChunk(IffChunkIds.FORM, key, innerBody, destination)
                }
                key in core.listEncoders -> {
                    val encoder = core.listEncoders.getValue(key) as ListBodyEncoder<Any>
                    val innerBody = Buffer()
                    encoder.encode(childValue, innerBody)
                    writeGroupChunk(IffChunkIds.LIST, key, innerBody, destination)
                }
                key in core.catEncoders -> {
                    val encoder = core.catEncoders.getValue(key) as CatBodyEncoder<Any>
                    val innerBody = Buffer()
                    encoder.encode(childValue, innerBody)
                    writeGroupChunk(IffChunkIds.CAT, key, innerBody, destination)
                }
                else -> throw RiffletEncodeException("No encoder registered for chunk type '${key.name}'")
            }
        }
    }
}
