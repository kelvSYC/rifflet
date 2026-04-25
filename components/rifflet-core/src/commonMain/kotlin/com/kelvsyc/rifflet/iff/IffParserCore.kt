package com.kelvsyc.rifflet.iff

import com.kelvsyc.collections.ListMultimap
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.internal.iff.IffParserCoreImpl
import okio.ByteString

/**
 * Holds the complete set of chunk parsers used during recursive IFF parsing.
 *
 * An [IffParserCore] is threaded through [FormParser], [ListParser], and [CatParser] so that nested
 * chunks of any group type can be dispatched to the appropriate registered parser. Chunks whose type
 * has no registered parser are left as their raw [IffChunk] representation.
 *
 * Use [newCore] to construct an instance, registering parsers via the [Builder].
 */
interface IffParserCore {
    companion object {
        /**
         * Creates a new [IffParserCore] configured by [fn].
         *
         * @param fn A lambda that registers parsers on the [Builder].
         */
        fun newCore(fn: Builder.() -> Unit): IffParserCore = IffParserCoreImpl.Builder().apply(fn).build()
    }

    /**
     * Builder for [IffParserCore].
     *
     * Each `add*Parser` method has two overloads:
     * - A direct overload that accepts a pre-built parser instance.
     * - A convenience overload that accepts an assembler lambda and constructs the appropriate
     *   [FormParser], [ListParser], or [CatParser] automatically, wired to the same core being
     *   built. This overload covers the common case where sub-chunk dispatch should be handled
     *   by the core's own registered parsers.
     */
    interface Builder {
        /** Registers [parser] for local chunks whose type ID matches [type]. */
        fun addLocalParser(type: ChunkId, parser: LocalChunkParser<*>)

        /**
         * Registers a [LocalChunkParser] for local chunks whose type ID matches [type], constructed
         * from the given lambda.
         */
        fun <T> addLocalParser(type: ChunkId, parser: (ByteString) -> T)

        /** Registers [parser] for `FORM` chunks whose form-type field matches [type]. */
        fun addFormParser(type: ChunkId, parser: FormChunkParser<*>)

        /**
         * Registers a [FormParser] for `FORM` chunks whose form-type field matches [type].
         * The parser is wired to this core so nested chunks are dispatched through the same
         * registered parsers.
         */
        fun <T> addFormParser(type: ChunkId, assembler: (ListMultimap<ChunkId, Any>) -> T)

        /** Registers [parser] for `LIST` chunks whose list-type field matches [type]. */
        fun addListParser(type: ChunkId, parser: ListChunkParser<*>)

        /**
         * Registers a [ListParser] for `LIST` chunks whose list-type field matches [type].
         * The parser is wired to this core so nested chunks are dispatched through the same
         * registered parsers.
         */
        fun <T> addListParser(type: ChunkId, assembler: (List<Any>) -> T)

        /** Registers [parser] for `CAT ` chunks whose hint field matches [type]. */
        fun addCatParser(type: ChunkId, parser: CatChunkParser<*>)

        /**
         * Registers a [CatParser] for `CAT ` chunks whose hint field matches [type].
         * The parser is wired to this core so nested chunks are dispatched through the same
         * registered parsers.
         */
        fun <T> addCatParser(type: ChunkId, assembler: (List<Any>) -> T)
    }

    /** Parsers for `FORM` chunks, keyed by form-type field. */
    val formParsers: Map<ChunkId, FormChunkParser<*>>

    /** Parsers for `LIST` chunks, keyed by list-type field. */
    val listParsers: Map<ChunkId, ListChunkParser<*>>

    /** Parsers for `CAT ` chunks, keyed by hint field. */
    val catParsers: Map<ChunkId, CatChunkParser<*>>

    /** Parsers for local (non-group) chunks, keyed by chunk type ID. */
    val localParsers: Map<ChunkId, LocalChunkParser<*>>
}
