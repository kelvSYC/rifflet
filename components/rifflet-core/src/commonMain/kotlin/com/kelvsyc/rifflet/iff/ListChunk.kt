package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkId

/**
 * Class representing a `LIST` chunk, one of the three standard group chunk types defined by the IFF standard.
 *
 * A `LIST` chunk is said to be an ordered collection of group chunks of a specific known type, like a `CAT ` chunk but
 * with stronger typing semantics. A `LIST` chunk also contains a separate list of property (`PROP`) chunks that define
 * common default "sub-chunks" among all of the `FORM` chunk items in the list.
 */
data class ListChunk(val type: ChunkId, val properties: Map<ChunkId, List<LocalChunk>>, val items: List<GroupChunk>) : GroupChunk
