package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkId

/**
 * Class representing a `CAT ` chunk, one of the three standard group chunk types defined by the IFF standard.
 *
 * A `CAT ` chunk is said to be a concatenation of other group chunks, and as such consists of an ordered collection of
 * other group chunks. A "hint" value is also provided, and is often used to hint at the contents of the chunks therein.
 *
 * Under the IFF standard, it is not an error to have nested `CAT ` chunks, and as such the design of `CatChunk` allows
 * for it.
 *
 * Although it may not be obvious from a casual reading of the IFF spec, `CAT ` chunks may also contain `PROP` chunks,
 * serving the same role as they do in `LIST` chunks: supplying default sub-chunks to `FORM` chunks of a matching form
 * type within the `CAT `. All `PROP` chunks must appear before any group chunk items.
 */
data class CatChunk(val outerChunkId: ChunkId, val hint: ChunkId, val properties: Map<ChunkId, List<LocalChunk>>, val chunks: List<GroupChunk>) : GroupChunk {
    override val chunkId: ChunkId get() = hint
}
