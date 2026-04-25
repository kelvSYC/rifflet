package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.internal.iff.IffRootParserImpl
import okio.Source

/**
 * Parses a binary IFF source into a domain object of type [T].
 *
 * Use [newParser] to construct an instance. The [Builder] requires two things:
 * - A [Root] declaration identifying which group chunk kind and type ID to expect at the top level.
 * - An [IffParserCore] supplying the parser for that root chunk and all nested chunks.
 *
 * If the root chunk does not match the declared [Root], or if the core has no parser registered
 * for the root type, [parse] throws [IllegalStateException].
 */
interface IffRootParser<out T> {

    /**
     * Identifies which group chunk at the top level of an IFF file this parser handles.
     */
    sealed interface Root {
        /** The root is a `FORM` chunk whose form-type field matches [type]. */
        data class FormRoot(val type: ChunkId) : Root

        /** The root is a `LIST` chunk whose list-type field matches [type]. */
        data class ListRoot(val type: ChunkId) : Root

        /** The root is a `CAT ` chunk whose hint field matches [hint]. */
        data class CatRoot(val hint: ChunkId) : Root
    }

    companion object {
        /**
         * Creates a new [IffRootParser] configured by [fn].
         */
        fun <T> newParser(fn: Builder<T>.() -> Unit): IffRootParser<T> =
            IffRootParserImpl.Builder<T>().apply(fn).build()
    }

    /**
     * Builder for [IffRootParser].
     *
     * Set [root] to declare the expected root chunk, then supply a parser registry via [core].
     */
    interface Builder<T> {
        /**
         * Declares the expected root chunk identity. Must be set before the builder completes.
         */
        var root: Root

        /**
         * Uses [core] as the parser registry for this root parser.
         */
        fun core(core: IffParserCore)

        /**
         * Builds a new [IffParserCore] inline and uses it as the parser registry.
         */
        fun core(fn: IffParserCore.Builder.() -> Unit)
    }

    /**
     * Reads [source] as a single IFF chunk tree and dispatches the root chunk to its registered parser.
     *
     * @throws IllegalStateException if the root chunk does not match the declared [Root], or if the
     *   core has no parser registered for the root type.
     */
    fun parse(source: Source): T
}
