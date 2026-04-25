package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkId

/**
 * Holds the complete set of chunk parsers used during recursive IFF parsing.
 *
 * An [IffParserCore] is threaded through [FormParser], [ListParser], and [CatParser] so that nested
 * chunks of any group type can be dispatched to the appropriate registered parser. Chunks whose type
 * has no registered parser are left as their raw [IffChunk] representation.
 */
interface IffParserCore {
    /** Parsers for `FORM` chunks, keyed by form-type field. */
    val formParsers: Map<ChunkId, FormChunkParser<*>>

    /** Parsers for `LIST` chunks, keyed by list-type field. */
    val listParsers: Map<ChunkId, ListChunkParser<*>>

    /** Parsers for `CAT ` chunks, keyed by hint field. */
    val catParsers: Map<ChunkId, CatChunkParser<*>>

    /** Parsers for local (non-group) chunks, keyed by chunk type ID. */
    val localParsers: Map<ChunkId, LocalChunkParser<*>>
}
