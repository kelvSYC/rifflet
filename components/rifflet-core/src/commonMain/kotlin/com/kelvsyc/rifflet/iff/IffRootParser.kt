package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.internal.iff.IffRootParserImpl
import okio.Source

/**
 * Parses a binary IFF source into a domain object of type [T].
 *
 * Use [newParser] to construct an instance, registering a parser for each root chunk type the parser
 * should handle via the [Builder]. The root chunk type determines which registered parser is invoked:
 * [FormChunkParser] for `FORM` roots, [ListChunkParser] for `LIST` roots, and [CatChunkParser] for
 * `CAT ` roots. If the root chunk has no registered parser, [parse] throws [IllegalStateException].
 */
interface IffRootParser<out T> {
    companion object {
        /**
         * Creates a new [IffRootParser] configured by [fn].
         *
         * @param fn A lambda that registers parsers on the [Builder].
         */
        fun <T> newParser(fn: Builder<T>.() -> Unit): IffRootParser<T> {
            val builder = IffRootParserImpl.Builder<T>().apply(fn)
            return IffRootParserImpl(builder.formParsers, builder.listParsers, builder.catParsers)
        }
    }

    /**
     * Builder for [IffRootParser]. Register parsers for each root chunk type that the parser should handle.
     * At most one parser may be registered per chunk type identifier.
     */
    interface Builder<T> {
        /**
         * Registers [parser] for `FORM` chunks whose form-type field matches [type].
         */
        fun addFormParser(type: ChunkId, parser: FormChunkParser<out T>)

        /**
         * Registers [parser] for `LIST` chunks whose type field matches [type].
         *
         * Because [ListChunkParser] returns [T] directly, [T] is the assembled result of the entire
         * `LIST` chunk — not the element type of an intermediate list. Use the [assembler] in
         * [ListParser] (or a custom implementation) to convert the sequence of parsed items into [T].
         */
        fun addListParser(type: ChunkId, parser: ListChunkParser<out T>)

        /**
         * Registers [parser] for `CAT ` chunks whose hint field matches [type].
         */
        fun addCatParser(type: ChunkId, parser: CatChunkParser<out T>)
    }

    /**
     * Reads [source] as a single IFF chunk tree and dispatches the root chunk to its registered parser.
     *
     * @throws IllegalStateException if the root chunk is not a group chunk, or if no parser has been
     *   registered for the root chunk's type identifier.
     */
    fun parse(source: Source): T
}
