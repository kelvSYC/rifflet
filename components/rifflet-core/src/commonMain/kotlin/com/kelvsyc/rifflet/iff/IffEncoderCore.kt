package com.kelvsyc.rifflet.iff

import com.kelvsyc.collections.ListMultimap
import com.kelvsyc.rifflet.core.ChunkEncoder
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.internal.iff.IffEncoderCoreImpl
import okio.Buffer

/**
 * Holds the complete set of chunk encoders used within a single encoder's private context.
 *
 * Each [FormBodyEncoder], [ListBodyEncoder], and [CatBodyEncoder] owns exactly one [IffEncoderCore]
 * — its private core. There is no shared or inherited core: every encoder is self-contained and
 * dispatches only against its own registered encoders. This mirrors the parse-side [IffParserCore]
 * model and allows two different `FORM` chunks with the same local child type IDs to register
 * independent encoders without conflict.
 *
 * **Variant outer IDs are not supported.** Encoders are registered and dispatched by the inner type
 * or hint field only. All group chunks are written with their standard outer IDs (`FORM`, `LIST`,
 * `CAT `); there is no mechanism to select a variant outer ID (`FOR1`–`FOR9`, `LIS1`–`LIS9`,
 * `CAT1`–`CAT9`) when encoding. This is intentional until a concrete format requiring variant outer
 * IDs is identified.
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
     *   [FormBodyEncoder], [ListBodyEncoder], or [CatBodyEncoder] automatically, wired to the same
     *   core being built. This overload covers the common case where child-chunk dispatch should be
     *   handled by the core's own registered encoders.
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
        fun addFormEncoder(type: ChunkId, encoder: FormBodyEncoder<*>)

        /**
         * Registers a [FormBodyEncoder] for `FORM` chunks whose form-type field matches [type],
         * constructed from the given disassembler lambda. The encoder is wired to this core so
         * nested chunks are dispatched through the same registered encoders.
         * The chunk is always written with outer ID `FORM`; variant outer IDs are not supported.
         */
        fun <T> addFormEncoder(type: ChunkId, disassembler: (T) -> ListMultimap<ChunkId, Any>)

        /**
         * Registers [encoder] for `LIST` chunks whose list-type field matches [type].
         * The chunk is always written with outer ID `LIST`; variant outer IDs are not supported.
         */
        fun addListEncoder(type: ChunkId, encoder: ListBodyEncoder<*>)

        /**
         * Registers a [ListBodyEncoder] for `LIST` chunks whose list-type field matches [type],
         * constructed from the given disassembler lambda. The encoder is wired to this core so
         * nested chunks are dispatched through the same registered encoders.
         * The chunk is always written with outer ID `LIST`; variant outer IDs are not supported.
         */
        fun <T> addListEncoder(type: ChunkId, disassembler: (T) -> List<Pair<ChunkId, Any>>)

        /**
         * Registers a [ListBodyEncoder] for `LIST` chunks whose list-type field matches [type],
         * with `PROP` support. [propertiesDisassembler] extracts the per-form-type property values;
         * [disassembler] extracts the child group chunks. Both are wired to this core.
         * The chunk is always written with outer ID `LIST`; variant outer IDs are not supported.
         */
        fun <T> addListEncoder(
            type: ChunkId,
            propertiesDisassembler: (T) -> Map<ChunkId, Any>,
            disassembler: (T) -> List<Pair<ChunkId, Any>>,
        )

        /**
         * Registers [encoder] for `CAT ` chunks whose hint field matches [type].
         * The chunk is always written with outer ID `CAT `; variant outer IDs are not supported.
         */
        fun addCatEncoder(type: ChunkId, encoder: CatBodyEncoder<*>)

        /**
         * Registers a [CatBodyEncoder] for `CAT ` chunks whose hint field matches [type],
         * constructed from the given disassembler lambda. The encoder is wired to this core so
         * nested chunks are dispatched through the same registered encoders.
         * The chunk is always written with outer ID `CAT `; variant outer IDs are not supported.
         */
        fun <T> addCatEncoder(type: ChunkId, disassembler: (T) -> List<Pair<ChunkId, Any>>)

        /**
         * Registers a [CatBodyEncoder] for `CAT ` chunks whose hint field matches [type],
         * with `PROP` support. [propertiesDisassembler] extracts the per-form-type property values;
         * [disassembler] extracts the child group chunks. Both are wired to this core.
         * The chunk is always written with outer ID `CAT `; variant outer IDs are not supported.
         */
        fun <T> addCatEncoder(
            type: ChunkId,
            propertiesDisassembler: (T) -> Map<ChunkId, Any>,
            disassembler: (T) -> List<Pair<ChunkId, Any>>,
        )

        /**
         * Registers [encoder] for `PROP` chunks whose form-type field matches [type].
         *
         * `PROP` encoders are only consulted by [ListBodyEncoder] and [CatBodyEncoder] instances
         * constructed via `withProperties`; they are ignored during `FORM` encoding.
         */
        fun addPropEncoder(type: ChunkId, encoder: PropBodyEncoder<*>)

        /**
         * Registers a [PropBodyEncoder] for `PROP` chunks whose form-type field matches [type],
         * constructed from the given disassembler lambda. The encoder is wired to this core so
         * local chunks are dispatched through the same registered local encoders.
         */
        fun <T> addPropEncoder(type: ChunkId, disassembler: (T) -> ListMultimap<ChunkId, Any>)
    }

    /** Encoders for `FORM` chunks, keyed by inner form-type field. */
    val formEncoders: Map<ChunkId, FormBodyEncoder<*>>

    /** Encoders for `LIST` chunks, keyed by inner list-type field. */
    val listEncoders: Map<ChunkId, ListBodyEncoder<*>>

    /** Encoders for `CAT ` chunks, keyed by inner hint field. */
    val catEncoders: Map<ChunkId, CatBodyEncoder<*>>

    /** Encoders for local (non-group) chunks, keyed by chunk type ID. */
    val localEncoders: Map<ChunkId, ChunkEncoder<*>>

    /** Encoders for `PROP` chunks, keyed by form-type field. */
    val propEncoders: Map<ChunkId, PropBodyEncoder<*>>
}
