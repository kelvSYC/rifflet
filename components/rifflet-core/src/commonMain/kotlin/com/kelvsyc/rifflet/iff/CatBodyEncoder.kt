package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkEncoder
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RiffletEncodeException
import com.kelvsyc.rifflet.internal.iff.writeGroupChunk
import okio.Buffer

/**
 * Encodes a domain object of type [T] into the child content of a `CAT ` chunk.
 *
 * This interface does not extend [ChunkEncoder] because a `CAT ` body is not itself a chunk —
 * it is the content written after the outer `CAT ` type and size header. [ChunkEncoder] models a
 * complete chunk (header + body); [CatBodyEncoder] models only the body.
 *
 * The [encode] method writes the child group chunks of the `CAT ` body — IFF-framed and appended in
 * order — directly into [destination]. The outer `CAT ` header (outer type ID and body size) and the
 * 4-byte hint field are written by the caller.
 *
 * PROP extraction is not part of this interface. No round-trip guarantee for PROP data is provided.
 *
 * Use the companion [invoke] to construct a standard implementation, or [uniform] for the common
 * case of a homogeneous sequence of items all of the same group chunk type.
 */
interface CatBodyEncoder<T> {
    /**
     * Encodes [value] into [destination] as the child content of a `CAT ` chunk.
     *
     * Writes all child group chunks with their IFF headers into [destination]. The caller is
     * responsible for writing the outer `CAT ` type ID, the body-size header, and the 4-byte
     * hint field.
     */
    fun encode(value: T, destination: Buffer)

    companion object {
        /**
         * Creates a [CatBodyEncoder] that disassembles a domain object using [disassembler] and
         * dispatches each child group chunk through [core].
         *
         * `CAT ` chunks contain an ordered sequence of group chunks (FORM, LIST, CAT); local chunks
         * are not valid children. Each `(typeId, value)` pair from the disassembler is dispatched
         * against [core]'s form, list, and cat encoder maps (in that order).
         *
         * @param core Private encoder core used to dispatch child group chunks. Any local encoders
         *   registered in [core] are ignored; `CAT ` chunks may only contain group chunks.
         * @param disassembler Breaks a domain object of type [T] into an ordered list of
         *   `(typeId, value)` pairs for the child group chunks.
         */
        operator fun <T> invoke(
            core: IffEncoderCore,
            disassembler: (T) -> List<Pair<ChunkId, Any>>,
        ): CatBodyEncoder<T> = CatBodyEncoderImpl(core, disassembler)

        /**
         * Creates a [CatBodyEncoder] for a `CAT ` chunk containing exclusively items of a single
         * group chunk type, encoded by [itemEncoder].
         *
         * Each element of the input [List] is tagged with [itemTypeId] and encoded using [itemEncoder].
         * An internal encoder core is constructed automatically; no core needs to be supplied. Use
         * this when all items in the cat are the same type — for example, a sequence of `FORM AIFF`
         * chunks inside a `CAT AIFF`:
         *
         * ```kotlin
         * addCatEncoder(ChunkId("AIFF"), CatBodyEncoder.uniform<AiffFile>(ChunkId("AIFF"), aiffEncoder))
         * ```
         */
        fun <T> uniform(itemTypeId: ChunkId, itemEncoder: FormBodyEncoder<T>): CatBodyEncoder<List<T>> {
            val core = IffEncoderCore.newCore { addFormEncoder(itemTypeId, itemEncoder) }
            return CatBodyEncoderImpl(core) { items -> items.map { itemTypeId to it as Any } }
        }

        /** Variant of [uniform] for items that are themselves `LIST` chunks. */
        fun <T> uniform(itemTypeId: ChunkId, itemEncoder: ListBodyEncoder<T>): CatBodyEncoder<List<T>> {
            val core = IffEncoderCore.newCore { addListEncoder(itemTypeId, itemEncoder) }
            return CatBodyEncoderImpl(core) { items -> items.map { itemTypeId to it as Any } }
        }

        /** Variant of [uniform] for items that are themselves `CAT ` chunks. */
        fun <T> uniform(itemTypeId: ChunkId, itemEncoder: CatBodyEncoder<T>): CatBodyEncoder<List<T>> {
            val core = IffEncoderCore.newCore { addCatEncoder(itemTypeId, itemEncoder) }
            return CatBodyEncoderImpl(core) { items -> items.map { itemTypeId to it as Any } }
        }
    }
}

@Suppress("UNCHECKED_CAST")
private class CatBodyEncoderImpl<T>(
    private val core: IffEncoderCore,
    private val disassembler: (T) -> List<Pair<ChunkId, Any>>,
) : CatBodyEncoder<T> {

    override fun encode(value: T, destination: Buffer) {
        for ((key, childValue) in disassembler(value)) {
            when {
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
                else -> throw RiffletEncodeException("No group encoder registered for type '${key.name}'")
            }
        }
    }
}
