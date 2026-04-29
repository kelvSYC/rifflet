package com.kelvsyc.rifflet.rifx

import com.kelvsyc.collections.ListMultimap
import com.kelvsyc.rifflet.core.ChunkEncoder
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.internal.rifx.RifxEncoderCoreImpl
import okio.Buffer

/**
 * Holds the complete set of chunk encoders used within a single RIFX encoder's private context.
 *
 * Each [RifxFormBodyEncoder] and [RifxListBodyEncoder] owns exactly one [RifxEncoderCore] —
 * its private core. There is no shared or inherited core: every encoder is self-contained and
 * dispatches only against its own registered encoders.
 *
 * Use [newCore] to construct an instance, registering encoders via the [Builder].
 */
interface RifxEncoderCore {
    companion object {
        fun newCore(fn: Builder.() -> Unit): RifxEncoderCore = RifxEncoderCoreImpl.Builder().apply(fn).build()
    }

    interface Builder {
        /** Registers [encoder] for local chunks whose type ID matches [type]. */
        fun addLocalEncoder(type: ChunkId, encoder: ChunkEncoder<*>)

        /** Registers a [ChunkEncoder] for local chunks whose type ID matches [type], constructed from the given lambda. */
        fun <T> addLocalEncoder(type: ChunkId, encoder: (T, Buffer) -> Unit)

        /** Registers [encoder] for `RIFX` containers whose form-type field matches [type]. */
        fun addFormEncoder(type: ChunkId, encoder: RifxFormBodyEncoder<*>)

        /**
         * Registers a [RifxFormBodyEncoder] for `RIFX` containers whose form-type field matches
         * [type], wired to this core so nested chunks are dispatched through the same registered
         * encoders.
         */
        fun <T> addFormEncoder(type: ChunkId, disassembler: (T) -> ListMultimap<ChunkId, Any>)

        /** Registers [encoder] for `LIST` containers whose list-type field matches [type]. */
        fun addListEncoder(type: ChunkId, encoder: RifxListBodyEncoder<*>)

        /**
         * Registers a [RifxListBodyEncoder] for `LIST` containers whose list-type field matches
         * [type], wired to this core so nested chunks are dispatched through the same registered
         * encoders.
         */
        fun <T> addListEncoder(type: ChunkId, disassembler: (T) -> ListMultimap<ChunkId, Any>)
    }

    /** Encoders for `RIFX` containers, keyed by form-type field. */
    val formEncoders: Map<ChunkId, RifxFormBodyEncoder<*>>

    /** Encoders for `LIST` containers, keyed by list-type field. */
    val listEncoders: Map<ChunkId, RifxListBodyEncoder<*>>

    /** Encoders for local (non-container) chunks, keyed by chunk type ID. */
    val localEncoders: Map<ChunkId, ChunkEncoder<*>>
}
