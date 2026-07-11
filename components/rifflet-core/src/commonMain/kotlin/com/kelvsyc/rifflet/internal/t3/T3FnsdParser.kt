package com.kelvsyc.rifflet.internal.t3

import com.kelvsyc.rifflet.t3.FnsdBlock

internal object T3FnsdParser {
    fun parse(raw: T3RawBufferedBlock): FnsdBlock {
        val count = raw.data.readShortLe().toInt() and 0xFFFF
        val functionSets = buildList {
            repeat(count) {
                val nameLen = raw.data.readByte().toInt() and 0xFF
                val nameBytes = raw.data.readByteArray(nameLen.toLong())
                add(buildString { for (b in nameBytes) append((b.toInt() and 0xFF).toChar()) })
            }
        }
        return FnsdBlock(functionSets)
    }
}
