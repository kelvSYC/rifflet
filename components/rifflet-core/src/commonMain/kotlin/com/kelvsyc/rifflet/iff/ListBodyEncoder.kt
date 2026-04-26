package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkEncoder
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RiffletEncodeException
import com.kelvsyc.rifflet.internal.iff.writeGroupChunk
import okio.Buffer

/**
 * Encodes a domain object of type [T] into the child content of a `LIST` chunk.
 *
 * This interface does not extend [ChunkEncoder] because a `LIST` body is not itself a chunk —
 * it is the content written after the outer `LIST` type and size header. [ChunkEncoder] models a
 * complete chunk (header + body); [ListBodyEncoder] models only the body.
 *
 * The [encode] method writes any `PROP` chunks followed by the child group chunks of the `LIST`
 * body — IFF-framed and appended in order — directly into [destination]. The outer `LIST` header
 * (outer type ID and body size) and the 4-byte list-type field are written by the caller.
 *
 * **Format grammar is the caller's responsibility.** The IFF spec defines the mechanism — `PROP`
 * chunks supply default local chunks for all enclosed `FORM` chunks of the matching form-type — but
 * it does not dictate which chunk types a given format places in a `PROP` versus in each individual
 * `FORM`. Callers must respect the grammar of the specific IFF-based format being written.
 *
 * Use the companion [invoke] to construct a standard implementation without `PROP` support,
 * [withProperties] to include `PROP` chunks, or [uniform] for the common case of a homogeneous
 * sequence of items all of the same group chunk type.
 */
interface ListBodyEncoder<T> {
    /**
     * Encodes [value] into [destination] as the child content of a `LIST` chunk.
     *
     * Writes all child group chunks with their IFF headers into [destination]. The caller is
     * responsible for writing the outer `LIST` type ID, the body-size header, and the 4-byte
     * list-type field.
     */
    fun encode(value: T, destination: Buffer)

    companion object {
        /**
         * Creates a [ListBodyEncoder] that disassembles a domain object using [disassembler] and
         * dispatches each child group chunk through [core].
         *
         * `LIST` chunks contain an ordered sequence of group chunks (FORM, LIST, CAT); local chunks
         * are not valid children. Each `(typeId, value)` pair from the disassembler is dispatched
         * against [core]'s form, list, and cat encoder maps (in that order).
         *
         * @param core Private encoder core used to dispatch child group chunks. Any local encoders
         *   registered in [core] are ignored; `LIST` chunks may only contain group chunks.
         * @param disassembler Breaks a domain object of type [T] into an ordered list of
         *   `(typeId, value)` pairs for the child group chunks.
         */
        operator fun <T> invoke(
            core: IffEncoderCore,
            disassembler: (T) -> List<Pair<ChunkId, Any>>,
        ): ListBodyEncoder<T> = ListBodyEncoderImpl(core, disassembler = disassembler)

        /**
         * Creates a [ListBodyEncoder] for a `LIST` chunk containing exclusively items of a single
         * group chunk type, encoded by [itemEncoder].
         *
         * Each element of the input [List] is tagged with [itemTypeId] and encoded using [itemEncoder].
         * An internal encoder core is constructed automatically; no core needs to be supplied. Use
         * this when all items in the list are the same type — for example, a sequence of `FORM TRAK`
         * chunks inside a `LIST ALBM`:
         *
         * ```kotlin
         * addListEncoder(ChunkId("ALBM"), ListBodyEncoder.uniform<Track>(ChunkId("TRAK"), trakEncoder))
         * ```
         */
        fun <T> uniform(itemTypeId: ChunkId, itemEncoder: FormBodyEncoder<T>): ListBodyEncoder<List<T>> {
            val core = IffEncoderCore.newCore { addFormEncoder(itemTypeId, itemEncoder) }
            return ListBodyEncoderImpl(core, disassembler = { items -> items.map { itemTypeId to it as Any } })
        }

        /** Variant of [uniform] for items that are themselves `LIST` chunks. */
        fun <T> uniform(itemTypeId: ChunkId, itemEncoder: ListBodyEncoder<T>): ListBodyEncoder<List<T>> {
            val core = IffEncoderCore.newCore { addListEncoder(itemTypeId, itemEncoder) }
            return ListBodyEncoderImpl(core, disassembler = { items -> items.map { itemTypeId to it as Any } })
        }

        /** Variant of [uniform] for items that are themselves `CAT ` chunks. */
        fun <T> uniform(itemTypeId: ChunkId, itemEncoder: CatBodyEncoder<T>): ListBodyEncoder<List<T>> {
            val core = IffEncoderCore.newCore { addCatEncoder(itemTypeId, itemEncoder) }
            return ListBodyEncoderImpl(core, disassembler = { items -> items.map { itemTypeId to it as Any } })
        }

        /**
         * Creates a [ListBodyEncoder] that writes `PROP` chunks before the child group chunks.
         *
         * [propertiesDisassembler] extracts a `Map<ChunkId, Any>` from the domain object, keyed by
         * form-type. For each entry, a `PROP` chunk is written using the matching encoder registered
         * in [core]'s `propEncoders` map. `PROP` chunks are written before all group chunks, as
         * required by the IFF spec.
         *
         * [disassembler] provides the ordered child group chunks, dispatched through [core]'s form,
         * list, and cat encoders exactly as with [invoke].
         */
        fun <T> withProperties(
            core: IffEncoderCore,
            propertiesDisassembler: (T) -> Map<ChunkId, Any>,
            disassembler: (T) -> List<Pair<ChunkId, Any>>,
        ): ListBodyEncoder<T> = ListBodyEncoderImpl(core, propertiesDisassembler, disassembler)
    }
}

@Suppress("UNCHECKED_CAST")
private class ListBodyEncoderImpl<T>(
    private val core: IffEncoderCore,
    private val propertiesDisassembler: (T) -> Map<ChunkId, Any> = { emptyMap() },
    private val disassembler: (T) -> List<Pair<ChunkId, Any>>,
) : ListBodyEncoder<T> {

    override fun encode(value: T, destination: Buffer) {
        for ((formType, propValue) in propertiesDisassembler(value)) {
            val encoder = core.propEncoders[formType] as? PropBodyEncoder<Any>
                ?: throw RiffletEncodeException("No PROP encoder registered for form type '${formType.name}'")
            val propBody = Buffer()
            encoder.encode(propValue, propBody)
            writeGroupChunk(IffChunkIds.PROP, formType, propBody, destination)
        }
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
