package com.kelvsyc.rifflet.internal.t3

import com.kelvsyc.rifflet.t3.CpdfBlock
import com.kelvsyc.rifflet.t3.CpdfPoolEntry

internal object T3CpdfParser {
    fun parse(raw: T3RawBufferedBlock): CpdfBlock {
        val poolCount = raw.data.readShortLe().toInt() and 0xFFFF
        val pools = buildList {
            repeat(poolCount) {
                val pageCount = raw.data.readIntLe().toUInt()
                val pageSize = raw.data.readIntLe().toUInt()
                add(CpdfPoolEntry(pageCount, pageSize))
            }
        }
        return CpdfBlock(pools)
    }
}
