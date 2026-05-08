package com.kelvsyc.rifflet.rifx

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.riff.RiffChunkIds

object RifxChunkIds {
    val RIFX = ChunkId("RIFX")
    val LIST = RiffChunkIds.LIST

    /** Padding chunk: content is arbitrary and must be ignored. */
    val JUNK = RiffChunkIds.JUNK

    /** Padding chunk: content is arbitrary and must be ignored. */
    val PAD = RiffChunkIds.PAD

    /** Chunk IDs that introduce a group container (RIFX or LIST). */
    val containerIds: Set<ChunkId> = setOf(RIFX, LIST)

    /** All chunk IDs that receive special treatment during parsing. */
    val reservedIds: Set<ChunkId> = setOf(RIFX, LIST, JUNK, PAD)
}
