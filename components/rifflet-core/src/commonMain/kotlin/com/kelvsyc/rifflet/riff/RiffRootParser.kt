package com.kelvsyc.rifflet.riff

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.internal.riff.RiffRootParserImpl
import okio.Source

/**
 * Parses a binary RIFF source into a domain object of type [T].
 *
 * Use [newParser] to construct an instance. The [Builder] requires two things:
 * - A [Root] declaration identifying the form-type expected at the top level.
 * - A [RiffParserCore] supplying the parser for that root chunk and all nested chunks.
 *
 * If the root chunk is not a `RIFF` chunk or if its form-type does not match the declared [Root],
 * or if the core has no parser registered for the root form-type, [parse] throws
 * [RiffletParseException].
 */
interface RiffRootParser<out T> {

    /**
     * Identifies the expected top-level `RIFF` chunk.
     */
    data class Root(val type: ChunkId)

    companion object {
        fun <T> newParser(fn: Builder<T>.() -> Unit): RiffRootParser<T> =
            RiffRootParserImpl.Builder<T>().apply(fn).build()
    }

    interface Builder<T> {
        var root: Root

        fun core(core: RiffParserCore)
        fun core(fn: RiffParserCore.Builder.() -> Unit)
    }

    /**
     * Reads [source] as a single RIFF chunk tree and dispatches the root chunk to its registered
     * parser.
     *
     * @throws RiffletParseException if the root chunk is not a `RIFF` chunk, if the form-type
     *   does not match, or if no parser is registered for the root form-type.
     */
    fun parse(source: Source): T
}
