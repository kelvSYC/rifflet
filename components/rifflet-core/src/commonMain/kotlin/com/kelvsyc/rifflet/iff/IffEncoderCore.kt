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
         * Registers [encoder] for `FORM` (and variant `FOR1`–`FOR9`) chunks whose form-type field matches [type].
         */
        fun addFormEncoder(type: ChunkId, encoder: FormChunkEncoder<*>)

        /**
         * Registers a [FormEncoder] for `FORM` (and variant `FOR1`–`FOR9`) chunks whose form-type field matches [type],
         * constructed from the given disassembler lambda.
         */
        fun <T> addFormEncoder(type: ChunkId, disassembler: (T) -> ListMultimap<ChunkId, Any>)

        /**
         * Registers [encoder] for `LIST` (and variant `LIS1`–`LIS9`) chunks whose list-type field matches [type].
         */
        fun addListEncoder(type: ChunkId, encoder: ListChunkEncoder<*>)

        /**
         * Registers a [ListEncoder] for `LIST` (and variant `LIS1`–`LIS9`) chunks whose list-type field matches [type],
         * constructed from the given disassembler lambda.
         */
        fun <T> addListEncoder(type: ChunkId, disassembler: (T) -> List<Any>)

        /**
         * Registers [encoder] for `CAT ` (and variant `CAT1`–`CAT9`) chunks whose hint field matches [type].
         */
        fun addCatEncoder(type: ChunkId, encoder: CatChunkEncoder<*>)

        /**
         * Registers a [CatEncoder] for `CAT ` (and variant `CAT1`–`CAT9`) chunks whose hint field matches [type],
         * constructed from the given disassembler lambda.
         */
        fun <T> addCatEncoder(type: ChunkId, disassembler: (T) -> List<Any>)
    }

    /** Encoders for `FORM` (and variant `FOR1`–`FOR9`) chunks, keyed by inner form-type field. */
    val formEncoders: Map<ChunkId, FormChunkEncoder<*>>

    /** Encoders for `LIST` (and variant `LIS1`–`LIS9`) chunks, keyed by inner list-type field. */
    val listEncoders: Map<ChunkId, ListChunkEncoder<*>>

    /** Encoders for `CAT ` (and variant `CAT1`–`CAT9`) chunks, keyed by inner hint field. */
    val catEncoders: Map<ChunkId, CatChunkEncoder<*>>

    /** Encoders for local (non-group) chunks, keyed by chunk type ID. */
    val localEncoders: Map<ChunkId, ChunkEncoder<*>>
}
