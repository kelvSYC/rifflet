package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkId

/**
 * Class representing a `CAT ` chunk, one of the three standard group chunk types defined by the IFF standard.
 *
 * A `CAT ` chunk is said to be a concatenation of other group chunks, and as such consists of an ordered collection of
 * other group chunks. A "hint" value is also provided, and is often used to hint at the contents of the chunks therein.
 *
 * Under the IFF standard, it is not an error to have nested `CAT ` chunks, and as such the design of `CatChunk` allows
 * for it. However, in practice, nested `CAT ` chunks are often flattened out (and the inner hint values discarded) so
 * that the chunk collection consists of `FORM` and `LIST` chunks.
 */
data class CatChunk(val hint: ChunkId, val chunks: List<GroupChunk>) : GroupChunk {
    override val chunkId: ChunkId get() = hint
}
