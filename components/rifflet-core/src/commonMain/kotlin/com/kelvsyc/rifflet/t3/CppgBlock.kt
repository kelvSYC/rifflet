package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.internal.core.toByteString
import okio.ByteString

data class CppgBlock(
    val poolId: Int,
    val pageIndex: UInt,
    val xorMask: Int,
    val pageData: ByteString,
) : T3Block {
    override val chunkId: ChunkId get() = T3BlockIds.CPPG

    /**
     * Returns the original on-disk bytes, reconstructed by re-applying [xorMask].
     *
     * Allocates two byte arrays (the minimum with okio's public API; [ByteString] is immutable).
     */
    fun rawPageData(): ByteString {
        if (xorMask == 0) return pageData
        val bytes = pageData.toByteArray()
        for (i in bytes.indices) bytes[i] = (bytes[i].toInt() xor xorMask).toByte()
        return bytes.toByteString()
    }
}
