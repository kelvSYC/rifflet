package com.kelvsyc.rifflet.internal.t3

import com.kelvsyc.rifflet.t3.ObjsBlock
import com.kelvsyc.rifflet.t3.ObjsObject

internal object T3ObjsParser {
    private const val LARGE_FLAG = 0x01
    private const val TRANSIENT_FLAG = 0x02

    fun parse(raw: T3RawBufferedBlock): ObjsBlock {
        val objectCount = raw.data.readShortLe().toInt() and 0xFFFF
        val metaclassIndex = raw.data.readShortLe().toInt() and 0xFFFF
        val flags = raw.data.readShortLe().toInt() and 0xFFFF
        val isLarge = flags and LARGE_FLAG != 0
        val isTransient = flags and TRANSIENT_FLAG != 0
        val objects = buildList {
            repeat(objectCount) {
                val objectId = raw.data.readIntLe().toUInt()
                val dataSize = if (isLarge) {
                    raw.data.readIntLe().toLong() and 0xFFFFFFFFL
                } else {
                    (raw.data.readShortLe().toInt() and 0xFFFF).toLong()
                }
                val data = raw.data.readByteString(dataSize)
                add(ObjsObject(objectId, data))
            }
        }
        return ObjsBlock(metaclassIndex, isTransient, objects)
    }
}
