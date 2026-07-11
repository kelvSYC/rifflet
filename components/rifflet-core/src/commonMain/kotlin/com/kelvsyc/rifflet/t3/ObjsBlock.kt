package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.core.ChunkId

data class ObjsBlock(
    val metaclassIndex: Int,
    val isTransient: Boolean,
    val objects: List<ObjsObject>,
) : T3Block {
    override val chunkId: ChunkId get() = T3BlockIds.OBJS
}
