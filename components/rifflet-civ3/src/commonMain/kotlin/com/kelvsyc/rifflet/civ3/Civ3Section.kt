package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.Chunk
import com.kelvsyc.rifflet.core.ChunkId

/**
 * Base interface for all sections in a Civilization III BIC/BIX/BIQ file.
 */
sealed interface Civ3Section : Chunk {
    override val chunkId: ChunkId
}
