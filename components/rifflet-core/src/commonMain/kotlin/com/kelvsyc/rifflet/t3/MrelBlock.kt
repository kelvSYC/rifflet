package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `MREL` (multimedia resource link) block: a named table of resources stored as external
 * files rather than embedded in the image file. Debug-only per the T3 spec.
 */
data class MrelBlock(val entries: List<MrelEntry>) : T3Block {
    override val chunkId: ChunkId get() = T3BlockIds.MREL

    // Same first-match-wins semantics as MresBlock.find() — see that type's comment for the
    // reference-VM rationale (tads3/resfind.cpp). A plain linear scan, never a cached map.
    fun find(name: String): MrelEntry? = entries.firstOrNull { it.name.equals(name, ignoreCase = true) }
}
