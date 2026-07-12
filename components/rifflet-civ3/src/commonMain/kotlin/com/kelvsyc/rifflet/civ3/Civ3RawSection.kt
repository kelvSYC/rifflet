package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId
import okio.ByteString

/**
 * Fallback representation for any Civ3 section type not yet modeled with a dedicated type.
 *
 * @param type The section's 4-character marker.
 * @param count The section's declared item count, as read from the file.
 * @param items The section's raw items, each a length-prefixed byte string exactly as read from
 *   the file, in file order.
 */
data class Civ3RawSection(val type: ChunkId, val count: Int, val items: List<ByteString>) : Civ3Section {
    override val chunkId: ChunkId get() = type
}
