package com.kelvsyc.rifflet.internal.t3

import com.kelvsyc.rifflet.t3.McldBlock
import com.kelvsyc.rifflet.t3.McldEntry

internal object T3McldParser {
    fun parse(raw: T3RawBufferedBlock): McldBlock {
        val count = raw.data.readShortLe().toInt() and 0xFFFF
        val entries = buildList {
            repeat(count) {
                raw.data.readShortLe()                              // offset_to_next — discard
                val nameLen = raw.data.readByte().toInt() and 0xFF
                val nameBytes = raw.data.readByteArray(nameLen.toLong())
                val name = buildString { for (b in nameBytes) append((b.toInt() and 0xFF).toChar()) }
                val propCount = raw.data.readShortLe().toInt() and 0xFFFF
                raw.data.readShortLe()                              // property_record_size — discard
                val properties = List(propCount) { raw.data.readShortLe().toInt() and 0xFFFF }
                add(McldEntry(name, properties))
            }
        }
        return McldBlock(entries)
    }
}
