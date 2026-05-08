package com.kelvsyc.rifflet.riff

import com.kelvsyc.collections.ListMultimap
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.LocalChunkParser
import com.kelvsyc.rifflet.internal.riff.RiffParserCoreImpl
import okio.ByteString

/**
 * Holds the complete set of chunk parsers used during recursive RIFF parsing.
 *
 * A [RiffParserCore] is threaded through [RiffFormParser] and [RiffListParser] so that nested
 * chunks of any container type can be dispatched to the appropriate registered parser. Chunks
 * whose type has no registered parser are left as their raw [RiffChunk] representation.
 *
 * Use [newCore] to construct an instance, registering parsers via the [Builder].
 */
interface RiffParserCore {
    companion object {
        fun newCore(fn: Builder.() -> Unit): RiffParserCore = RiffParserCoreImpl.Builder().apply(fn).build()
    }

    interface Builder {
        /** Registers [parser] for local chunks whose type ID matches [type]. */
        fun addLocalParser(type: ChunkId, parser: LocalChunkParser<*>)

        /** Registers a [LocalChunkParser] for local chunks whose type ID matches [type], constructed from the given lambda. */
        fun <T> addLocalParser(type: ChunkId, parser: (ByteString) -> T)

        /**
         * Registers [parser] for `RIFF` containers whose form-type field matches [type].
         */
        fun addFormParser(type: ChunkId, parser: RiffFormChunkParser<*>)

        /**
         * Registers a [RiffFormParser] for `RIFF` containers whose form-type field matches [type],
         * wired to this core so nested chunks are dispatched through the same registered parsers.
         */
        fun <T> addFormParser(type: ChunkId, assembler: (ListMultimap<ChunkId, Any>) -> T)

        /**
         * Registers [parser] for `LIST` containers whose list-type field matches [type].
         */
        fun addListParser(type: ChunkId, parser: RiffListChunkParser<*>)

        /**
         * Registers a [RiffListParser] for `LIST` containers whose list-type field matches [type],
         * wired to this core so nested chunks are dispatched through the same registered parsers.
         */
        fun <T> addListParser(type: ChunkId, assembler: (ListMultimap<ChunkId, Any>) -> T)
    }

    /** Parsers for `RIFF` containers, keyed by form-type field. */
    val formParsers: Map<ChunkId, RiffFormChunkParser<*>>

    /** Parsers for `LIST` containers, keyed by list-type field. */
    val listParsers: Map<ChunkId, RiffListChunkParser<*>>

    /** Parsers for local (non-container) chunks, keyed by chunk type ID. */
    val localParsers: Map<ChunkId, LocalChunkParser<*>>
}
