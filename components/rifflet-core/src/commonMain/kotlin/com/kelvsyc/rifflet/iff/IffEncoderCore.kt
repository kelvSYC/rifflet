package com.kelvsyc.rifflet.iff

import com.kelvsyc.collections.ListMultimap
import com.kelvsyc.rifflet.core.ChunkEncoder
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.internal.iff.IffEncoderCoreImpl
import okio.Buffer

/**
 * Holds the complete set of chunk encoders used during recursive IFF encoding.
 *
 * An [IffEncoderCore] is threaded through the encoding infrastructure so that nested chunks of any group
 * type can be dispatched to the appropriate registered encoder. The core is format-aware: it handles
 * big-endian chunk headers and even-alignment padding when serializing children.
 *
 * **Variant outer IDs are not supported.** Encoders are registered and dispatched by the inner type or
 * hint field only. All group chunks are written with their standard outer IDs (`FORM`, `LIST`, `CAT `);
 * there is no mechanism to select a variant outer ID (`FOR1`–`FOR9`, `LIS1`–`LIS9`, `CAT1`–`CAT9`) when
 * encoding. This is intentional until a concrete format requiring variant outer IDs is identified.
 *
 * Use [newCore] to construct an instance, registering encoders via the [Builder].
 */
interface IffEncoderCore {
    companion object {
        /**
         * Creates a new [IffEncoderCore] configured by [fn].
         */
        fun newCore(fn: Builder.() -> Unit): IffEncoderCore = IffEncoderCoreImpl.Builder().apply(fn).build()
    }

    /**
     * Builder for [IffEncoderCore].
     *
     * Each `add*Encoder` method has two overloads:
     * - A direct overload that accepts a pre-built encoder instance.
     * - A convenience overload that accepts a lambda and constructs the appropriate
     *   [FormEncoder], [ListEncoder], or [CatEncoder] automatically.
     */
    interface Builder {
        /** Registers [encoder] for local chunks whose type ID matches [type]. */
        fun addLocalEncoder(type: ChunkId, encoder: ChunkEncoder<*>)

        /**
         * Registers a [ChunkEncoder] for local chunks whose type ID matches [type], constructed
         * from the given lambda.
         */
        fun <T> addLocalEncoder(type: ChunkId, encoder: (T, Buffer) -> Unit)

        /**
         * Registers [encoder] for `FORM` chunks whose form-type field matches [type].
         * The chunk is always written with outer ID `FORM`; variant outer IDs are not supported.
         */
        fun addFormEncoder(type: ChunkId, encoder: FormChunkEncoder<*>)

        /**
         * Registers a [FormEncoder] for `FORM` chunks whose form-type field matches [type],
         * constructed from the given disassembler lambda.
         * The chunk is always written with outer ID `FORM`; variant outer IDs are not supported.
         */
        fun <T> addFormEncoder(type: ChunkId, disassembler: (T) -> ListMultimap<ChunkId, Any>)

        /**
         * Registers [encoder] for `LIST` chunks whose list-type field matches [type].
         * The chunk is always written with outer ID `LIST`; variant outer IDs are not supported.
         */
        fun addListEncoder(type: ChunkId, encoder: ListChunkEncoder<*>)

        /**
         * Registers a [ListEncoder] for `LIST` chunks whose list-type field matches [type],
         * constructed from the given disassembler lambda.
         * The chunk is always written with outer ID `LIST`; variant outer IDs are not supported.
         */
        fun <T> addListEncoder(type: ChunkId, disassembler: (T) -> List<Pair<ChunkId, Any>>)

        /**
         * Registers [encoder] for `CAT ` chunks whose hint field matches [type].
         * The chunk is always written with outer ID `CAT `; variant outer IDs are not supported.
         */
        fun addCatEncoder(type: ChunkId, encoder: CatChunkEncoder<*>)

        /**
         * Registers a [CatEncoder] for `CAT ` chunks whose hint field matches [type],
         * constructed from the given disassembler lambda.
         * The chunk is always written with outer ID `CAT `; variant outer IDs are not supported.
         */
        fun <T> addCatEncoder(type: ChunkId, disassembler: (T) -> List<Pair<ChunkId, Any>>)
    }

    /** Encoders for `FORM` chunks, keyed by inner form-type field. */
    val formEncoders: Map<ChunkId, FormChunkEncoder<*>>

    /** Encoders for `LIST` chunks, keyed by inner list-type field. */
    val listEncoders: Map<ChunkId, ListChunkEncoder<*>>

    /** Encoders for `CAT ` chunks, keyed by inner hint field. */
    val catEncoders: Map<ChunkId, CatChunkEncoder<*>>

    /** Encoders for local (non-group) chunks, keyed by chunk type ID. */
    val localEncoders: Map<ChunkId, ChunkEncoder<*>>
}
