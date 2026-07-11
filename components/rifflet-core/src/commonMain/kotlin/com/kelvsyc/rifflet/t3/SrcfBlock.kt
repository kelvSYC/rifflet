package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.core.ChunkId

data class SrcfBlock(val fileRecords: List<SrcfFileRecord>) : T3Block {
    override val chunkId: ChunkId get() = T3BlockIds.SRCF
}
