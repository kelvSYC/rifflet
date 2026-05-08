package com.kelvsyc.rifflet.riff

import com.kelvsyc.collections.ListMultimap
import com.kelvsyc.rifflet.core.ChunkEncoder
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.internal.riff.RiffEncoderCoreImpl
import okio.Buffer

/**
 * Holds the complete set of chunk encoders used within a single encoder's private context.
 *
 * Each [RiffFormBodyEncoder] and [RiffListBodyEncoder] owns exactly one [RiffEncoderCore] —
 * its private core. There is no shared or inherited core: every encoder is self-contained and
 * dispatches only against its own registered encoders.
 *
 * Use [newCore] to construct an instance, registering encoders via the [Builder].
 */
interface RiffEncoderCore {
    companion object {
        fun newCore(fn: Builder.() -> Unit): RiffEncoderCore = RiffEncoderCoreImpl.Builder().apply(fn).build()
    }

    interface Builder {
        /** Registers [encoder] for local chunks whose type ID matches [type]. */
        fun addLocalEncoder(type: ChunkId, encoder: ChunkEncoder<*>)

        /** Registers a [ChunkEncoder] for local chunks whose type ID matches [type], constructed from the given lambda. */
        fun <T> addLocalEncoder(type: ChunkId, encoder: (T, Buffer) -> Unit)

        /** Registers [encoder] for `RIFF` containers whose form-type field matches [type]. */
        fun addFormEncoder(type: ChunkId, encoder: RiffFormBodyEncoder<*>)

        /**
         * Registers a [RiffFormBodyEncoder] for `RIFF` containers whose form-type field matches
         * [type], wired to this core so nested chunks are dispatched through the same registered
         * encoders.
         */
        fun <T> addFormEncoder(type: ChunkId, disassembler: (T) -> ListMultimap<ChunkId, Any>)

        /** Registers [encoder] for `LIST` containers whose list-type field matches [type]. */
        fun addListEncoder(type: ChunkId, encoder: RiffListBodyEncoder<*>)

        /**
         * Registers a [RiffListBodyEncoder] for `LIST` containers whose list-type field matches
         * [type], wired to this core so nested chunks are dispatched through the same registered
         * encoders.
         */
        fun <T> addListEncoder(type: ChunkId, disassembler: (T) -> ListMultimap<ChunkId, Any>)
    }

    /** Encoders for `RIFF` containers, keyed by form-type field. */
    val formEncoders: Map<ChunkId, RiffFormBodyEncoder<*>>

    /** Encoders for `LIST` containers, keyed by list-type field. */
    val listEncoders: Map<ChunkId, RiffListBodyEncoder<*>>

    /** Encoders for local (non-container) chunks, keyed by chunk type ID. */
    val localEncoders: Map<ChunkId, ChunkEncoder<*>>
}
