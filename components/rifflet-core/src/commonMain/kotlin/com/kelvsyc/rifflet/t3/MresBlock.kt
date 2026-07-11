package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `MRES` (multimedia resource) block: a named table of embedded binary resources.
 */
data class MresBlock(val entries: List<MresEntry>) : T3Block {
    override val chunkId: ChunkId get() = T3BlockIds.MRES

    // First match wins, per the reference VM (tads3/resfind.cpp's t3_find_res):
    // duplicate names resolve to whichever occurs first in TOC/file order. A plain
    // linear scan over `entries` gets this for free from iteration order — no map,
    // no cache, nothing that could silently invert the precedence.
    fun find(name: String): MresEntry? = entries.firstOrNull { it.name.equals(name, ignoreCase = true) }
}
