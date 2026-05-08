package com.kelvsyc.rifflet.riff

import com.kelvsyc.rifflet.core.ChunkId

object RiffChunkIds {
    val RIFF = ChunkId("RIFF")
    val LIST = ChunkId("LIST")

    /** Padding chunk: content is arbitrary and must be ignored. */
    val JUNK = ChunkId("JUNK")

    /** Padding chunk: content is arbitrary and must be ignored. */
    val PAD = ChunkId("PAD ")

    /** Chunk IDs that introduce a group container (RIFF or LIST). */
    val containerIds: Set<ChunkId> = setOf(RIFF, LIST)

    /** All chunk IDs that receive special treatment during parsing. */
    val reservedIds: Set<ChunkId> = setOf(RIFF, LIST, JUNK, PAD)
}
