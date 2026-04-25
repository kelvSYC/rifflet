package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkId

/**
 * Class representing a `LIST` chunk, one of the three standard group chunk types defined by the IFF standard.
 *
 * A `LIST` chunk is an ordered sequence of group chunks (`FORM`, `LIST`, and `CAT `). The [type] field identifies the
 * primary form type contained in the list and is used to scope [properties]; it is advisory and does not restrict the
 * actual contents.
 *
 * A `LIST` chunk may contain zero or more `PROP` chunks, each of which provides default sub-chunks for `FORM` chunks
 * of a specific form type within the list. A `PROP` applies only to `FORM` chunks whose type matches the `PROP`'s own
 * form-type field. All `PROP` chunks must appear before any group chunk items.
 */
data class ListChunk(val type: ChunkId, val properties: Map<ChunkId, List<LocalChunk>>, val items: List<GroupChunk>) : GroupChunk {
    override val chunkId: ChunkId get() = type
}
