package com.kelvsyc.rifflet.internal.t3

import com.kelvsyc.rifflet.t3.CppgBlock
import okio.ByteString

internal object T3CppgParser {
    private const val HEADER_SIZE = 7L  // UINT2 (2) + UINT4 (4) + UINT1 (1)

    fun parse(raw: T3RawBufferedBlock): CppgBlock {
        val poolId = raw.data.readShortLe().toInt() and 0xFFFF
        val pageIndex = raw.data.readIntLe().toUInt()
        val xorMask = raw.data.readByte().toInt() and 0xFF
        val dataSize = raw.declaredSize.toLong() - HEADER_SIZE
        val bytes = raw.data.readByteArray(dataSize)
        if (xorMask != 0) {
            for (i in bytes.indices) bytes[i] = (bytes[i].toInt() xor xorMask).toByte()
        }
        return CppgBlock(poolId, pageIndex, xorMask, ByteString.of(*bytes))
    }
}
